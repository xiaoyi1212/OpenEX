package ex.openex.compile.ast;

import ex.openex.Main;
import ex.openex.code.GroupOutCode;
import ex.openex.code.OutCode;
import ex.openex.code.PopLocalValueTableOutCode;
import ex.openex.compile.LexToken;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;

import java.util.ArrayList;

public class AstSetValue extends AstLeaf {
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
        LexToken.TokenD td = getTokens();
        ArrayList<OutCode> bcs = new ArrayList<>();
        if(!td.getToken().equals(LexToken.Token.KEY))throw new VMException("Unknown character in 'set' statement:"+td.getData(),Main.output);
        if(!e.value_names.contains(td.getData())) throw new VMException("Not found value:"+td.getData(), Main.output);
        for(AstTree tree:this.children()){
            OutCode bc = tree.eval(e);
            if(bc instanceof GroupOutCode){
                bcs.addAll(((GroupOutCode) bc).getBcs());
            }else bcs.add(bc);
        }
        index = 0;
        return new PopLocalValueTableOutCode(td.getData(),bcs);
    }
}
