package ex.exvm.obj;

public class ExString extends ExObject{
    String data;
    public ExString(String s){
        this.data = s;
    }

    @Override
    public Type getType() {
        return Type.STRING;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "STRING|"+data;
    }
}
