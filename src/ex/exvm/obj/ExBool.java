package ex.exvm.obj;

public class ExBool extends ExObject{
    boolean data;
    public ExBool(boolean data){
        this.data = data;
    }

    @Override
    public Type getType() {
        return Type.BOOL;
    }

    @Override
    public String getData() {
        return String.valueOf(data);
    }

    @Override
    public String toString() {
        return String.valueOf(data);
    }
}
