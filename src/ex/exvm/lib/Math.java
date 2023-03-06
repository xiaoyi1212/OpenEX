package ex.exvm.lib;

import ex.exvm.exe.Executor;
import ex.exvm.obj.ExDouble;
import ex.exvm.obj.ExInt;
import ex.exvm.obj.ExObject;
import ex.openex.exception.InterruptException;

import java.util.ArrayList;

public class Math implements NativeLib{
    ArrayList<NativeFunction> nfs;
    public Math(){
        nfs = new ArrayList<>();
        nfs.add(new Cos());
        nfs.add(new Tan());
        nfs.add(new Sin());
        nfs.add(new Sqrt());
        nfs.add(new Cbrt());
        nfs.add(new Random());
    }
    private static class Random extends NativeFunction{
        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {
            ExObject obj = values.get(0);
            if(obj.getType().equals(ExObject.Type.INT)){
                return new ExInt(new java.util.Random().nextInt(Integer.parseInt(obj.getData())));
            }else throw new InterruptException("math.random : 参数类型不匹配");
        }
        @Override
        public int getValueNum() {
            return 1;
        }
        @Override
        public String getName() {
            return "random";
        }
    }

    private static class Cos extends NativeFunction{
        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {
            ExObject obj = values.get(0);
            if(obj.getType().equals(ExObject.Type.DOUBLE)||obj.getType().equals(ExObject.Type.INT)){
                return new ExDouble(java.lang.Math.cos(Double.parseDouble(obj.getData())));
            }else throw new InterruptException("math.cos : 参数类型不匹配");
        }
        @Override
        public int getValueNum() {
            return 1;
        }
        @Override
        public String getName() {
            return "cos";
        }
    }
    private static class Sin extends NativeFunction{
        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {
            ExObject obj = values.get(0);
            if(obj.getType().equals(ExObject.Type.DOUBLE)||obj.getType().equals(ExObject.Type.INT)){
                return new ExDouble(java.lang.Math.sin(Double.parseDouble(obj.getData())));
            }else throw new InterruptException("math.sin : 参数类型不匹配");
        }
        @Override
        public int getValueNum() {
            return 1;
        }
        @Override
        public String getName() {
            return "sin";
        }
    }
    private static class Tan extends NativeFunction{
        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {
            ExObject obj = values.get(0);
            if(obj.getType().equals(ExObject.Type.DOUBLE)||obj.getType().equals(ExObject.Type.INT)){
                return new ExDouble(java.lang.Math.cos(Double.parseDouble(obj.getData())));
            }else throw new InterruptException("math.tan : 参数类型不匹配");
        }
        @Override
        public int getValueNum() {
            return 1;
        }
        @Override
        public String getName() {
            return "tan";
        }
    }
    private static class Sqrt extends NativeFunction{
        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {
            ExObject obj = values.get(0);
            if(obj.getType().equals(ExObject.Type.DOUBLE)||obj.getType().equals(ExObject.Type.INT)){
                return new ExDouble(java.lang.Math.sqrt(Double.parseDouble(obj.getData())));
            }else throw new InterruptException("math.sqrt : 参数类型不匹配");
        }
        @Override
        public int getValueNum() {
            return 1;
        }
        @Override
        public String getName() {
            return "sqrt";
        }
    }
    private static class Cbrt extends NativeFunction{
        @Override
        public ExObject exe(ArrayList<ExObject> values, Executor executor) throws InterruptException {
            ExObject obj = values.get(0);
            if(obj.getType().equals(ExObject.Type.DOUBLE)||obj.getType().equals(ExObject.Type.INT)){
                return new ExDouble(java.lang.Math.cbrt(Double.parseDouble(obj.getData())));
            }else throw new InterruptException("math.cbrt : 参数类型不匹配");
        }
        @Override
        public int getValueNum() {
            return 1;
        }
        @Override
        public String getName() {
            return "cbrt";
        }
    }

    @Override
    public ArrayList<NativeFunction> getFunctions() {
        return nfs;
    }

    @Override
    public String getName() {
        return "math";
    }
}
