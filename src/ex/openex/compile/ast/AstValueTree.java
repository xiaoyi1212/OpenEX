package ex.openex.compile.ast;

import ex.openex.Main;
import ex.openex.code.GroupOutCode;
import ex.openex.code.LoadValueOutCode;
import ex.openex.code.OutCode;
import ex.openex.code.PushOPStackOutCode;
import ex.openex.compile.LexToken;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;
import ex.exvm.obj.ExNull;

import java.util.ArrayList;

public class AstValueTree extends AstLeaf{

    ArrayList<LexToken.TokenD> tds;

    private int index = 0;

    private LexToken.TokenD getTokens(){
        if(index > tds.size())return null;
        LexToken.TokenD td = tds.get(index);
        index += 1;
        return td;
    }

    public AstValueTree(){
    }

    public void setTds(ArrayList<LexToken.TokenD> tds) {
        index = 0;
        this.tds = tds;
    }

    @Override
    public OutCode eval(CompileFile e) throws VMException {
        index = 0;
        String name,text;ArrayList<OutCode> bcs = new ArrayList<>();
        LexToken.TokenD td = getTokens();
        int type11;

        if(!td.getToken().equals(LexToken.Token.NAME))throw new VMException("The variable definition statement must have an access permission modifier.", Main.output);
        if(td.getData().equals("local"))type11 = 1;
        else if(td.getData().equals("global"))type11 = 0;
        else throw new VMException("Unknown type in value statement",Main.output);

        td = getTokens();
        if(!td.getToken().equals(LexToken.Token.KEY))throw new VMException("Variable names must not be defined with other types.",Main.output);
        name = td.getData();td = getTokens();
        if(!(td.getToken().equals(LexToken.Token.SEM)&&td.getData().equals(":")))throw new VMException("Unknown lex in value creator.",Main.output);
        td = getTokens();
        if(!td.getToken().equals(LexToken.Token.STR)) throw new VMException("Unknown value text.",Main.output);
        text = td.getData();

        if(e.value_names.contains(name))throw new VMException("Cannot define variables with the same variable name.",Main.output);

        for(AstTree tree:this.children()){
            if(tree instanceof AstNoneTree) {
                bcs.add(new PushOPStackOutCode(new ExNull()));
                break;
            }
            OutCode bc = tree.eval(e);
            if(bc instanceof GroupOutCode){
                bcs.addAll(((GroupOutCode) bc).getBcs());
            }else bcs.add(bc);
        }
        e.value_names.add(name);
        index = 0;

        if(type11==0)e.all_value_names.add(name);
        if(type11==1)e.value_names.add(name);

        return new LoadValueOutCode(name,text,bcs,type11);
    }
}
