package ex.openex.compile.ast;

import ex.openex.code.OutCode;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;

import java.util.ArrayList;

public abstract class AstTree {
    public abstract AstTree child(int i);
    public abstract int getChildren();
    public abstract ArrayList<AstTree> children();
    public abstract void location(int em);
    public abstract OutCode eval(CompileFile e) throws VMException;
}
