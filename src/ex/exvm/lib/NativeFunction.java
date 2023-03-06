package ex.exvm.lib;

import ex.exvm.exe.IntException;
import ex.openex.code.output.InvokeCode;
import ex.openex.exception.InterruptException;
import ex.exvm.obj.ExObject;
import ex.exvm.exe.Executor;
import ex.exvm.obj.ExValue;

import java.util.ArrayList;
import java.util.EmptyStackException;

public abstract class NativeFunction {
    public abstract ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException;

    public abstract int getValueNum();
    public abstract String getName();

    public void invoke(Executor executor){
        try {
            ArrayList<ExObject> values = new ArrayList<>();

            for (int i = 0; i < getValueNum(); i++){
                ExObject obj = executor.pop();
                if(obj.getType().equals(ExObject.Type.OBJECT)) obj = ((ExValue) obj).getValue(executor);
                values.add(obj);
            }

            executor.push(exe(values, executor));
        }catch (EmptyStackException r){
           executor.getIntException().throwError(IntException.Error_Type.FUNCTION_VALUE_ERROR,"函数参数个数不匹配,需要参数为:"+getValueNum()+"个.",new InvokeCode((byte) 0),executor.executing);
        }catch (InterruptException ie){
            executor.getIntException().throwError(IntException.Error_Type.NATIVE_FUNCTION_ERROR,ie.getMessage(),new InvokeCode((byte) 0),executor.executing);
        }
    }
}
