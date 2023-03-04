package ex.openex.compile.ast;

import ex.openex.Main;
import ex.openex.code.OutCode;
import ex.openex.code.ThrowOutCode;
import ex.openex.compile.LexToken;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;

import java.util.ArrayList;

public class AstThrowTree extends AstLeaf{
    ArrayList<LexToken.TokenD> tds;

    private int index = 0;

    private LexToken.TokenD getTokens(){
        if(index > tds.size())return null;
        LexToken.TokenD td = tds.get(index);
        index += 1;
        return td;
    }
    public void setTds(ArrayList<LexToken.TokenD> tds) {
        index = 0;
        this.tds = tds;
    }

    @Override
    public OutCode eval(CompileFile e) throws VMException {

        LexToken.TokenD td = getTokens();
        if(td.getToken().equals(LexToken.Token.NAME)) throw new VMException("Cannot use key in catch statement 'exception type' block.", Main.output);

        return new ThrowOutCode(td.getData());
    }
}
