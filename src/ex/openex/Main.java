package ex.openex;

import ex.exvm.EXThreadManager;
import ex.exvm.ScriptManager;

import java.util.HashSet;

public class Main {

    public static final String vm_version = "OpenEXVirtualMachine-v0.5.0:"+System.getProperty("java.vm.name")+"-"+System.getProperty("java.vm.specification.version");
    public static final String compile_version = "EXScriptCompile-v0.3.2";
    public static VMOutputStream output;
    private static HashSet<String> str = new HashSet<>(){{
        add("exe");
        add("if");
        add("while");
        add("function");
        add("value");
        add("include");
        add("false");
        add("true");
        add("this");
        add("break");
        add("null");
        add("catch");
        add("global");
        add("local");
        add("return");
        add("list");
        add("throw");
        add("else");
    }};

    public static boolean isKey(String data){
        return str.contains(data);
    }



    public static void main(String[] args) throws Exception{


        /*
        args = new String[]{
              //"-binary:debug.ebc",
               "-filename:debug.exf",
                "-CVMcompile"
        };
         */


        output = new VMOutputStream();

        int i = ScriptManager.command(args);
        EXThreadManager.launch();
    }
}
