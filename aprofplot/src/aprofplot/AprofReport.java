package aprofplot;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;

public class AprofReport {

    public enum InputMetric {

        RMS, DRMS, INVALID
    }

    public enum CostMetric {

        BB, TIME, INVALID
    }

    private InputMetric input_metric = InputMetric.INVALID;
    private CostMetric cost_metric = CostMetric.INVALID;

    private String appname = null;
    private String cmdline = null;
    private int version = -1;

    private double total_cumul_cost = 0;
    private double total_self_cost = 0;
    private long total_calls = 0;
    private long total_self_syscall_input = 0;
    private long total_self_thread_input = 0;
    private boolean has_input_stats = false;

    private HashSet<String> libset = new HashSet<String>();
    private ArrayList<RoutineInfo> routines = new ArrayList<RoutineInfo>();
    private ArrayList<RoutineContext> contexts = new ArrayList<RoutineContext>();
    private HashSet<String> favorites = new HashSet<String>();
    ;
	private File file = null;

    // index 0: count of lines with zero input
    // index 1: count of lines with zero cost
    // index 2: count of incomplete lines
    // index 3: count of broken contexts
    // index 4: fatal error
    private int[] parse_errors = new int[]{0, 0, 0, 0, 0};

    private fitter fitter = null;

    public AprofReport(File f) throws Exception {

        file = f;

        if (file.length() <= 0) {
            parse_errors[4] += 1;
            System.err.println("Zero length report");
            return;
        }

        // read report file
        String str;
        StringTokenizer tokenizer;
        BufferedReader in = new BufferedReader(new FileReader(f));

        //long lines = 0;
        System.out.println("Processing of report started");
        while ((str = in.readLine()) != null) {

            //System.out.println(str);
            /*
             lines += 1;
             if (lines % 1000 == 0)
             System.out.println("Processed " + lines + " lines of the report");
             */
            tokenizer = new StringTokenizer(str);
            if (!tokenizer.hasMoreTokens()) {
                continue;
            }
            String token = tokenizer.nextToken();

            if (token.equals("v")) { // report version number
                this.version = Integer.parseInt(tokenizer.nextToken());
                continue;
            }

            if (token.equals("m")) { // performance metric type

                String m = tokenizer.nextToken();
                if (m.equals("bb_count")) {
                    this.cost_metric = CostMetric.BB;
                } else if (m.equals("time-usec")) {
                    this.cost_metric = CostMetric.TIME;
                }

                continue;
            }

            if (token.equals("i")) { // input metric type

                String m = tokenizer.nextToken();
                if (m.equals("rms")) {
                    this.input_metric = InputMetric.RMS;
                } else if (m.equals("rvms") || m.equals("drms")) {
                    this.input_metric = InputMetric.DRMS;
                }

                continue;
            }

            if (token.equals("k")) { // total cost
                this.total_cumul_cost = Double.parseDouble(tokenizer.nextToken());
                continue;
            }

            if (token.equals("f")) { // command line
                this.cmdline = str.substring(2);
                continue;
            }

            if (token.equals("a")) { // application name
                this.appname = tokenizer.nextToken();
                continue;
            }

            if (token.equals("r")) { // routine

                String rtn_name;
                String lib;
                String id;

                Scanner s = new Scanner(str);
                MatchResult result = null;
                try {
                    s.findInLine("r \"([^\"]+)\" \"([^\"]+)\" ([0-9]+)");
                    result = s.match();
                } catch (IllegalStateException e) {
                    parse_errors[4] += 1;
                    System.err.println("Invalid report format");
                    return;
                }

                rtn_name = result.group(1);
                lib = result.group(2);
                id = result.group(3);

                int rtn_id = Integer.parseInt(id);

                RoutineInfo r;
                if (contexts.isEmpty()) {
                    r = new RoutineInfo(rtn_id, rtn_name, lib);
                } else {
                    r = r = (RoutineInfo) new ContextualizedRoutineInfo(rtn_id,
                        rtn_name, lib);
                }

                while (routines.size() <= rtn_id) {
                    routines.add(null);
                }
                routines.set(rtn_id, r);

                if (!libset.contains(r.getImage())) {
                    libset.add(r.getImage());
                }

                continue;
            }

            if (token.equals("x")) { // context

                int routine_id = Integer.parseInt(tokenizer.nextToken());
                int context_id = Integer.parseInt(tokenizer.nextToken());
                int parent_id = Integer.parseInt(tokenizer.nextToken());

                RoutineInfo r = routines.get(routine_id);
                if (!(r instanceof ContextualizedRoutineInfo)) {
                    r = new ContextualizedRoutineInfo(r.getID(),
                        r.getName(),
                        r.getImage());
                    routines.set(r.getID(), r);
                }
                ContextualizedRoutineInfo rc = (ContextualizedRoutineInfo) r;
                RoutineContext c = null;

                while (contexts.size() <= context_id) {
                    contexts.add(null);
                }

                c = contexts.get(context_id);

                RoutineContext p = null;
                if (parent_id >= 0) {
                    p = contexts.get(parent_id);
                }

                if (c == null) {
                    System.err.println("Missing context: " + str);
                    parse_errors[3] += 1;
                    c = new RoutineContext();
                    contexts.set(context_id, c);
                }

                c.setParent(p);
                c.setOverallRoutine(rc);
                rc.addContext(c);

                continue;
            }

            if (token.equals("p") || token.equals("q")) { // routine point

                if (!tokenizer.hasMoreTokens()) {
                    parse_errors[2] += 1;
                    System.err.println("Incomplete line: " + str);
                    continue;
                }

                int id = Integer.parseInt(tokenizer.nextToken());

                if (!tokenizer.hasMoreTokens()) {
                    parse_errors[2] += 1;
                    System.err.println("Incomplete line: " + str);
                    continue;
                }

                long input = Long.parseLong(tokenizer.nextToken());

                double min_cumul_cost = Double.parseDouble(tokenizer.nextToken());
                double max_cumul_cost = Double.parseDouble(tokenizer.nextToken());
                double sum_cumul_cost = Double.parseDouble(tokenizer.nextToken());

                double sqr_cumul_cost = 0;
                if (this.version < 2 || this.version > 3) {
                    sqr_cumul_cost = Double.parseDouble(tokenizer.nextToken());
                }

                long occ = Long.parseLong(tokenizer.nextToken());

                double sum_cumul_real_cost = 0;
                double sum_self_cost = 0;
                if (this.version >= 2) {
                    sum_cumul_real_cost = Double.parseDouble(tokenizer.nextToken());
                    sum_self_cost = Double.parseDouble(tokenizer.nextToken());
                    total_self_cost += sum_self_cost;
                }

                double min_self_cost = 0, max_self_cost = 0;
                if (this.version >= 3) {
                    min_self_cost = Double.parseDouble(tokenizer.nextToken());
                    max_self_cost = Double.parseDouble(tokenizer.nextToken());
                }

                double sqr_self_cost = 0;
                if (this.version > 3) {
                    sqr_self_cost = Double.parseDouble(tokenizer.nextToken());
                }

                long sum_cumul_syscall = 0;
                long sum_cumul_thread = 0;
                long sum_self_syscall = 0;
                long sum_self_thread = 0;
                if (this.version >= 5 && this.input_metric == InputMetric.DRMS) {

                    // skip next (deprecated)
                    tokenizer.nextToken();
                    tokenizer.nextToken();

                    if (tokenizer.hasMoreTokens()) {

                        sum_cumul_syscall = Long.parseLong(tokenizer.nextToken());
                        sum_cumul_thread = Long.parseLong(tokenizer.nextToken());

                        has_input_stats = true;
                    }

                    if (tokenizer.hasMoreTokens()) {

                        sum_self_syscall = Long.parseLong(tokenizer.nextToken());
                        sum_self_thread = Long.parseLong(tokenizer.nextToken());

                        this.total_self_syscall_input += sum_self_syscall;
                        this.total_self_thread_input += sum_self_thread;

                    }
                }

                /*
                 if (input <= 0) {
                 System.err.println("Skipping line (zero input): " + str);
                 parse_errors[0] += 1;
                 continue;
                 }
                 */
                if (min_cumul_cost == 0 || max_cumul_cost == 0
                    || sum_cumul_cost == 0) {

                    System.err.println("Skipping line (zero cost): " + str);
                    parse_errors[1] += 1;
                    continue;
                }

                Input te = new Input(input, min_cumul_cost, max_cumul_cost,
                    sum_cumul_cost, sum_cumul_real_cost,
                    sum_self_cost, occ, min_self_cost,
                    max_self_cost, sqr_cumul_cost,
                    sqr_self_cost,
                    sum_cumul_syscall, sum_cumul_thread,
                    sum_self_syscall, sum_self_thread);

                if (token.equals("p")) {
                    RoutineInfo r = routines.get(id);
                    r.addInput(te);
                } else {

                    RoutineContext c = null;

                    try {
                        c = contexts.get(id);
                    } catch (IndexOutOfBoundsException e) {
                        while (contexts.size() <= id) {
                            contexts.add(null);
                        }
                    }

                    if (c == null) {
                        c = new RoutineContext();
                        contexts.set(id, c);
                    }

                    c.addInputTuple(te);
                }

                continue;
            }

            if (token.equals("u")) { // mangled routine

                int index = Integer.parseInt(tokenizer.nextToken());
                //System.out.println(index);
                String name = "";
                while (tokenizer.hasMoreTokens()) {
                    name += (tokenizer.nextToken() + " ");
                }
                name = name.trim();
                routines.get(index).setMangledName(name);

                continue;
            }

            if (token.equals("d")) { // old demangled routine

                int index = Integer.parseInt(tokenizer.nextToken());
                String name = "";
                while (tokenizer.hasMoreTokens()) {
                    name += (tokenizer.nextToken() + " ");
                }
                name = name.trim().replace("\"", "");
                routines.get(index).setFullName(name);

                continue;
            }

            if (token.equals("c")) { // comment
                // skip comments
                continue;
            }

            if (token.equals("fav")) { // favourite routines
                // load favourite routines list
                while (tokenizer.hasMoreTokens()) {
                    favorites.add(tokenizer.nextToken());
                }
                continue;
            }

            //throw(new Exception());
        }
        in.close();

        if (hasContexts()) {

            System.out.println("Performing merge of context tuples...");

            Iterator it = routines.iterator();
            while (it.hasNext()) {

                ContextualizedRoutineInfo r = (ContextualizedRoutineInfo) it.next();
                r.mergeInputTupleLazyList();

            }

            System.out.println("Merge of context tuples done.");
        }

        System.out.println("Collecting statistics...");
        for (int i = 0; i < routines.size(); i++) {

            if (routines.get(i) == null) {
                System.out.println("Missing routine: " + i);
            }

            if (routines.get(i).getTotalCumulativeCost() > total_cumul_cost) {
                System.out.println("Routine " + routines.get(i).getName()
                    + " has a bigger cost than report...");
                total_cumul_cost = routines.get(i).getTotalCumulativeCost();
            }
            total_calls += routines.get(i).getTotalCalls();

        }

        if (this.input_metric == InputMetric.INVALID) {
            this.input_metric = InputMetric.RMS;
        }

        System.out.println("Report processed correctly.");
    }

    public HashMap<String, Routine> getHashMapRoutines() {

        HashMap<String, Routine> h = new HashMap<String, Routine>();
        for (Routine r : routines) {
            h.put(r.getName(), r);
        }

        return h;
    }

    public void save() throws Exception {

        File tmp = new File(this.file.getParent(), "aprof-plot.tmp");
        tmp.createNewFile();
        BufferedReader in = new BufferedReader(new FileReader(this.file));
        PrintWriter out = new PrintWriter(new FileWriter(tmp));
        String str;
        while ((str = in.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(str);
            String token = tokenizer.nextToken();
            if (!token.equals("fav")) {
                out.println(str);
            }
        }
        if (!favorites.isEmpty()) {
            out.print("fav ");
            Iterator<String> iterator = favorites.iterator();
            while (iterator.hasNext()) {
                out.print(iterator.next() + " ");
            }
            out.println("");
        }
        in.close();
        out.close();
        this.file.delete();
        tmp.renameTo(this.file);
        this.file = tmp;

    }

    public String getAppName() {
        return this.appname;
    }

    public String getCommandLine() {
        return this.cmdline;
    }

    public double getTotalCost() {
        return total_cumul_cost;
    }

    public double getTotalSelfCost() {
        return total_self_cost;
    }

    public long getTotalCalls() {
        return total_calls;
    }

    public long getContextsCount() {
        return contexts.size();
    }

    public int getRoutineCount() {
        return this.routines.size();
    }

    public boolean isFavorite(String fav) {
        return favorites.contains(fav);
    }

    public void addToFavorites(String fav) {
        if (!favorites.contains(fav)) {
            favorites.add(fav);
        }
    }

    public void removeFromFavorites(String fav) {
        if (favorites.contains(fav)) {
            favorites.remove(fav);
        }
    }

    public void sortRoutinesByTotalCost() {
        Collections.sort(routines, new Comparator<RoutineInfo>() {
            @Override
            public int compare(RoutineInfo r1, RoutineInfo r2) {
                if (r1.getTotalCost() == r2.getTotalCost()) {
                    return 0;
                }
                if (r1.getTotalCost() < r2.getTotalCost()) {
                    return 1;
                }
                return -1;
            }
        });
    }

    public ArrayList<Routine> getRoutines() {
        return new ArrayList<Routine>(this.routines);
    }

    public ArrayList<String> getLibList() {
        return new ArrayList<String>(libset);
    }

    public String getName() {
        return this.file.toString();
    }

    public final boolean hasContexts() {
        return !(contexts.isEmpty());
    }

    public InputMetric getInputMetric() {
        return input_metric;
    }

    public boolean hasInputStats() {
        return has_input_stats;
    }

    public void sortRoutinesByThreadInput() {
        Collections.sort(routines, new Comparator<RoutineInfo>() {
            @Override
            public int compare(RoutineInfo r1, RoutineInfo r2) {
                if (r1.getRatioThreadInput() == r2.getRatioThreadInput()) {
                    return 0;
                }
                if ((r1.getRatioThreadInput()) < (r2.getRatioThreadInput())) {
                    return 1;
                }
                return -1;
            }
        });
    }

    public void sortRoutinesBySyscallInput() {
        Collections.sort(routines, new Comparator<RoutineInfo>() {
            @Override
            public int compare(RoutineInfo r1, RoutineInfo r2) {
                if (r1.getRatioSyscallInput() == r2.getRatioSyscallInput()) {
                    return 0;
                }
                if ((r1.getRatioSyscallInput()) < (r2.getRatioSyscallInput())) {
                    return 1;
                }
                return -1;
            }
        });
    }

    public void sortRoutinesByDynamicInput() {
        Collections.sort(routines, new Comparator<RoutineInfo>() {
            @Override
            public int compare(RoutineInfo r1, RoutineInfo r2) {
                if (r1.getRatioInducedAccesses() == r2.getRatioInducedAccesses()) {
                    return 0;
                }
                if (r1.getRatioInducedAccesses() < r2.getRatioInducedAccesses()) {
                    return 1;
                }
                return -1;
            }
        });
    }

    public long getTotalSelfSyscallInput() {
        return total_self_syscall_input;
    }

    public long getTotalSelfThreadInput() {
        return total_self_thread_input;
    }

    //@Deprecated
    public double getRatioRmsRvms() {
        return 0;
    }

    //@Deprecated
    public boolean hasDistinctRms() {
        return false;
    }

    //@Deprecated
    public void sortRoutinesByRatioTuples() {
        Collections.sort(routines, new Comparator<RoutineInfo>() {
            @Override
            public int compare(RoutineInfo r1, RoutineInfo r2) {
                if (r1.getRatioRvmsRms() == r2.getRatioRvmsRms()) {
                    return 0;
                }
                if (r1.getRatioRvmsRms() < r2.getRatioRvmsRms()) {
                    return 1;
                }
                return -1;
            }
        });
    }

    //@Deprecated
    public void sortRoutinesByExternalInput() {
        Collections.sort(routines, new Comparator<RoutineInfo>() {
            @Override
            public int compare(RoutineInfo r1, RoutineInfo r2) {
                if (r1.getRatioSumRmsRvms() == r2.getRatioSumRmsRvms()) {
                    return 0;
                }
                if ((1 - r1.getRatioSumRmsRvms()) < (1 - r2.getRatioSumRmsRvms())) {
                    return 1;
                }
                return -1;
            }
        });
    }

    public boolean hasFatalError() {
        return parse_errors[4] > 0;
    }

    public boolean hasWarnings() {
        return parse_errors[0] > 0
            || parse_errors[1] > 0
            || parse_errors[2] > 0
            || parse_errors[3] > 0;
    }

    public String getWarnings() {

        String w = "";
        if (parse_errors[0] > 0) {
            w += "Skipped " + parse_errors[0] + " zero input lines\n";
        }
        if (parse_errors[1] > 0) {
            w += "Skipped " + parse_errors[1] + " zero cost lines\n";
        }
        if (parse_errors[2] > 0) {
            w += "Skipped " + parse_errors[2] + " incomplete lines\n";
        }
        if (parse_errors[3] > 0) {
            w += "Skipped " + parse_errors[3] + " broken contexts\n";
        }

        return w;
    }

    public void addFitter(File fitlog) {
        try {
            this.fitter = new fitter(this);
        } catch (IOException ex) {
            Logger.getLogger(AprofReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Fit getFit(long id) {
        return fitter.getFit(id);
    }

    public boolean hasFitter() {
        if (fitter == null) {
            return false;
        }
        return true;
    }
}
