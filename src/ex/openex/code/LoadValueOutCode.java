package ex.openex.code;

import java.util.ArrayList;

public class LoadValueOutCode implements OutCode{
    int type; //0 global|1 local
    String name;
    String text;
    ArrayList<OutCode> bcs;
    public LoadValueOutCode(String name, String text, ArrayList<OutCode> bcs, int type){
        this.name = name;
        this.text = text;
        this.bcs = bcs;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public ArrayList<OutCode> getBcs() {
        return bcs;
    }

    public int getType() {
        return type;
    }
}
