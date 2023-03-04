package ex.exvm.exe;

import ex.exvm.ScriptLoader;
import ex.exvm.ScriptManager;
import ex.openex.Main;
import ex.openex.code.output.FunctionGroup;
import ex.openex.code.output.InvokeCode;
import ex.openex.exception.InterruptException;

public class EXThread {
    Executor executor;
    String name;
    Status status;
    String function;
    public enum Status{
        DEATH,INT,RUNNING,LOADING
    }
    public EXThread(Executor executor,String name){
        this.executor = executor;
        this.name = name;
        this.status = Status.LOADING;
        this.function = null;
    }
    public EXThread(String name,String function){
        this.name = name;
        this.executor = null;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }
    public void  shutdownHook(){
        if(executor == null){
            boolean isfuc = true;
            executor = new Executor(null);
            for (FunctionGroup fg : ScriptManager.fgs) {
                String path_d = function;
                Executor executor1 = new Executor(null);
                if(!path_d.contains("/"))executor1.getIntException().throwError(IntException.Error_Type.FUNCTION_PATH_ERROR,"函数调用路径不正确, 缺少'/'分隔符.",fg);
                if (path_d.toCharArray()[0] == 'L') {
                    path_d = path_d.replaceFirst("L", "");
                    String lib_d = path_d.split("/")[0];
                    String name_d = path_d.split("/")[1];
                    if (lib_d.equals(fg.getLib_name()) && name_d.equals(fg.getFuc_name())) {
                        for (ScriptLoader sl : ScriptManager.sls)
                            if (sl.getLibName().equals(fg.getLib_name())) {
                                executor1.executing = sl;
                            }
                        executor1.subExecutor(fg.getBcs(), executor1.executing, path_d);
                        isfuc = false;
                        break;
                    }
                } else executor1.getIntException().throwError(IntException.Error_Type.FUNCTION_PATH_ERROR,"函数调用路径类型不正确:" + path_d+",正确应该为 'L"+path_d+"'.",fg);
            }
            if(isfuc)executor.getIntException().throwError(IntException.Error_Type.FUNCTION_PATH_ERROR,"未找到指定路径的函数调用,尝试输入 'L库名/函数名' 进行调用.",new InvokeCode((byte) 0));
        }else {
            executor.start();
        }
    }

    public void run(){
        this.status = Status.RUNNING;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (executor == null) {
                    boolean isfuc = true;
                    for (FunctionGroup fg : ScriptManager.fgs) {
                        String path_d = function;
                        Executor executor1 = new Executor(null);
                        if (!path_d.contains("/"))
                            executor1.getIntException().throwError(IntException.Error_Type.FUNCTION_PATH_ERROR, "函数调用路径不正确, 缺少'/'分隔符.", fg);
                        if (path_d.toCharArray()[0] == 'L') {
                            path_d = path_d.replaceFirst("L", "");
                            String lib_d = path_d.split("/")[0];
                            String name_d = path_d.split("/")[1];
                            if (lib_d.equals(fg.getLib_name()) && name_d.equals(fg.getFuc_name())) {
                                for (ScriptLoader sl : ScriptManager.sls)
                                    if (sl.getLibName().equals(fg.getLib_name())) {
                                        executor1.executing = sl;
                                    }
                                executor1.subExecutor(fg.getBcs(), executor1.executing, path_d);
                                isfuc = false;
                                break;
                            }
                        } else
                            executor1.getIntException().throwError(IntException.Error_Type.FUNCTION_PATH_ERROR, "函数调用路径类型不正确:" + path_d + ",正确应该为 'L" + path_d + "'.", fg);
                    }
                    if (isfuc)
                        executor.getIntException().throwError(IntException.Error_Type.FUNCTION_PATH_ERROR, "未找到指定路径的函数调用,尝试输入 'L库名/函数名' 进行调用.", new InvokeCode((byte) 0));
                } else {
                    executor.start();
                }

                status = Status.DEATH;
            }
        }).start();
    }
}
