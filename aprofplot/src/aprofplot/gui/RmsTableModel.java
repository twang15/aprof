package aprofplot.gui;

import javax.swing.table.*;
import java.util.*;
import aprofplot.*;

public class RmsTableModel extends AbstractTableModel {

    private Routine rtn = null;
    private ArrayList<String> columnNames = new ArrayList<String>();
    private ArrayList<Class> columnTypes = new ArrayList<Class>();
    private MainWindow main = null;

    public RmsTableModel(MainWindow main) {
        this.main = main;
        updateColumns();
    }

    private void updateColumns() {

        columnNames = new ArrayList<String>();
        columnTypes = new ArrayList<Class>();

        if (main == null || main.isInputMetricRms()) {
            columnNames.add("RMS");
        } else {
            columnNames.add("DRMS");
        }
        columnTypes.add(Integer.class);

        if (Main.getChartCostMode() == Input.CostType.CUMULATIVE) {
            columnNames.add("min cost (cumul)");
        } else {
            columnNames.add("min cost (self)");
        }

        columnTypes.add(Double.class);

        if (Main.getChartCostMode() == Input.CostType.CUMULATIVE) {
            columnNames.add("avg cost (colum.)");
        } else {
            columnNames.add("avg cost (self)");
        }

        columnTypes.add(Double.class);

        if (Main.getChartCostMode() == Input.CostType.CUMULATIVE) {
            columnNames.add("max cost (cumul)");
        } else {
            columnNames.add("max cost (self)");
        }

        columnTypes.add(Double.class);

        columnNames.add("freq");
        columnTypes.add(Long.class);

        if (main != null && main.hasDrmsStats()) {

            columnNames.add("%Thread");
            columnTypes.add(Double.class);

            columnNames.add("%Syscall");
            columnTypes.add(Double.class);

        }
    }

    public void setData(Routine r) {
        this.rtn = r;
        //updateColumns();
        fireTableDataChanged();
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return columnTypes.get(columnIndex);
    }

    @Override
    public int getColumnCount() {
        return columnTypes.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames.get(columnIndex);
    }

    @Override
    public int getRowCount() {
        if (rtn == null) {
            return 0;
        } else {
            return rtn.getInputTuplesCount();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (rtn == null) {
            return null;
        }

        Input te = rtn.getInputTuple(rowIndex);
        switch (columnIndex) {
            case 0:
                return te.getSize();
            case 1:
                return te.getMinCost();
            case 2:
                return Math.ceil(te.getAvgCost() * 10) / 10;
            case 3:
                return te.getMaxCost();
            case 4:
                return te.getCalls();
            case 5:
                return Math.round(te.getRatioSyscallInput() * 100);
            case 6:
                return Math.round(te.getRatioThreadInput() * 100);
            //case 5: return te.getVar();
            //case 6: return rtn.getAmortizedValue(te.getRms());
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // not available
        return;
    }

    public void refreshStructure() {
        updateColumns();
        fireTableStructureChanged();
    }
}
