package ex.openex.binary;

import ex.openex.Util;
import ex.openex.exception.ByteCodeException;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public static ArrayList<Byte> read(String name){
        ArrayList<Byte> bs = new ArrayList<>();
        try {
            Byte buf;
            DataInputStream in = new DataInputStream(new FileInputStream(name));
            while ((buf = in.readByte()) != null) bs.add(buf);
            return bs;
        }catch (EOFException e){
            return bs;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
    public static void manage(ArrayList<String> files) throws ByteCodeException{
        for(String d:files) {
            ArrayList<Byte> bs = read(d);
            bs.remove(0);
            byte const_size = bs.get(0);

            List<Byte> b = bs.subList(1, const_size+1);
            ArrayList<ConstArea.TableTask> tt = new ConstArea(b).getTable();
            System.out.println(tt);

            System.out.println(bs);
            System.out.println((byte)0xff);
            bs = Util.removeFirst(bs,b.size()+1);


            int xaa = bs.get(1);


            if(xaa != (byte)0xff) throw new ByteCodeException("("+d+")标识符解析有误-方法区");

            byte function_size = bs.get(1);

            System.out.println(function_size);

            List<Byte> bb = bs.subList(2,function_size+2);
            ArrayList<FunctionArea.Function> ff = new FunctionArea(bb).getTable();
            System.out.println(ff);

            bs = Util.removeFirst(bs,function_size+2);

            int zaa = bs.get(0);
            if(zaa != (byte)0xff) throw new ByteCodeException("("+d+")标识符解析有误-字节码区");

            byte bytecode_size = bs.get(1);
            List<Byte> bbb = bs.subList(2,bytecode_size+2);
            ArrayList<ByteCodeArea.ByteCodeBinary> bcb = new ByteCodeArea(bbb).getTable();

            System.out.println(bcb);


        }
    }
}
