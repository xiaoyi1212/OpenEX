package ex.openex.compile.ast;

import ex.openex.Main;
import ex.openex.code.GroupOutCode;
import ex.openex.code.InvokeOutCode;
import ex.openex.code.OutCode;
import ex.openex.code.PushOPStackOutCode;
import ex.openex.compile.LexToken;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;
import ex.exvm.obj.*;

import java.util.ArrayList;
import java.util.Collections;

public class AstInvokeTree extends AstLeaf{
    private ArrayList<LexToken.TokenD> tds;
    private int index = 0;

    private LexToken.TokenD getTokens(){
        if(index > tds.size())return null;
        LexToken.TokenD td = tds.get(index);
        index += 1;
        return td;
    }

    public void setTds(ArrayList<LexToken.TokenD> tds) {
        this.tds = tds;
        index = 0;
    }

    @Override
    public OutCode eval(CompileFile e) throws VMException {

        index = 0;
        String lib = null;
        String function = null;
        ArrayList<OutCode> values = new ArrayList<>();
        LexToken.TokenD td = tds.get(0);
        index += 1;

        if(!(td.getToken().equals(LexToken.Token.SEM)&&td.getData().equals(".")))throw new VMException("Unknown parser in invoke function",Main.output);td = getTokens();
        if((td.getToken().equals(LexToken.Token.KEY)||(td.getToken().equals(LexToken.Token.NAME)&&td.getData().equals("this")))) lib = td.getData();

        if(lib==null)throw new VMException("Unknown library name:"+td.getData(),Main.output);
        td = getTokens();

        if(!(td.getToken().equals(LexToken.Token.SEM)&&td.getData().equals(".")))throw new VMException("Unknown parser in invoke function",Main.output);td = getTokens();

        if(td.getToken().equals(LexToken.Token.KEY)) function = td.getData();

        if(function==null)throw new VMException("Unknown function name.",Main.output);
        td = getTokens();
        if(!(td.getToken().equals(LexToken.Token.LP)&&td.getData().equals("(")))throw new VMException("Unknown parser in invoke function",Main.output);
        td = getTokens();
        try {
            do {
                ExObject f = null;
                do {

                    if((td.getToken().equals(LexToken.Token.SEM) && td.getData().equals(","))){
                        td = getTokens();
                        continue;
                    }

                    if (td.getToken().equals(LexToken.Token.LR) && td.getData().equals(")")) break;

                    if (td.getToken().equals(LexToken.Token.STR)) f = new ExString(td.getData());
                    else if (td.getToken().equals(LexToken.Token.NUM)) f = new ExInt(Integer.parseInt(td.getData()));
                    else if (td.getToken().equals(LexToken.Token.DOUBLE))
                        f = new ExDouble(Double.parseDouble(td.getData()));
                    else if (td.getToken().equals(LexToken.Token.KEY)){
                        if(e.value_names.contains(td.getData()))f = new ExValue(td.getData(),1);
                        else if(e.all_value_names.contains(td.getData())) f = new ExValue(td.getData(),0);
                        else if(e.list_names.contains(td.getData())) f = new ExList(td.getData(),1);
                        else if(e.all_list_names.contains(td.getData())) f = new ExList(td.getData(),0);
                        else throw new VMException("Not found value:"+td.getData(),Main.output);
                    }
                    else if (td.getToken().equals(LexToken.Token.NAME)){
                        if(td.getData().equals("true")||td.getData().equals("false"))f = new ExBool(Boolean.parseBoolean(td.getData()));
                        else if(td.getData().equals("null"))f = new ExNull();
                        else if(td.getData().equals("exe")){
                            int exe_index = 1;boolean isfirst = false;
                            ArrayList<LexToken.TokenD> v_tds = new ArrayList<>();
                            do{
                                td = getTokens();
                                v_tds.add(td);

                                if(td.getToken().equals(LexToken.Token.LP)&&td.getData().equals("(")&&isfirst) exe_index += 1;
                                if(td.getToken().equals(LexToken.Token.LP)&&td.getData().equals("(")) isfirst = true;
                                if(td.getToken().equals(LexToken.Token.LR)&&td.getData().equals(")")) exe_index-= 1;

                            }while (exe_index > 0);

                            AstInvokeTree in = new AstInvokeTree();
                            in.setTds(v_tds);
                            InvokeOutCode ic = (InvokeOutCode) in.eval(e);
                            values.add(ic);
                            continue;

                        } else throw new VMException("Cannot usr key in function value.", Main.output);
                    } else throw new VMException("Unknown value type.",Main.output);

                    values.add(new PushOPStackOutCode(f));

                    td = getTokens();
                } while (!(td.getToken().equals(LexToken.Token.LR) && td.getData().equals(")")));

                if(f==null)break;

            } while (!(td.getToken().equals(LexToken.Token.LR) && td.getData().equals(")")));
        }catch (IndexOutOfBoundsException e1){
        }catch (Exception e1){
            throw new VMException("Unknown parser error:"+e1.getLocalizedMessage(),Main.output);
        }

        Collections.reverse(values);

        return new InvokeOutCode("L"+lib+"/"+function,new GroupOutCode(values));
    }
}
