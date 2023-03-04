package ex.exvm;

import ex.exvm.exe.Executor;
import ex.exvm.lib.NativeLib;
import ex.exvm.plugin.LibManager;
import ex.openex.Main;
import ex.openex.binary.FileManager;
import ex.openex.code.output.BaseCode;
import ex.openex.code.output.FunctionGroup;
import ex.openex.compile.LexToken;
import ex.openex.compile.parser.BasicParser;
import ex.openex.compile.parser.CodeOptimization;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;
import ex.exvm.obj.ExList;
import ex.exvm.obj.ExValue;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class ScriptManager {

    public static ArrayList<ExValue> values = new ArrayList<>();
    public static ArrayList<ExList> lists = new ArrayList<>();
    public static ArrayList<ScriptLoader> sls = new ArrayList<>();
    public static ArrayList<NativeLib> nls = new ArrayList<>();
    public static ArrayList<FunctionGroup> fgs = new ArrayList<>();
    public static ArrayList<String> lib_names = new ArrayList<>();

    public static int command(String args[]) throws Exception{
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> bin_names = new ArrayList<>();
        int a = 0;
        if(args.length == 0){
            System.out.println("-version :查看当前OpenEX版本\n" +
                    "-filename:<name> :编译并执行脚本\n" +
                    "-compile :编译脚本至二进制字节码文件(配合-filename:使用)\n" +
                    "-binary:<name> :加载二进制文件并执行\n" +
                    "-loadlib:<name> :装载指定第三方库");
            return 0;
        }
        for(String s:args){
            if(s.equals("-version")){
                System.out.println(Main.vm_version);
                System.out.println(Main.compile_version);
                return 0;
            }else if(s.contains("-filename:")){
                names.add(s.split(":")[1]);
            }else if(s.contains("-binary:")) {
                names.clear();
                bin_names.add(s.split(":")[1]);
            }else if(s.contains("-loadlib:")){
                lib_names.add(s.split(":")[1]);
            }else if(s.equals("-compile")){
                a = 1;
            }
        }

        if(a == 1){
            compileBinary(names);
            return 0;
        }
        if(bin_names.size() > 0) FileManager.manage(bin_names);
        if(names.size() > 0)compileFile(names);
        return 0;
    }

    private static void compileBinary(ArrayList<String> names) throws VMException {
        for(String name:names){
            LinkedList<LexToken.TokenD> tds = new LexToken(name,Main.output,1).launch();
            LinkedList<LexToken.TokenD> buf = new LinkedList<>();

            for (LexToken.TokenD td : tds) {
                if (td.getToken().equals(LexToken.Token.TEXT)) continue;
                buf.add(td);
            }
            CompileFile cm = new BasicParser(name,buf, Main.output).rust();
            CodeOptimization co = new CodeOptimization(cm);
            write(name,co);
        }
    }

    private static void write(String filename,CodeOptimization co) throws VMException {
        try{
            DataOutputStream out = new DataOutputStream(new FileOutputStream(filename.split("\\.")[0]+".ebc"));
            out.write(0xcc);
            ArrayList<BaseCode> bc = co.getOutput();
            byte size_const = 0;
            for(CodeOptimization.ConstTableTask ct:co.getCtt()) size_const += ct.getData().length;
            out.write(size_const);

            out.write((byte)0xff);
            out.write(0);

            for(CodeOptimization.ConstTableTask ct:co.getCtt())out.write(ct.getData());
            out.write((byte)0xff);
            byte size_bytecode =0;
            for(BaseCode bcc:bc)size_bytecode += bcc.eval().length;
            out.write(size_bytecode);
            for(BaseCode bcc:bc)out.write(bcc.eval());
            out.close();
        }catch (IOException io){
            io.printStackTrace();
        }
    }


    private static void compileFile(ArrayList<String> names) throws Exception{
        for(String name:names){
            LinkedList<LexToken.TokenD> tds = new LexToken(name,Main.output,1).launch();
            LinkedList<LexToken.TokenD> buf = new LinkedList<>();

            for (LexToken.TokenD td : tds) {
                if (td.getToken().equals(LexToken.Token.TEXT)) continue;
                buf.add(td);
            }
            CompileFile cm = new BasicParser(name,buf, Main.output).rust();
            CodeOptimization co = new CodeOptimization(cm);

            ScriptLoader sl = new ScriptLoader(co);
            fgs.addAll(sl.getFunctions());

            sls.add(sl);
        }

        for(String s:ScriptManager.lib_names){
            LibManager.loadLib(new File(s));
        }
        LibManager.loadPlugin();

        Executor e = new Executor(sls.get(0));
        EXThreadManager.addThread("main",e);
    }
}
