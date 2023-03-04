package ex.exvm.obj;

public class ExInt extends ExObject{
    int data;
    public ExInt(int data){
        this.data = data;
    }

    @Override
    public Type getType() {
        return Type.INT;
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
