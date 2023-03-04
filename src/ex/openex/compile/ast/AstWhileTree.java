package ex.openex.compile.ast;

import ex.openex.Main;
import ex.openex.code.*;
import ex.openex.compile.LexToken;
import ex.openex.compile.nbl.BoolExpression;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;
import ex.exvm.obj.ExBool;

import java.util.ArrayList;

public class AstWhileTree extends AstLeaf{
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
    }

    public void setBool(ArrayList<LexToken.TokenD> bool) {
        this.bool = bool;
    }

    @Override
    public OutCode eval(CompileFile e) throws VMException {
        LexToken.TokenD td = getBoolTokens();
        bool.remove(bool.size()-1);boolean isbool = false;

        for(LexToken.TokenD t:bool)
            if (t.getToken().equals(LexToken.Token.SEM) || t.getToken().equals(LexToken.Token.LP)) {
                isbool = true;
                break;
            }

        ArrayList<OutCode> bc = new ArrayList<>(),bol = new ArrayList<>();
        for(AstTree TREE:children())bc.add(TREE.eval(e));

        if(td.getToken().equals(LexToken.Token.NAME)){
            if(td.getData().equals("true")) {
                bol.add(new PushOPStackOutCode(new ExBool(true)));
                return new LopOutCode(new GroupOutCode(bol),new GroupOutCode(bc));
            }else if(td.getData().equals("false")){
                return new NolOutCode();
            }else throw new VMException("Cannot use keywords in Boolean expressions in While statements.", Main.output);
        }else if(td.getToken().equals(LexToken.Token.KEY)){
            return new LopOutCode(new GroupOutCode(BoolExpression.calculate(e,BoolExpression.parseBoolExpr(bool))),new GroupOutCode(bc));
        }else throw new VMException("Cannot in while statement boolean use key.",Main.output);
    }
}
