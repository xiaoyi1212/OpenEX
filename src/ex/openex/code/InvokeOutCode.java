package ex.openex.code;

public class InvokeOutCode implements OutCode{
    String path;
    GroupOutCode value;
    public InvokeOutCode(String path,GroupOutCode value){
        this.path = path;
        this.value = value;
    }

    public GroupOutCode getValue() {
        return value;
    }

    public String getPath() {
        return path;
    }
}
