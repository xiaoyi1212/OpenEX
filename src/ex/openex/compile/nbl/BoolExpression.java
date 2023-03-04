package ex.openex.compile.nbl;

import ex.openex.Main;
import ex.openex.code.*;
import ex.openex.compile.LexToken;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;
import ex.exvm.obj.*;

import java.util.ArrayList;
import java.util.Stack;

public class BoolExpression {

    public static ArrayList<OutCode> calculate(CompileFile e, ArrayList<LexToken.TokenD> tds) throws VMException {
        ArrayList<OutCode> ret = new ArrayList<>();

        for (LexToken.TokenD td:tds){
            if(td.getToken().equals(LexToken.Token.NAME)){
                if(td.getData().equals("true")) ret.add(new PushOPStackOutCode(new ExBool(true)));
                else if(td.getData().equals("false")) ret.add(new PushOPStackOutCode(new ExBool(false)));
            }else if(td.getToken().equals(LexToken.Token.SEM)){
                switch (td.getData()) {
                    case "!" -> ret.add(new NotOutCode());
                    case "&" -> ret.add(new AndOutCode());
                    case "|" -> ret.add(new OrOutCode());
                    case "==" -> ret.add(new EquOutCode());
                    case ">=" -> ret.add(new BigEquOutCode());
                    case "<=" -> ret.add(new LessEquOutCode());
                    case ">" -> ret.add(new BigOutCode());
                    case "<" -> ret.add(new LessOutCode());
                    default -> throw new VMException("Unknown sem in boolean statement.",Main.output);
                }
            }else if(td.getToken().equals(LexToken.Token.KEY)) {
                ExValue valuea = null;

                if (e.value_names.contains(td.getData())) {
                    valuea = new ExValue(td.getData(),1);
                } else throw new VMException("Unknown value name.", Main.output);
               ret.add(new PushOPStackOutCode(valuea));
            }else if(td.getToken().equals(LexToken.Token.DOUBLE))ret.add(new PushOPStackOutCode(new ExDouble(Double.parseDouble(td.getData()))));
            else if(td.getToken().equals(LexToken.Token.NUM))ret.add(new PushOPStackOutCode(new ExInt(Integer.parseInt(td.getData()))));
            else if(td.getToken().equals(LexToken.Token.STR))ret.add(new PushOPStackOutCode(new ExString(td.getData())));
        }

        return ret;
    }

    public static ArrayList<LexToken.TokenD> parseBoolExpr(ArrayList<LexToken.TokenD> expression) {
        Stack<LexToken.TokenD> stack = new Stack<>();
        boolean b = false;
        LexToken.TokenD temp;
        ArrayList<LexToken.TokenD> out = new ArrayList<>();

        for(LexToken.TokenD c : expression) {
            switch (c.getData()){
                case "!":b = !b;break;
                case "&", "|","==",">=","<=",">","<":
                    while (stack.size() != 0){
                        temp = stack.pop();
                        if(temp.getData().equals("(")){stack.push(temp);break;}out.add(temp);
                    }stack.push(c);break;
                case "(":stack.push(c);break;
                case ")":
                while (!stack.isEmpty()) {
                    temp = stack.pop();
                    if (temp.getData().equals("(")) break;
                    out.add(temp);
                }break;
                default: out.add(c);break;
            }
        }
        if(b) stack.push(new LexToken.TokenD(LexToken.Token.SEM,"!"));
        while (!stack.isEmpty()) {out.add(stack.pop());}

        return out;
    }
}
