package ex.openex.compile.parser;

import ex.openex.code.OutCode;

import java.util.ArrayList;

public class InterruptDescriptorTable implements OutCode{
    String id;
    ArrayList<OutCode> oc;
    public InterruptDescriptorTable(String id,ArrayList<OutCode> oc){
        this.id = id;
        this.oc = oc;
    }

    public String getId() {
        return id;
    }

    public void run(){

    }
}
