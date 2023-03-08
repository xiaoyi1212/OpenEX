package ex.exvm;

import ex.openex.code.output.BaseCode;
import ex.openex.code.output.FunctionGroup;
import ex.openex.code.output.IntCode;
import ex.openex.compile.parser.CodeOptimization;
import ex.openex.exception.VMException;
import ex.exvm.obj.ExList;
import ex.exvm.obj.ExValue;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class ScriptLoader implements Serializable {
    ArrayList<CodeOptimization.ConstTableTask> tts;
    ArrayList<BaseCode> bcs;
    ArrayList<ExValue> pre_values;
    ArrayList<ExList> pre_lists;
    ArrayList<IntCode> ints;
    String filename;

    public String getFilename(){
        return filename;
    }

    @Serial
    private static final long serialVersionUID = 1145141919L;

    ArrayList<FunctionGroup> fgs;
    String lib_name;

    public ArrayList<FunctionGroup> getFunctions() {
        return fgs;
    }

    public ScriptLoader(CodeOptimization co) throws VMException {
        bcs = co.getOutput();
        this.tts = co.getCtt();
        pre_values = new ArrayList<>();
        pre_lists = new ArrayList<>();
        fgs = co.getFunctions();
        ints = co.getInts();
        lib_name = co.getFilename().split("\\.")[0];
        this.filename = co.getFilename();
    }

    public ArrayList<IntCode> getInts() {
        return ints;
    }

    public String getLibName() {
        return lib_name;
    }

    public synchronized ArrayList<ExValue> getPreValues() {
        return pre_values;
    }

    public ArrayList<ExList> getPreLists() {
        return pre_lists;
    }

    public synchronized ArrayList<CodeOptimization.ConstTableTask> getTable() {
        return tts;
    }

    public synchronized ArrayList<BaseCode> getBcs() {
        return bcs;
    }
}
