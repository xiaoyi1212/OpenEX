package ex.openex.binary.cvm;

import ex.openex.code.output.BaseCode;
import ex.openex.code.output.FunctionGroup;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FunctionWriter {
    ArrayList<FunctionGroup> fgs;
    public FunctionWriter(ArrayList<FunctionGroup> fgs){
        this.fgs = fgs;
    }

    private ArrayList<Byte> function(FunctionGroup fg){
        ArrayList<Byte> b = new ArrayList<>(),buf = new ArrayList<>();
        b.add((byte) 0xfa);
        b.add(fg.getPath());
        for(BaseCode bc:fg.getBcs()){
            buf.add(bc.eval()[0]);
            buf.add(bc.getOpNum());
        }
        b.add((byte) buf.size());
        b.addAll(buf);
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
        for(FunctionGroup fg:fgs)out.write(toArray(function(fg)));
    }
}
