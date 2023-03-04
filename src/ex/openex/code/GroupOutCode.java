package ex.openex.code;

import java.util.ArrayList;

public class GroupOutCode implements OutCode{
    ArrayList<OutCode> ocs;
    public GroupOutCode(ArrayList<OutCode> bcs){
        this.ocs = bcs;
    }

    public ArrayList<OutCode> getBcs() {
        return ocs;
    }
}
