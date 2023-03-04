package ex.exvm;

import ex.openex.code.output.BaseCode;
import ex.openex.code.output.FunctionGroup;
import ex.openex.compile.parser.CodeOptimization;
import ex.openex.exception.VMException;
import ex.exvm.obj.ExList;
import ex.exvm.obj.ExValue;

import java.util.ArrayList;

public class ScriptLoader {
    ArrayList<CodeOptimization.ConstTableTask> tts;
    ArrayList<BaseCode> bcs;
    ArrayList<ExValue> pre_values;
    ArrayList<ExList> pre_lists;

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
        lib_name = co.getFilename().split("\\.")[0];
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
