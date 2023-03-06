package ex.exvm.obj;

import ex.exvm.ScriptManager;
import ex.exvm.exe.Executor;
import ex.exvm.exe.IntException;
import ex.openex.code.output.LoadCode;

import java.util.EmptyStackException;

public class ExValue extends ExObject{
    String name;
    int type;//0 global|1 local
    ExObject value = null;
    public ExValue(String name,int type){
        this.name = name;
        this.type = type;
    }

    @Override
    public Type getType() {
        return Type.OBJECT;
    }

    public String getName() {
        return name;
    }

    public void setValue(ExObject value) {
        this.value = value;
    }

    public ExObject getValue(Executor executor){
        try {
            if(value != null)return value;

            if(type == 0) {
                for(ExValue ev: ScriptManager.values){
                    if(ev.name.equals(name))return ev;
                }
            }else if(type == 1){
                for(ExValue ev:executor.getExecuting().getPreValues()){
                    if(ev.name.equals(name))return ev;
                }
            }
            executor.getIntException().throwError(IntException.Error_Type.NOT_FOUND_VALUE_ERROR,"找不到指定变量:"+name,new LoadCode((byte) 0),executor.executing);
            return null;
        }catch (EmptyStackException e){
            executor.getIntException().throwError(IntException.Error_Type.LOAD_VALUE_ERROR,"变量值获取出现问题:"+name,new LoadCode((byte) 0),executor.executing);
            return null;
        }
    }

    @Override
    public String toString() {
        return "name:"+name+"|var:"+value;
    }

    @Override
    public String getData() {
        return name;
    }
}
