package ex.openex.code;

public class ThrowOutCode implements OutCode{
    String name;
    public ThrowOutCode(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
