package ex.exvm.lib;

import ex.exvm.exe.Executor;
import ex.openex.exception.InterruptException;
import ex.exvm.obj.ExList;
import ex.exvm.obj.ExNull;
import ex.exvm.obj.ExObject;

import java.util.ArrayList;

public class Util implements NativeLib{
    ArrayList<NativeFunction> nfs;
    public Util(){
        nfs = new ArrayList<>();
        nfs.add(new AddList());
        nfs.add(new RemoveList());
        nfs.add(new GetList());
        nfs.add(new SetList());
    }

    @Override
    public ArrayList<NativeFunction> getFunctions() {
        return nfs;
    }

    @Override
    public String getName() {
        return "util";
    }

    private static class AddList extends NativeFunction{
        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {


            ExObject l =values.get(0);

            ExObject obj = values.get(1);
            if(l.getType().equals(ExObject.Type.LIST)){
                ((ExList)l).add(obj);
            }else throw new InterruptException("util.add_list : 传入的参数不为列表类型");
            return new ExNull();
        }

        @Override
        public int getValueNum() {
            return 2;
        }

        @Override
        public String getName() {
            return "add_list";
        }
    }
    private static class RemoveList extends NativeFunction{
        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {
            ExObject l =values.get(0);
            ExObject obj = values.get(1);
            if(l.getType().equals(ExObject.Type.LIST)){
                ((ExList)l).remove(obj);
            }else throw new InterruptException("util.remove_list : 传入的参数不为列表类型");
            return new ExNull();
        }

        @Override
        public int getValueNum() {
            return 2;
        }

        @Override
        public String getName() {
            return "remove_list";
        }
    }
    private static class GetList extends NativeFunction{

        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {
            ExObject l =values.get(0);
            ExObject obj = values.get(1);
            if(!obj.getType().equals(ExObject.Type.INT))throw new InterruptException("util.get_list : 未知的索引类型");
            if(l.getType().equals(ExObject.Type.LIST)){
                return  ((ExList)l).get(Integer.parseInt(obj.getData()));
            }else throw new InterruptException("util.get_list : 传入的参数不为列表类型");
        }

        @Override
        public int getValueNum() {
            return 2;
        }

        @Override
        public String getName() {
            return "get_list";
        }
    }
    private static class SetList extends NativeFunction{
        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {
            ExObject l =values.get(0);
            ExObject obj = values.get(1);
            ExObject obj1 = values.get(2);
            if(!obj.getType().equals(ExObject.Type.INT))throw new InterruptException("util.set_list : 未知的索引类型");
            if(l.getType().equals(ExObject.Type.LIST)){
                ((ExList)l).set(Integer.parseInt(obj.getData()),obj1);
            }else throw new InterruptException("util.set_list : 传入的参数不为列表类型");
            return new ExNull();
        }

        @Override
        public int getValueNum() {
            return 3;
        }

        @Override
        public String getName() {
            return "set_list";
        }
    }
}
