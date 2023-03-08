package ex.openex.binary.cvm;

import ex.exvm.ScriptLoader;
import ex.openex.Main;
import ex.openex.exception.VMException;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BinaryManage {
    static ArrayList<ScriptLoader> sls = new ArrayList<>();

    public static void addLoader(ScriptLoader sl){
        sls.add(sl);
    }
    public static void launch() throws VMException {
        try {
            for (ScriptLoader sl : sls) {
                String outname = sl.getFilename().split("\\.")[0] + ".ebc";
                DataOutputStream out = new DataOutputStream(new FileOutputStream(outname));
                out.write(0xcc);
                new ConstWrite(sl.getTable()).write(out);
                out.write(0xcc);
                new FunctionWriter(sl.getFunctions()).write(out);
                out.close();
            }
        }catch (IOException io){
            throw new VMException("[BIN]:编译终止>"+io.getLocalizedMessage(), Main.output);
        }
    }
}
