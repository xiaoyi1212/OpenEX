package ex.openex.compile.ast;

import ex.openex.code.OutCode;
import ex.openex.compile.LexToken;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;

import java.util.ArrayList;

public class AstLeaf extends AstTree{
    private ArrayList<AstTree> empty;
    private LexToken.TokenD token;
    public AstLeaf(){
        empty = new ArrayList<>();
    }

    public AstLeaf(LexToken.TokenD token){
        empty = new ArrayList<>();
        this.token = token;
    }

    @Override
    public AstTree child(int i) {
        return empty.get(i);
    }

    @Override
    public int getChildren() {
        return empty.size();
    }

    @Override
    public ArrayList<AstTree> children() {
        return empty;
    }

    @Override
    public void location(int em) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < em; i++) sb.append("\t");
        for (AstTree tree:empty)tree.location(em+1);
    }

    @Override
    public OutCode eval(CompileFile e) throws VMException {
        return new OutCode() {

        };
    }
}
