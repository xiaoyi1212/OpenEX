package ex.openex.compile.ast;


import ex.openex.code.BreakOutCode;
import ex.openex.code.OutCode;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;

public class AstBreakTree extends AstLeaf{
    @Override
    public OutCode eval(CompileFile e) throws VMException {
        return new BreakOutCode();
    }
}
