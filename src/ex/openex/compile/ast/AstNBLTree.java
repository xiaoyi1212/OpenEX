package ex.openex.compile.ast;

import ex.openex.code.GroupOutCode;
import ex.openex.code.OutCode;
import ex.openex.code.PushOPStackOutCode;
import ex.openex.compile.LexToken;
import ex.openex.compile.nbl.IntNBLExpression;
import ex.openex.compile.nbl.NBLExpression;
import ex.openex.compile.nbl.NBLValue;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;
import ex.exvm.obj.ExDouble;

import java.util.ArrayList;

public class AstNBLTree extends AstLeaf{

    private ArrayList<LexToken.TokenD> tds;


    public void setTds(ArrayList<LexToken.TokenD> tds) {
        this.tds = tds;
    }

    @Override
    public OutCode eval(CompileFile e) throws VMException {
        boolean isnofinal = false;
        boolean isdouble = false;

        for(LexToken.TokenD td:tds){
            if(td.getToken().equals(LexToken.Token.KEY))isnofinal = true;
            if(td.getToken().equals(LexToken.Token.DOUBLE))isdouble=true;
        }

        if(isdouble) {
            double a;
            if (isnofinal) {
                NBLValue nbl = new NBLValue(tds);
                ArrayList<OutCode> bc = nbl.calculate(e,nbl.nblLexValue());
                return new GroupOutCode(bc);
            } else {
                a = new NBLExpression(tds).run();
                return new PushOPStackOutCode(new ExDouble(a));
            }
        }else {
            IntNBLExpression ine = new IntNBLExpression(tds);
            ArrayList<OutCode> bc = ine.calculate(e,ine.nblLexValue());
            return new GroupOutCode(bc);
        }

    }
}
