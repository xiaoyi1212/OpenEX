package ex.openex.compile.ast;


import ex.openex.Main;
import ex.openex.code.LoadLibOutCode;
import ex.openex.code.OutCode;
import ex.openex.compile.LexToken;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;

import java.util.ArrayList;

public class AstIncludeTree extends AstLeaf{
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
        index = 0;
        LexToken.TokenD td = tds.get(0);

        if(!td.getToken().equals(LexToken.Token.STR))throw new VMException("Unknown lib name type.", Main.output);

        String lib = td.getData();

        return new LoadLibOutCode(lib);
    }
}
