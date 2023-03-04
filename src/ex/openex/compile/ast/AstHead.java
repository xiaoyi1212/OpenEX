package ex.openex.compile.ast;

import ex.openex.code.OutCode;
import ex.openex.compile.parser.CompileFile;

import java.util.ArrayList;

public class AstHead extends AstTree{

    static ArrayList<AstTree> at = new ArrayList<>();

    public AstTree child(int i) {
        return at.get(i);
    }

    @Override
    public int getChildren() {
        return at.size();
    }

    @Override
    public ArrayList<AstTree> children() {
        return at;
    }

    @Override
    public void location(int em) {
        System.out.println("HEAD");
    }


    @Override
    public OutCode eval(CompileFile e) {
        return null;
    }
}
