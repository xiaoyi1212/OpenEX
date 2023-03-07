package ex.openex.compile.ast;

import ex.openex.Main;
import ex.openex.code.*;
import ex.openex.compile.LexToken;
import ex.openex.compile.nbl.BoolExpression;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;
import ex.exvm.obj.ExBool;

import java.util.ArrayList;

public class AstIfStatement extends AstLeaf{
    private ArrayList<LexToken.TokenD> tds;
    private ArrayList<LexToken.TokenD> bool;
    private int index = 0;
    private int bool_index = 0;

    private LexToken.TokenD getTokens(){
        if(index > tds.size())return null;
        LexToken.TokenD td = tds.get(index);
        index += 1;
        return td;
    }
    private LexToken.TokenD getBoolTokens(){
        if(bool_index > bool.size())return null;
        LexToken.TokenD td = bool.get(bool_index);
        bool_index += 1;
        return td;
    }

    public void setTds(ArrayList<LexToken.TokenD> tds) {
        this.tds = tds;
        index = 0;
    }

    public void setBool(ArrayList<LexToken.TokenD> bool) {
        this.bool = bool;
    }

    @Override
    public OutCode eval(CompileFile e) throws VMException {
        LexToken.TokenD td = bool.get(0);

        LexToken.TokenD end = bool.get(bool.size()-1);if(end.getToken().equals(LexToken.Token.LR))
            bool.remove(bool.size()-1);
        boolean isbool = false;

        for(LexToken.TokenD t:bool)
            if (t.getToken().equals(LexToken.Token.SEM) || t.getToken().equals(LexToken.Token.LP)||t.getToken().equals(LexToken.Token.KEY)) {
                isbool = true;
                break;
            }

        ArrayList<OutCode> bc = new ArrayList<>(),bol = new ArrayList<>();GroupOutCode elseb=null;
        for(AstTree TREE:children()){
            if(TREE instanceof AstElseStatement){
                bc.add(new JmpOutCode(TREE.eval(e)));
                elseb = (GroupOutCode) TREE.eval(e);
                break;
            }
            bc.add(TREE.eval(e));
        }



        if(!isbool){
            if(td.getData().equals("true")) {
                bol.add(new PushOPStackOutCode(new ExBool(true)));
                return new JneOutCode(new GroupOutCode(bol),new GroupOutCode(bc),null);
            }else if(td.getData().equals("false")){
                return elseb;
            }else throw new VMException("Cannot use keywords in Boolean expressions in IF statements.", Main.output);
        }else if(isbool){
            return new JneOutCode(new GroupOutCode(BoolExpression.calculate(e,BoolExpression.parseBoolExpr(bool))),new GroupOutCode(bc), elseb);
        }else throw new VMException("Cannot in if statement boolean use key.",Main.output);
    }
}
