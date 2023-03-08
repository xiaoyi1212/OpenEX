package ex.exvm;

import ex.exvm.exe.EXThread;
import ex.exvm.exe.Executor;
import ex.exvm.plugin.LibManager;
import ex.openex.exception.InterruptException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class EXThreadManager {
    static ArrayList<EXThread> threads = new ArrayList<>();
    static HashSet<String> names = new HashSet<>();

    public static ArrayList<EXThread> getThreads() {
        return threads;
    }

    public static EXThread addThread(String name, Executor executor) throws InterruptException {
        if(names.contains(name)) throw new InterruptException("线程名重复:"+name);
        names.add(name);
        EXThread thread = new EXThread(executor,name);
        executor.setThread(thread);
        threads.add(thread);
        return thread;
    }
    public static EXThread addThread(String name,String path) throws InterruptException{
        if(names.contains(name)) throw new InterruptException("线程名重复:"+name);
        names.add(name);
        EXThread thread = new EXThread(name,path);
        threads.add(thread);
        return thread;
    }

    public static void launch() {
        new Thread(() -> {
            for(EXThread thread:threads){
                if(thread.getName().equals("main")){



                    thread.run();
                    return;
                }
            }
        }).start();
    }
}
