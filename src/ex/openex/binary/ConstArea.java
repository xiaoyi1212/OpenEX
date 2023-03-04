package ex.openex.binary;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

public class ConstArea {
    List<Byte> data;
    private int index = 0;

    public static final int STRING = (byte) 0xab;
    public static final int DOUBLE = (byte) 0xca;
    public static final int BOOL = (byte) 0xbe;
    public static final int INTEGER = (byte) 0xcc;
    public static final int NULL = (byte) 0x01;
    public static final int VALUE_NAME = (byte) 0x0a;

    private int getBytes() throws EOFException {
        if(index >= data.size())throw new EOFException();
        byte b = data.get(index);
        index += 1;
        return b;
    }

    public static class TableTask{
        int index;
        int type;
        String data;
        public TableTask(int index,int type,String data){
            this.index = index;
            this.data = data;
            this.type = type;
        }

        @Override
        public String toString() {
            return "index:"+index+"|type:"+type+"|data:"+data;
        }
    }
    public ConstArea(List<Byte> data){
        this.data = data;
    }

    private TableTask getTask() throws EOFException{
        int type = getBytes();
        int index = getBytes();
        int size = getBytes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) sb.append((char) getBytes());
        return new TableTask(index,type,sb.toString());
    }


    public ArrayList<TableTask> getTable(){
        ArrayList<TableTask> tss = new ArrayList<>();
        TableTask tt;
        try {
            while ((tt = getTask()) != null) tss.add(tt);
        }catch (EOFException e){
            return tss;
        }
        return tss;
    }
}
