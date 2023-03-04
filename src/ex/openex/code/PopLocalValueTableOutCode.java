package ex.openex.code;

import java.util.ArrayList;

public class PopLocalValueTableOutCode implements OutCode{
    String name;
    ArrayList<OutCode> bcs;

    public String getName() {
        return name;
    }

    public ArrayList<OutCode> getBcs() {
        return bcs;
    }

    public PopLocalValueTableOutCode(String name, ArrayList<OutCode> ocs){
        this.name = name;
        this.bcs = ocs;
    }
}
