package ex.openex.compile.ast;

import ex.openex.Main;
import ex.openex.code.OutCode;
import ex.openex.code.RetOutCode;
import ex.openex.compile.LexToken;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;
import ex.exvm.obj.*;

import java.util.ArrayList;

public class AstReturnTree extends AstLeaf{
    ArrayList<LexToken.TokenD> tds;
    public void setTds(ArrayList<LexToken.TokenD> t){
        tds = t;
    }

    @Override
    public OutCode eval(CompileFile e) throws VMException {
        LexToken.TokenD td = tds.get(0);
        if(td.getToken().equals(LexToken.Token.DOUBLE))return new RetOutCode(new ExDouble(Double.parseDouble(td.getData())));
        else if(td.getToken().equals(LexToken.Token.STR))return new RetOutCode(new ExString(td.getData()));
        else if(td.getToken().equals(LexToken.Token.NUM))return new RetOutCode(new ExDouble(Double.parseDouble(td.getData())));
        else if(td.getToken().equals(LexToken.Token.KEY))return new RetOutCode(new ExValue(td.getData(),1));
        else if(td.getToken().equals(LexToken.Token.NAME)){
            return switch (td.getData()) {
                case "true" -> new RetOutCode(new ExBool(true));
                case "false" -> new RetOutCode(new ExBool(false));
                case "null" -> new RetOutCode(new ExNull());
                default -> throw new VMException("Cannot use key in return statement.", Main.output);
            };
        }else throw new VMException("Unknown parser in return statement.",Main.output);
    }
}
