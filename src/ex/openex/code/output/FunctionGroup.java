package ex.openex.code.output;

import java.util.ArrayList;

public class FunctionGroup implements BaseCode{
    byte path;
    String lib_name;
    String fuc_name;
    ArrayList<BaseCode> bcs;

    public String getLib_name() {
        return lib_name;
    }

    public String getFuc_name() {
        return fuc_name;
    }

    public FunctionGroup(byte path, String lib_name,String fuc_name, ArrayList<BaseCode> bcs){
        this.path = path;
        this.bcs = bcs;
        this.lib_name = lib_name;
        this.fuc_name = fuc_name;
    }

    public byte getPath() {
        return path;
    }

    public ArrayList<BaseCode> getBcs() {
        return bcs;
    }

    @Override
    public String toString() {
        return "name:"+getLib_name()+"/"+getFuc_name()+"|||"+bcs;
    }

    @Override
    public byte[] eval() {
        return new byte[0];
    }

    @Override
    public byte getOpNum() {
        return 0;
    }
}
