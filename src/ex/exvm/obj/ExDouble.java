package ex.exvm.obj;

public class ExDouble extends ExObject{
    double data;
    public ExDouble(double data){
        this.data = data;
    }

    @Override
    public Type getType() {
        return Type.DOUBLE;
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
