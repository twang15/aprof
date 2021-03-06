#ifndef APROF_DATA_H
#define APROF_DATA_H

#include "hashtable/hashtable.h"

// input size metric
typedef enum input_metric_t {RMS, DRMS, INVALID} input_metric_t;

#if APROF_TOOL

// memory event
typedef enum { Event_Ir, Event_Dr, Event_Dw, Event_Dm } EventKind;
typedef struct {
    EventKind  ekind;
    IRExpr *   addr;
    Int        size;
} Event;

// type of memory access
typedef enum access_t {LOAD, STORE, MODIFY} access_t;

// Used to descriminate final exit/jump of a BB
typedef enum jump_t {

    BB_INIT,                    // head of BB, not used anymore
    CALL, RET, OTHER,           // jump within a BB, not used anymore
    BBCALL, BBRET, BBOTHER,     // final exit of a BB
    NONE                        // default value
    
} jump_t;

#if DEBUG_ALLOCATION
// Used for debugging memory usage of aprof
typedef enum alloc_type {

    BB_S,       // BB 
    RT_S,       // Routine
    FN_S,       // Function
    T_S,        // thread
    FN_NAME_S,  // Function name
    ACT_S,      // Activation
    OBJ_NAME_S, // Object name
    POOL_PAGE_S,// Pool page
    HTN_S,      // ht node
    ILT_LK_S,   // intermediate table LK
    SEG_LK_S,   // LK segment
    LK_S,       // LK
    INPUT_S,    // input tuple
    HT_S,       // Hash Table
    HTC_S,      // array ht
    CCT_S,      // CCT node
    OBJ_S,      // Object
    MANGLED_S,  // Mangled name 
    FILE_S,     // FILE
    A_NONE      // unused

} alloc_type;

#endif // DEBUG_ALLOCATION
#endif // APROF_TOOL

// an ELF object, instance shared by all threads
typedef struct Object {
    
    UWord   key;        // Unique key for this function
    void *  next;       // HT node attr
    HChar * name;       // Name of object
    HChar * filename;   // Name of file
    
} Object;

// a function, instance shared by all threads
typedef struct Function {
    
    UWord       key;        // unique key for this function
    void *      next;       // HT node attr
    HChar *     name;       // name of routine (demangled full)
    Object *    obj;        // name of library the routine belongs to
    HChar *     mangled;    // name of routine (mangled)
    Bool        discard;    // discard information about this function
    Bool        skip;       // ignore this and all its children
    
    HashTable * input_map;  // input tuples (used if single_report == True) 
    ULong       function_id;// id (used if single_report == True) 
    
    #if EXTERNAL
    ULong       total_calls;
    #endif
    
} Function;

#if APROF_TOOL

// Info about a Basic Block, instance shared by all threads 
typedef struct BB {

    UWord       key;                    // Basic block address (unique)
    void *      next;                   // HT node attr...
    UInt        instr_offset;           // length of BB (# bytes) 
    jump_t      exit;                   // jumpking of this BB
    VgSectKind  obj_section;            // ELF Object section (of the function)
    Bool        is_dl_runtime_resolve;  // Is this BB part of dl_runtime_resolve?
    Bool        is_entry;               // Is this BB first one of a function?
    Function *  fn;                     // Info about the associated function
    
} BB;

/* a code pattern is a list of tuples (start offset, length) */
struct chunk_t { int start, len; };
struct pattern {

    const char * name;
    int len;
    struct chunk_t chunk[];

};

// file descriptor
typedef struct FILE {

    Int file;                       // file descriptior ID 
    HChar fw_buffer[BUFFER_SIZE];   // buffer
    Int fw_pos;                     // buffer offset
    
} FILE;

#endif // APROF_TOOL

// Record associated with CCT node,
typedef struct CCTNode {

    struct CCTNode *    first_child;    // first child of the node in the CCT
    struct CCTNode *    next_sibling;   // next sibling of the node in the CCT
    ULong               routine_id;     // the routine id associated with this CCT node
    UInt                context_id;     // the context id associated with this CCT node
    
} CCTNode;

// Info about a routine, not shared btw threads 
typedef struct RoutineInfo {
    
    UWord       key;                // Unique key for this routine
    void *      next;               // HT node attr...
    ULong       routine_id;         // unique id for this routine
    Function *  fn;                 // Info (name, file, etc) about this routine
    UInt        recursion_pending;  // number of pending activations (> 1 means recursive)
    HashTable * input_map;          // input size tuples

} RoutineInfo;

// input size tuple
typedef struct {
    
    UWord   key;                    // hash key
    void *  next;                   // HT node value
    UWord   input_size;             // input size value
    UWord   context_id;             // context id
    ULong   calls;                  // number of calls
    
    ULong   min_cumulative_cost;    // minimum cumulative cost 
    ULong   max_cumulative_cost;    // maximum cumulative cost 
    ULong   sum_cumulative_cost;    // sum of cumulative costs
    
    ULong   min_self_cost;          // min self cost
    ULong   max_self_cost;          // max self cost
    ULong   sum_self_cost;          // sum of self costs
    
    ULong   sum_cumul_real_cost;    // total cost spent by the routine 
                                    // in calls with this input size
                                    // without considering recursive 
                                    // calls of the same function: e.g.,
                                    // t1: CALL FOO
                                    // t2: CALL FOO
                                    // t3: RET FOO
                                    // t4: RET FOO
                                    // sum of real costs is (t4 - t1)
                                    // and NOT (t4 - t1) + (t3 - t2)
  
    #if EXTERNAL
    double  sqr_cumulative_cost;    // sum of the square of cumulative costs
    double  sqr_self_cost;          // sum of the square of self costs
    #else // EXTERNAL
    ULong   sqr_cumulative_cost;    // sum of the square of cumulative costs
    ULong   sqr_self_cost;          // sum of the square of self costs
    #endif // !EXTERNAL

    #if INPUT_STATS
    ULong   sum_cumul_syscall;      // sum cumulative syscall input
    ULong   sum_cumul_thread;       // sum cumulatibe thread input
    ULong   sum_self_syscall;       // sum self syscall input
    ULong   sum_self_thread;        // sum self thread input
    #endif // INPUT_STATS

} Input;

#if APROF_TOOL

// info about an activation of a routine
typedef struct {

    ULong           start;              // time stamp at activation entry
    ULong           sum_children_cost;  // sum of cumulative children's costs
    UInt            input_size;         // (partial) input size estimation
    RoutineInfo *   routine_info;       // routine information
    UInt            activation_id;      // activation ID
    CCTNode *       node;               // CCT node 
    
    UWord           sp;                 // stack pointer value 
                                        // when entered this activation
    UWord           ret_addr;           // Expected BB addr of BB executed 
                                        // after a return of a called 
                                        // function (only meaningful if 
                                        // the function is called with Ijk_Call)

    #if INPUT_STATS
    ULong   cumul_syscall;              // cumulative syscall input
    ULong   cumul_thread;               // cumulatibe thread input
    ULong   self_syscall;               // self syscall input
    ULong   self_thread;                // self thread input
    #endif // INPUT_STATS                                
    
} Activation;

// Info about a thread
typedef struct ThreadData {

    LookupTable *   shadow_memory;      // Shadow memory (timestamps) 
    HashTable *     rtn_ht;             // routine hash table
    
    ULong           cost;               // if BB_COUNT and INSTR 
                                        // is the counter
                                        // if RTDTSC is the entry time

    UInt            stack_depth;        // stack depth
    Activation *    stack;              // activation stack
    UInt            max_stack_size;     // max stack size
    
    BB *            last_bb;            // Last executed BB
    jump_t          last_exit;          // Last "final" exit/jump of last BB

    ULong           next_routine_id;    // next routine id 
    UInt            skip;               // # active rtn with discard info
    ULong           skip_cost;          // sum skipped rtn costs
 
    UInt            next_activation_id; // next act id (RMS)
    
    CCTNode *       root;               // CCT root
    ULong           next_context_id;    // next CCT node id
    
    #if EVENTCOUNT
    ULong num_func_enter;               // number of CALLs
    ULong num_func_exit;                // number of RETs
    ULong num_read;                     // number of read ops
    ULong num_write;                    // number of write ops
    ULong num_modify;                   // number of read+write ops
    #endif // EVENTCOUNT
    
} ThreadData;

#endif // APROF_TOOL

typedef struct Runtime {
    
    /* Global variables */

    #if APROF_TOOL
    UInt            events_used;            // # memory events queued 
    Event           events[N_EVENTS];       // memory event queue
    void            (*flush_events)(IRSB *);// flush event helper
    ThreadId        current_TID;            // Thread ID of the current active thread
    ThreadData *    current_tdata;          // Thread info of current active thread
    UInt            running_threads;        // # of active threads
    ThreadData *    threads[MAX_THREADS_DEFAULT];  // Thread data
    HashTable *     bb_ht;                  // BB hash table
    jump_t          last_exit;              // Last BB jump seen
    BB *            last_bb;                // last BB seen
    #endif // APROF_TOOL
    
    HashTable *     obj_ht;                 // Object hash table
    HashTable *     fn_ht;                  // Function hash table
    ULong           binary_mtime;           // exec's modification time 
    HChar *         application;            // application name
    HChar *         cmd_line;               // command line
    
    ULong           extra_cost;                 // needed when merging reports
    CCTNode *       root;
    
    #if APROF_TOOL
    LookupTable *   global_shadow_memory;   // Global shadow memory (DRMS)
    UInt            global_counter;         // Global counter (DRMS)
    
    Addr            dl_runtime_resolve_addr;    // dl_runtime_resolve address
    Int             dl_runtime_resolve_length;  // dl_runtime_resolve length
                                                // if 1, then dl_* is stripped
    
    Bool            loaded_report;              // needed when merging reports
    UInt            next_context_id;
    #endif
    
    UInt            next_function_id;
   
    /* Some configuration parameters */
    
    // Memory resolution: we can aggregate addresses in order
    // to decrease the space required by the shadow memory.
    // - 1 => finest resolution, each byte has its timestamp
    // - 2 => every 2 bytes we have a single timestamp
    // - ...
    // Default: 4
    UInt            memory_resolution;
    
    // Input size metric
    // Default: RMS
    input_metric_t  input_metric;
    
    // Collect calling context tree
    // Default: False
    Bool            collect_CCT;
    
    #if APROF_TOOL
    // If defined, reports will be saved in this directory
    // otherwise in the cwd.
    // Default: NULL
    const HChar *   log_dir;
    
    // Use internal function CALL/RET heuristics
    // otherwise use -finstrument-functions
    // Default: True
    Bool            function_tracing;

    Bool            single_report;
    Bool            incremental_report;
    
    const HChar *   dope_fn;
    #endif
    
    #if !APROF_TOOL
    Bool            consistency;
    Bool            compare;
    Bool            merge_all;
    Bool            merge_runs;
    Bool            merge_threads;    
    Bool            sqr_cumul_overflow;
    Bool            sqr_self_overflow;
    ULong           total_real_cost;
    ULong           total_self_cost;
    ULong           total_cumul_thread;
    ULong           total_cumul_syscall;
    ULong           total_self_thread;
    ULong           total_self_syscall;
    #endif

} Runtime;

#endif // APROF_DATA_H
