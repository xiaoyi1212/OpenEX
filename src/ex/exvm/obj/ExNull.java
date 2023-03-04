package ex.exvm.obj;

public class ExNull extends ExObject{
    @Override
    public Type getType() {
        return Type.NULL;
    }

    @Override
    public String getData() {
        return "null";
    }
}
