package ex.openex.compile.ast;

import ex.openex.Main;
import ex.openex.code.OutCode;
import ex.openex.compile.LexToken;
import ex.openex.compile.parser.CompileFile;
import ex.openex.compile.parser.Function;
import ex.openex.exception.VMException;

import java.util.ArrayList;

public class AstFunctionTree extends AstLeaf{
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
        index = 0;
        LexToken.TokenD td = getTokens();
        if(!td.getToken().equals(LexToken.Token.KEY))throw new VMException("Unknown function statement name: '"+td.getData()+"'.", Main.output);

        String functionname = td.getData();

        ArrayList<String> values = new ArrayList<>();
        td = getTokens();

        if(td.getToken().equals(LexToken.Token.LP)&&td.getData().equals("(")){
            do{
                td = getTokens();
                if(td.getToken().equals(LexToken.Token.LR)&&td.getData().equals(")"))break;
                if(td.getToken().equals(LexToken.Token.SEM)&&td.getData().equals(","))continue;
                if(!td.getToken().equals(LexToken.Token.KEY)) throw new VMException("Unknown lex in function value statement:"+functionname,Main.output);
                values.add(td.getData());
                e.value_names.add(td.getData());
            }while (!(td.getToken().equals(LexToken.Token.LR)&&td.getData().equals(")")));
        }else throw new VMException("The function statement must has '('",Main.output);

        ArrayList<OutCode> bc = new ArrayList<>();
        for(AstTree tree:this.children()){
            bc.add(tree.eval(e));
        }
        index = 0;
        return new Function(e.getFilename().split("\\.")[0],functionname,values,bc);
    }
}
