package ex.exvm.exe;

import ex.exvm.EXThreadManager;
import ex.openex.Main;
import ex.openex.code.output.BaseCode;

import java.util.ArrayList;

public class IntException {
    public enum Error_Type{
        NULL_PRINT_ERROR,
        NOT_FOUND_VALUE_ERROR,
        STACK_ERROR,
        OPERATOR_TYPE_ERROR,
        NOT_FOUND_LIB,
        NOT_FOUND_FUNCTION,
        FUNCTION_PATH_ERROR,
        FUNCTION_VALUE_ERROR,
        NATIVE_FUNCTION_ERROR,
        LOAD_VALUE_ERROR
    }
    Executor executor;
    EXThread thread;

    public IntException(Executor executor){
        this.executor = executor;
        this.thread = executor.thread;
    }

    public void throwError(Error_Type type, String message, BaseCode bc){
        executor.thread.status = EXThread.Status.INT;
        StackTraceElement[] tace = Thread.currentThread().getStackTrace();
        StringBuilder info = new StringBuilder();
        for (int i = 1, taceLength = tace.length; i < taceLength; i++) {
            StackTraceElement s = tace[i];
            info.append("\t\t" + s + "\n");
        }
        Main.output.error("[Thread-"+executor.thread.getName()+"]: "+message+"\n" +
                "\tType:" +type+"\n"+
                "\tOpStack:"+executor.op_stack.size()+"\n" +
                "\tIndex:"+bc+"\n" +
                "\tInvokeStack:\n"+info);
        shutdown(-1);
    }
    public void shutdown(int i){
        Main.output.info("[ShutdownHook]:正在关闭程序...");
        ArrayList<EXThread> ets = new ArrayList<>();

        for (EXThread shut:EXThreadManager.getThreads()){
            if(shut.getName().contains("SHUTDOWN")){
                shut.shutdownHook();
            }
        }

        for(EXThread et: EXThreadManager.getThreads()){
            et.status = EXThread.Status.DEATH;
            ets.add(et);
        }
        EXThreadManager.getThreads().removeAll(ets);
        Main.output.info("[ShutdownHook]:程序已结束,退出代码:"+i);
        System.exit(i);
    }
}
