package ex.openex.binary;

import ex.exvm.EXThreadManager;
import ex.exvm.ScriptLoader;
import ex.exvm.ScriptManager;
import ex.exvm.exe.Executor;
import ex.openex.exception.ByteCodeException;
import ex.openex.exception.InterruptException;

import java.io.*;
import java.util.ArrayList;

public class FileManager {
    public static ScriptLoader read(String name) throws ByteCodeException {
        try{
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(name));
            return (ScriptLoader) in.readObject();
        }catch (IOException io){
            throw new ByteCodeException("[BYTECODE]:"+io.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            throw new ByteCodeException("[BYTECODE]: UNKNOWN CLASS FILE.");
        }
    }
    public static void manage(ArrayList<String> files) throws ByteCodeException, InterruptException {
        String main = files.get(0);
        files.remove(0);
        ScriptLoader sl = read(main);
        Executor executor = new Executor(sl);

        for(String d:files) {
            ScriptManager.sls.add(read(d));
        }
        EXThreadManager.addThread("main",executor);
    }
}
