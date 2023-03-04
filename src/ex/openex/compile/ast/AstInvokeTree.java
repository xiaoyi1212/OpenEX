package ex.openex.compile.ast;

import ex.openex.Main;
import ex.openex.code.GroupOutCode;
import ex.openex.code.InvokeOutCode;
import ex.openex.code.OutCode;
import ex.openex.code.PushOPStackOutCode;
import ex.openex.compile.LexToken;
import ex.openex.compile.nbl.IntNBLExpression;
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

                ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                int index = 1;
                do {
                    if(td.getToken().equals(LexToken.Token.LP) && td.getData().equals("(")){
                        tds.add(td);
                        index += 1;
                    }

                    if((td.getToken().equals(LexToken.Token.SEM) && td.getData().equals(","))){
                        td = getTokens();
                        IntNBLExpression inble = new IntNBLExpression(tds);
                        values.add(new GroupOutCode(inble.calculate(e,inble.nblLexValue())));
                        tds.clear();
                        continue;
                    }

                    if (td.getToken().equals(LexToken.Token.LR) && td.getData().equals(")")&&index > 0){
                        index -=1;
                        tds.add(td);
                    }



                    if(td.getToken().equals(LexToken.Token.LR) && td.getData().equals(")")&&index <= 0) {
                        IntNBLExpression inble = new IntNBLExpression(tds);
                        values.add(new GroupOutCode(inble.calculate(e,inble.nblLexValue())));
                        tds.clear();
                        break;
                    }

                    tds.add(td);



                    td = getTokens();
                } while (true);
            } while (!(td.getToken().equals(LexToken.Token.LR) && td.getData().equals(")")));


        }catch (IndexOutOfBoundsException ignored){
        }catch (Exception e1){
            throw new VMException("Unknown parser error:"+e1.getLocalizedMessage(),Main.output);
        }

        Collections.reverse(values);

        return new InvokeOutCode("L"+lib+"/"+function,new GroupOutCode(values));
    }
}
