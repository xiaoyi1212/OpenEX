package ex.openex;

import ex.exvm.EXThreadManager;
import ex.exvm.ScriptManager;

import java.util.HashSet;

public class Main {

    public static final String vm_version = "OpenEXVirtualMachine-v0.4.7:"+System.getProperty("java.vm.name")+"-"+System.getProperty("java.vm.specification.version");
    public static final String compile_version = "EXScriptCompile-v0.2.3";
    public static VMOutputStream output;
    private static HashSet<String> str = new HashSet<>(){{
        add("exe");
        add("if");
        add("while");
        add("function");
        add("value");
        add("include");
        add("false");
        add("set");
        add("true");
        add("this");
        add("break");
        add("null");
        add("catch");
        add("global");
        add("local");
        add("return");
        add("list");
    }};

    public static boolean isKey(String data){
        return str.contains(data);
    }



    public static void main(String[] args) throws Exception{

        /*
        args = new String[]{
              //"-binary:debug.ebc"
                "-filename:debug.exf",
                "-loadlib:Native.jar"
        };

         */


        output = new VMOutputStream();

        int i = ScriptManager.command(args);
        EXThreadManager.launch();
    }
}
