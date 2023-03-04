package ex.openex.exception;

import ex.openex.VMOutputStream;

public class VMException extends Exception{
    String message;
    public VMException(String message, VMOutputStream stream){
        ThreadGroup parentThread;
        int totalThread = 0;
        for (parentThread = Thread.currentThread().getThreadGroup(); parentThread
                .getParent() != null; parentThread = parentThread.getParent()) {
            totalThread = parentThread.activeCount();
        }
        int byteToMb = 1024 * 1024;
        Runtime rt = Runtime.getRuntime();
        long vmFree = rt.freeMemory() / byteToMb;
        long vmTotal = rt.totalMemory() / byteToMb;
        long vmUse = vmTotal - vmFree;

        message="EXCompile-Exception:"+message +
                "\n UsageMemory: "+vmUse+"MB\n" +
                " ThreadGroup: "+totalThread+"\n" +
                " OperatingSystem: "+System.getProperty("os.name");
        stream.error(message);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
