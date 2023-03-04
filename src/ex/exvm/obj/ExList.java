package ex.exvm.obj;

import ex.openex.exception.InterruptException;

import java.util.ArrayList;

public class ExList extends ExObject{
    ArrayList<ExObject> objs;
    ExObject type;
    String name;
    int ppp;
    public ExList(String name,int ppp){
        this.name = name;
        this.ppp = ppp;
        objs = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public boolean contains(ExObject obj){
        return objs.contains(obj);
    }

    public void add(ExObject obj) throws InterruptException {
        if(type==null)type = obj;
        if(!obj.getType().equals(type.getType()))throw new InterruptException("[LIST]传入参数类型不匹配,原参数类型为:"+type.getType());
        objs.add(obj);
    }

    public void set(int index,ExObject obj) throws InterruptException{
        if(!obj.getType().equals(type.getType()))throw new InterruptException("[LIST]传入参数类型不匹配,原参数类型为:"+type.getType());
        try{
            objs.set(index, obj);
        }catch (IndexOutOfBoundsException e){
            throw new InterruptException("[LIST]列表操作发生越界:"+e.getLocalizedMessage());
        }
    }

    public void remove(ExObject obj){
        objs.remove(obj);
    }

    public void remove(int index){
        objs.remove(index);
    }

    public ExObject get(int index){
        return objs.get(index);
    }

    @Override
    public Type getType() {
        return Type.LIST;
    }

    @Override
    public String toString() {
        return getType()+"|"+objs;
    }

    public ArrayList<ExObject> getObjs() {
        return objs;
    }

    @Override
    public String getData() {
        return name;
    }
}
