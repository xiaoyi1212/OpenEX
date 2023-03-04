package ex.openex.binary;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

public class FunctionArea {
    List<Byte> data;
    private int index = 0;

    private int getBytes() throws EOFException {
        if(index >= data.size())throw new EOFException();
        byte b = data.get(index);
        index += 1;
        return b;
    }

    public FunctionArea(List<Byte> data){
        this.data = data;
    }

    public static class Function{
        int const_name_index;
        int const_lib_index;
        ArrayList<ByteCodeArea.ByteCodeBinary> bcbs;
        public Function(int const_name_index, int const_lib_index, ArrayList<ByteCodeArea.ByteCodeBinary> bcbs){
            this.const_lib_index = const_lib_index;
            this.const_name_index = const_name_index;
            this.bcbs = bcbs;
        }

        @Override
        public String toString() {
            return "name:"+const_name_index+"|lib:"+const_lib_index+"|"+bcbs;
        }
    }

    private Function getTask() throws EOFException{
        int name = getBytes();
        int lib = getBytes();
        int size = getBytes();

        List<Byte> b = new ArrayList<>();
        for (int i = 0; i < size; i++) b.add((byte) getBytes());
        ArrayList<ByteCodeArea.ByteCodeBinary> bcb =new ByteCodeArea(b).getTable();
        return new Function(name,lib,bcb);
    }


    public ArrayList<Function> getTable(){
        ArrayList<Function> tss = new ArrayList<>();
        Function tt;
        try {
            while ((tt = getTask()) != null) tss.add(tt);
        }catch (EOFException e){
            return tss;
        }
        return tss;
    }
}
