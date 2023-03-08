package ex.openex.binary.cvm;

import ex.openex.compile.parser.CodeOptimization;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ConstWrite {
    ArrayList<CodeOptimization.ConstTableTask> ctt;

    public ConstWrite(ArrayList<CodeOptimization.ConstTableTask> ctt){
        this.ctt = ctt;
    }

    private ArrayList<Byte> constTask(CodeOptimization.ConstTableTask cctt){
        ArrayList<Byte> b = new ArrayList<>();
        b.add(cctt.getIndex());
        b.add(cctt.getType());
        b.add(cctt.getSize());
        for(byte bb:cctt.getData())b.add(bb);
        return b;
    }

    private byte[] toArray(ArrayList<Byte> b){
        byte[] ret = new byte[b.size()];
        for(int i=0;i<b.size();i++){
            ret[i]=b.get(i);
        }
        return ret;
    }

    public void write(DataOutputStream out) throws IOException {
        ArrayList<Byte> ret = new ArrayList<>();
        for(CodeOptimization.ConstTableTask cttt:ctt) ret.addAll(constTask(cttt));
        out.write(ret.size());
        out.write(toArray(ret));
    }

}
