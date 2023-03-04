package ex.openex.code;

public class LoadListOutCode implements OutCode{
    int type; //0 global|1 local
    String name;
    String text;
    public LoadListOutCode(String name, String text, int type){
        this.name = name;
        this.text = text;

        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public int getType() {
        return type;
    }
}
