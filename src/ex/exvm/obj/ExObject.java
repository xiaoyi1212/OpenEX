package ex.exvm.obj;

public class ExObject {
    public enum Type{
        INT,DOUBLE,STRING,OBJECT,VOID,BOOL,NULL,LIST
    }
    private Type type;
    private String data;
    public ExObject(){
        this.type = Type.VOID;
        data = "";
    }
    public ExObject(Type type,String data){
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
