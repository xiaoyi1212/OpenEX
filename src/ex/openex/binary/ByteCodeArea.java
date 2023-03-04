package ex.openex.binary;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteCodeArea {
    List<Byte> data;
    private int index = 0;

    public static final byte PUSH = 0x0f;
    public static final byte POP = 0x0c;
    public static final byte ADD = 0x06;
    public static final byte SUB = 0x07;
    public static final byte MUL = 0x08;
    public static final byte DIV = 0x09;
    public static final byte IML = 0x12;
    public static final byte INV = 0x1a;
    public static final byte AND = 0x1f;
    public static final byte OR =  0x23;
    public static final byte NOT = 0x0e;
    public static final byte NUL = 0x0a;
    public static final byte MOV = 0x05;
    public static final byte VOID_OP_NUM = 0x00;

    public static class ByteCodeBinary{
        int opcode;
        byte[] opnum;
        public ByteCodeBinary(int opcode,byte[] opnum){
            this.opcode = opcode;
            this.opnum = opnum;
        }

        @Override
        public String toString() {
            return "opcode:"+opcode+"|opnum:"+ Arrays.toString(opnum);
        }
    }

    private int getBytes() throws EOFException {
        if(index >= data.size())throw new EOFException();
        byte b = data.get(index);
        index += 1;
        return b;
    }

    public ByteCodeArea(List<Byte> data){
        this.data = data;
    }

    private ByteCodeBinary getTask() throws EOFException {
        int opcode = getBytes();
        int size = getBytes();
        ArrayList<Byte> b = new ArrayList<>();
        for (int i = 0; i < size; i++) b.add((byte) getBytes());

        if(b.size()==0)return new ByteCodeBinary(opcode,new byte[]{VOID_OP_NUM});
        return new ByteCodeBinary(opcode,toArray(b));
    }

    private byte[] toArray(ArrayList<Byte> b){

        byte[] ret = new byte[b.size()];
        for(int i=0;i<b.size();i++){
            ret[i]=b.get(i);
        }
        return ret;
    }


    public ArrayList<ByteCodeBinary> getTable(){
        ArrayList<ByteCodeBinary> tss = new ArrayList<>();
        ByteCodeBinary tt;
        try {
            while ((tt = getTask()) != null) tss.add(tt);
        }catch (EOFException e){
            return tss;
        }
        return tss;
    }
}
