package ex.openex.compile.ast;

import ex.openex.code.GroupOutCode;
import ex.openex.code.OutCode;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;

import java.util.ArrayList;

public class AstElseStatement extends AstLeaf{
    @Override
    public OutCode eval(CompileFile e) throws VMException {
        ArrayList<OutCode> ocs = new ArrayList<>();
        for(AstTree tree:children())ocs.add(tree.eval(e));
        return new GroupOutCode(ocs);
    }
}
