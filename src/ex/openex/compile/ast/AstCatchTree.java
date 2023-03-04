package ex.openex.compile.ast;


import ex.openex.Main;
import ex.openex.code.OutCode;
import ex.openex.compile.LexToken;
import ex.openex.compile.parser.CompileFile;
import ex.openex.compile.parser.InterruptDescriptorTable;
import ex.openex.exception.VMException;

import java.util.ArrayList;

public class AstCatchTree extends AstLeaf{
    private ArrayList<LexToken.TokenD> tds;
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

        try {
            LexToken.TokenD td = getTokens();
            if (!td.getToken().equals(LexToken.Token.KEY))
                throw new VMException("Unknown exception type: '" + td.getData() + "'.", Main.output);

            String id = td.getData();

            ArrayList<OutCode> bc = new ArrayList<>();
            for (AstTree tree : this.children()) {
                bc.add(tree.eval(e));
            }
            index = 0;

            return new InterruptDescriptorTable(id,bc);
        }catch (IndexOutOfBoundsException e1){
            throw new VMException("Not found exception name in catch statement.", Main.output);
        }
    }
}
