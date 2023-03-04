package ex.exvm.lib;

import ex.exvm.EXThreadManager;
import ex.exvm.exe.EXThread;
import ex.exvm.exe.Executor;
import ex.openex.Main;
import ex.openex.exception.InterruptException;
import ex.exvm.obj.*;

import java.util.ArrayList;
import java.util.Scanner;

public class Sys implements NativeLib{
    ArrayList<NativeFunction> nf;

    public Sys(){
        nf = new ArrayList<>();
        nf.add(new Print());
        nf.add(new Thread());
        nf.add(new Shutdown());
        nf.add(new Memory());
        nf.add(new Version());
        nf.add(new Input());
    }

    @Override
    public ArrayList<NativeFunction> getFunctions() {
        return nf;
    }

    @Override
    public String getName() {
        return "system";
    }

    private static class Input extends NativeFunction{

        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {
            executor.setStatus(EXThread.Status.WAIT);
            Scanner scanner = new Scanner(System.in);
            String name = scanner.nextLine();
            executor.setStatus(EXThread.Status.RUNNING);
            return new ExString(name);
        }

        @Override
        public int getValueNum() {
            return 0;
        }

        @Override
        public String getName() {
            return "input";
        }
    }

    private static class Print extends NativeFunction{
        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) {
            ExObject obj = values.get(0);
            if(obj.getType().equals(ExObject.Type.LIST)){
                System.out.println(((ExList)obj).getObjs());
                return new ExNull();
            }
            System.out.println(obj.getData());
            return new ExNull();
        }

        @Override
        public int getValueNum() {
            return 1;
        }

        @Override
        public String getName() {
            return "print";
        }
    }
    private static class Shutdown extends NativeFunction{

        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {
            ExObject obj = values.get(0);
            if(obj.getType().equals(ExObject.Type.INT)){
                executor.getIntException().shutdown(Integer.parseInt(obj.getData()));
            }else throw new InterruptException("system.shutdown : 无法将参数转换为[INT]类型.");
            return new ExNull();
        }

        @Override
        public int getValueNum() {
            return 1;
        }

        @Override
        public String getName() {
            return "shutdown";
        }
    }
    private static class Thread extends NativeFunction{
        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException{
            String thread_name = values.get(0).getData();
            String path_dd = values.get(1).getData();

            if(thread_name.contains("SHUTDOWN")){
                EXThreadManager.addThread(thread_name,path_dd);
                return new ExNull();
            }

            EXThreadManager.addThread(thread_name,path_dd).run();
            return new ExNull();
        }

        @Override
        public int getValueNum() {
            return 2;
        }

        @Override
        public String getName() {
            return "thread";
        }
    }
    private static class Memory extends NativeFunction{

        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {
            Runtime runtime = Runtime.getRuntime();
            long mb = runtime.totalMemory() - runtime.freeMemory();
            return new ExDouble(mb(mb));
        }
        private static double mb (long s) {
            return Double.parseDouble(String.format("%.2f",(double)s / (1024 * 1024)));
        }


        @Override
        public int getValueNum() {
            return 0;
        }

        @Override
        public String getName() {
            return "memory";
        }
    }
    private static class Version extends NativeFunction{

        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {
            return new ExString(Main.vm_version);
        }

        @Override
        public int getValueNum() {
            return 0;
        }

        @Override
        public String getName() {
            return "version";
        }
    }
}
