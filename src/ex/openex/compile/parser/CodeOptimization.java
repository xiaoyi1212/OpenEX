package ex.openex.compile.parser;

import ex.openex.code.*;
import ex.openex.code.output.*;
import ex.openex.compile.ast.AstTree;
import ex.openex.exception.VMException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class CodeOptimization {
    CompileFile cf;
    ArrayList<ConstTableTask> ctt;
    ArrayList<FunctionGroup> fgs;
    ArrayList<IntCode> ints;
    byte const_index_ax;

    public ArrayList<IntCode> getInts() {
        return ints;
    }

    public String getFilename() {
        return cf.filename;
    }

    public ArrayList<FunctionGroup> getFunctions(){
        return fgs;
    }

    public static class ConstTableTask{
        byte type;
        byte index;
        byte size;
        byte[] data;
        public static final byte STRING = (byte) 0xab;
        public static final byte DOUBLE = (byte) 0xca;
        public static final byte BOOL = (byte) 0xbe;
        public static final byte INTEGER = (byte) 0xcc;
        public static final byte NULL = (byte) 0x01;
        public static final byte VALUE_NAME = (byte) 0x0a;
        public static final byte LIST = (byte) 0xac;

        public ConstTableTask(byte index,byte type,String data){
            this.index = index;
            this.size = (byte) data.getBytes(StandardCharsets.UTF_8).length;
            this.data = data.getBytes(StandardCharsets.UTF_8);
            this.type = type;
        }

        private byte[] toArray(ArrayList<Byte> array) {
            byte[] ret = new byte[array.size()];
            for (int i = 0; i < array.size(); i++) {
                ret[i] = array.get(i);
            }
            return ret;
        }

        @Override
        public String toString() {
            return "type:"+type+"|data:"+new String(data);
        }

        public byte getType() {
            return type;
        }

        public String getExeData(){
            return new String(data);
        }

        public byte[] getData(){
            ArrayList<Byte> b = new ArrayList<>();
            b.add(type);
            b.add(index);
            b.add(size);
            for(byte bb:data)b.add(bb);
            return toArray(b);
        }
    }
    public CodeOptimization(CompileFile cf){
        this.cf = cf;
        ctt = new ArrayList<>();
        fgs = new ArrayList<>();
        const_index_ax = 0;
        this.ints = new ArrayList<>();
    }

    public ArrayList<ConstTableTask> getCtt() {
        return ctt;
    }

    private ArrayList<BaseCode> parser(ArrayList<OutCode> ocs, ArrayList<BaseCode> bcs){
        for(OutCode oc:ocs){
            if(oc instanceof InvokeOutCode){
                ctt.add(new ConstTableTask(const_index_ax,ConstTableTask.STRING,((InvokeOutCode) oc).getPath()));

                byte buf = const_index_ax;
                const_index_ax+=1;
                parser(((InvokeOutCode) oc).getValue().getBcs(), bcs);
                bcs.add(new InvokeCode(buf));
            }else if(oc instanceof PushOPStackOutCode) {
                ctt.add(new ConstTableTask(const_index_ax, ((PushOPStackOutCode) oc).getType(), ((PushOPStackOutCode) oc).getObj()));

                bcs.add(new PushCode(const_index_ax));
                const_index_ax += 1;
            }else if(oc instanceof GroupOutCode){
                parser(((GroupOutCode) oc).getBcs(),bcs);
            }else if(oc instanceof AddOutCode)bcs.add(new AddCode());
            else if(oc instanceof SubOutCode)bcs.add(new SubCode());
            else if(oc instanceof MulOutCode)bcs.add(new MulCode());
            else if(oc instanceof DivOutCode)bcs.add(new DivCode());
            else if(oc instanceof EquOutCode) bcs.add(new EquCode());
            else if(oc instanceof BigOutCode) bcs.add(new BigCode());
            else if(oc instanceof LessOutCode) bcs.add(new LessCode());
            else if(oc instanceof LessEquOutCode) bcs.add(new LessEquCode());
            else if(oc instanceof BigEquOutCode) bcs.add(new BigEquCode());
            else if(oc instanceof NotOutCode)bcs.add(new NotCode());
            else if(oc instanceof AndOutCode) bcs.add(new AndCode());
            else if(oc instanceof OrOutCode) bcs.add(new OrCode());
        }
        return bcs;
    }

    public ArrayList<BaseCode> getOutput() throws VMException {
        ArrayList<OutCode> bcs = new ArrayList<>();
        ArrayList<BaseCode> out = new ArrayList<>();
        for(AstTree tree:cf.trees) bcs.add(tree.eval(cf));

        for(OutCode oc:bcs){
            if(oc instanceof LoadLibOutCode){
                ctt.add(new ConstTableTask(const_index_ax,ConstTableTask.STRING,((LoadLibOutCode) oc).getLib()));
                out.add(new ImportCode(const_index_ax));
                const_index_ax += 1;
            }else if(oc instanceof InvokeOutCode){
                ctt.add(new ConstTableTask(const_index_ax,ConstTableTask.STRING,((InvokeOutCode) oc).getPath()));

                byte buf = const_index_ax;
                const_index_ax+=1;
                parser(((InvokeOutCode) oc).getValue().getBcs(), out);
                out.add(new InvokeCode(buf));

            }else if(oc instanceof LoadValueOutCode) {
                out.addAll(parser(((LoadValueOutCode) oc).getBcs(), new ArrayList<>()));
                ctt.add(new ConstTableTask(const_index_ax, ConstTableTask.STRING, ((LoadValueOutCode) oc).getName()));

                if (((LoadValueOutCode) oc).getType() == 1) out.add(new LoadCode(const_index_ax));
                if (((LoadValueOutCode) oc).getType() == 0) out.add(new MovCode(const_index_ax));

                const_index_ax += 1;
            }else if(oc instanceof LoadListOutCode){

                ctt.add(new ConstTableTask(const_index_ax, ConstTableTask.STRING, ((LoadListOutCode) oc).getName()));

                if (((LoadListOutCode) oc).getType() == 1) out.add(new LListCode(const_index_ax));
                if (((LoadListOutCode) oc).getType() == 0) out.add(new MListCode(const_index_ax));

                const_index_ax += 1;
            }else if(oc instanceof Function){
                ctt.add(new ConstTableTask(const_index_ax,ConstTableTask.STRING,"L"+((Function) oc).script_name+"/"+((Function) oc).function_name));

                byte name = const_index_ax;

                ArrayList<BaseCode> bc = new ArrayList<>();
                const_index_ax += 1;

                for(String s:((Function) oc).values){
                    ctt.add(new ConstTableTask(const_index_ax,ConstTableTask.STRING,s));
                    bc.add(new LoadCode(const_index_ax));
                    const_index_ax += 1;
                }

                bc.addAll(getFunctionCode(((Function) oc).ocs));
                FunctionGroup fg = new FunctionGroup(name,getFilename().split("\\.")[0],((Function) oc).function_name,bc);
                fgs.add(fg);
            }else if(oc instanceof JneOutCode){
                out.addAll(parser(((JneOutCode) oc).getBool().getBcs(),new ArrayList<>()));
                out.add(new JempCode((byte) parser(((JneOutCode) oc).getBool().getBcs(), new ArrayList<>()).size()));
                out.addAll(getFunctionCode(((JneOutCode) oc).getBlock().getBcs()));
                if(((JneOutCode) oc).getElseb()!=null)out.addAll(getFunctionCode(((JneOutCode) oc).getElseb().getBcs()));
            }else if(oc instanceof LopOutCode){
                ArrayList<BaseCode> bool = parser(((LopOutCode) oc).getBool().getBcs(), new ArrayList<>());
                ArrayList<BaseCode> block = getFunctionCode(((LopOutCode) oc).getBlock().getBcs());
                out.add(new RejmpCode((byte) bool.size(), (byte) block.size()));
                out.addAll(bool);
                out.addAll(block);
            }else if(oc instanceof PopLocalValueTableOutCode){
                out.addAll(parser(((PopLocalValueTableOutCode) oc).getBcs(),new ArrayList<>()));
                ctt.add(new ConstTableTask(const_index_ax,ConstTableTask.STRING,((PopLocalValueTableOutCode) oc).getName()));
                out.add(new ReLoadCode(const_index_ax));
                const_index_ax +=1;
            }else if(oc instanceof PushOPStackOutCode){
                ctt.add(new ConstTableTask(const_index_ax,((PushOPStackOutCode) oc).getType(),((PushOPStackOutCode) oc).getObj()));
                out.add(new PushCode(const_index_ax));

                const_index_ax+= 1;
            }else if(oc instanceof ThrowOutCode){
                ctt.add(new ConstTableTask(const_index_ax,ConstTableTask.STRING,((ThrowOutCode) oc).getName()));
                out.add(new ThrowCode(const_index_ax));
                const_index_ax += 1;
            }else if(oc instanceof InterruptDescriptorTable){
                ctt.add(new ConstTableTask(const_index_ax,ConstTableTask.STRING,((InterruptDescriptorTable) oc).id));
                ints.add(new IntCode(const_index_ax,parser(((InterruptDescriptorTable) oc).oc,new ArrayList<>())));
                const_index_ax += 1;
            }else if(oc instanceof JmpOutCode){
                out.add(new JmpCode((byte) (getFunctionCode(((GroupOutCode)((JmpOutCode) oc).getOcs()).getBcs()).size())));
            }
        }

        return out;
    }


    private ArrayList<BaseCode> getFunctionCode(ArrayList<OutCode> bcs){
        ArrayList<BaseCode> out = new ArrayList<>();
        for(OutCode oc:bcs){
            if(oc instanceof LoadLibOutCode){
                ctt.add(new ConstTableTask(const_index_ax,ConstTableTask.STRING,((LoadLibOutCode) oc).getLib()));
                out.add(new ImportCode(const_index_ax));
                const_index_ax += 1;
            }else if(oc instanceof InvokeOutCode){
                ctt.add(new ConstTableTask(const_index_ax,ConstTableTask.STRING,((InvokeOutCode) oc).getPath()));

                byte buf = const_index_ax;
                const_index_ax+=1;
                parser(((InvokeOutCode) oc).getValue().getBcs(), out);
                out.add(new InvokeCode(buf));

            }else if(oc instanceof LoadValueOutCode){
                out.addAll(parser(((LoadValueOutCode) oc).getBcs(),new ArrayList<>()));
                ctt.add(new ConstTableTask(const_index_ax,ConstTableTask.STRING,((LoadValueOutCode) oc).getName()));

                if(((LoadValueOutCode) oc).getType()==1) out.add(new LoadCode(const_index_ax));
                if(((LoadValueOutCode) oc).getType()==0) out.add(new MovCode(const_index_ax));

                const_index_ax += 1;
            }else if(oc instanceof JneOutCode){
                out.addAll(parser(((JneOutCode) oc).getBool().getBcs(),new ArrayList<>()));
                out.add(new JempCode((byte) parser(((JneOutCode) oc).getBool().getBcs(), new ArrayList<>()).size()));
                out.addAll(getFunctionCode(((JneOutCode) oc).getBlock().getBcs()));
                if(((JneOutCode) oc).getElseb()!=null)out.addAll(getFunctionCode(((JneOutCode) oc).getElseb().getBcs()));
            }else if(oc instanceof PopLocalValueTableOutCode){
                out.addAll(parser(((PopLocalValueTableOutCode) oc).getBcs(),new ArrayList<>()));
                ctt.add(new ConstTableTask(const_index_ax,ConstTableTask.STRING,((PopLocalValueTableOutCode) oc).getName()));
                out.add(new ReLoadCode(const_index_ax));
                const_index_ax +=1;
            }else if(oc instanceof PushOPStackOutCode){
                ctt.add(new ConstTableTask(const_index_ax,((PushOPStackOutCode) oc).getType(),((PushOPStackOutCode) oc).getObj()));
                out.add(new PushCode(const_index_ax));
                const_index_ax+= 1;
            }else if(oc instanceof LopOutCode){
                ArrayList<BaseCode> bool = parser(((LopOutCode) oc).getBool().getBcs(), new ArrayList<>());
                ArrayList<BaseCode> block = getFunctionCode(((LopOutCode) oc).getBlock().getBcs());
                out.add(new RejmpCode((byte) bool.size(), (byte) block.size()));
                out.addAll(bool);
                out.addAll(block);
            }else if(oc instanceof RetOutCode){
                ctt.add(new ConstTableTask(const_index_ax,(((RetOutCode) oc).getType()),((RetOutCode) oc).getObj()));
                out.add(new PushCode(const_index_ax));
                const_index_ax+= 1;
                out.add(new RetCode());
            }else if(oc instanceof ThrowOutCode){
                ctt.add(new ConstTableTask(const_index_ax,ConstTableTask.STRING,((ThrowOutCode) oc).getName()));
                out.add(new ThrowCode(const_index_ax));
                const_index_ax += 1;
            }else if(oc instanceof JmpOutCode){

                out.add(new JmpCode((byte) (getFunctionCode(((GroupOutCode)((JmpOutCode) oc).getOcs()).getBcs()).size())));
            }
        }
        return out;
    }
}
