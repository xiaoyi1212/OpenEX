package ex.openex.compile.ast;


import ex.openex.Main;
import ex.openex.code.GroupOutCode;
import ex.openex.code.OutCode;
import ex.openex.code.PushOPStackOutCode;
import ex.openex.compile.LexToken;
import ex.openex.compile.nbl.BoolExpression;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;
import ex.exvm.obj.ExBool;

import java.util.ArrayList;

public class AstBoolStatement extends AstLeaf{
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
    }

    @Override
    public OutCode eval(CompileFile e) throws VMException {
        LexToken.TokenD td = getTokens();
        if(td.getData().equals("true")) return new PushOPStackOutCode(new ExBool(true));
        else if(td.getData().equals("false")) return new PushOPStackOutCode(new ExBool(false));
        else if(td.getData().equals("("))return new GroupOutCode(BoolExpression.calculate(e,BoolExpression.parseBoolExpr(tds)));
        else throw new VMException("Unknown type in boolean statement.", Main.output);
    }
}
