package ex.openex.compile.parser;

import ex.openex.code.OutCode;

import java.util.ArrayList;

public class Function implements OutCode{
    String script_name;
    String function_name;
    ArrayList<OutCode> ocs;
    ArrayList<String> values;

    public Function(String script_name,String function_name,ArrayList<String> values,ArrayList<OutCode> ocs){
        this.script_name = script_name;
        this.function_name = function_name;
        this.ocs = ocs;
        this.values = values;
    }
}
