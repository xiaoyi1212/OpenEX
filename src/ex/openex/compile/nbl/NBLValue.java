package ex.openex.compile.nbl;

import ex.openex.Main;
import ex.openex.code.*;
import ex.openex.compile.LexToken;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;
import ex.exvm.obj.ExDouble;
import ex.exvm.obj.ExValue;

import java.util.ArrayList;
import java.util.Stack;

public class NBLValue {

    ArrayList<LexToken.TokenD> tds;
    public NBLValue(ArrayList<LexToken.TokenD> tds){
        this.tds = tds;
    }

    public ArrayList<LexToken.TokenD> nblLexValue() {
        Stack<LexToken.TokenD> opStack = new Stack<>();
        ArrayList<LexToken.TokenD> suffixList = new ArrayList<>();
        for (int i = 0; i < tds.size(); i++) {
            LexToken.TokenD tmp;LexToken.TokenD t = tds.get(i);
            switch (t.getData()){
                case "(":opStack.push(t);break;
                case "+": case "-":
                    while (opStack.size() != 0){
                        tmp = opStack.pop();
                        if(tmp.getData().equals("(")){opStack.push(tmp);break;}suffixList.add(tmp);
                    }opStack.push(t);break;
                case "*": case "/": case "%":
                    while (opStack.size() != 0) {
                        tmp = opStack.pop();
                        if (tmp.getData().equals("+")||tmp.getData().equals("-")||tmp.getData().equals("(")) {opStack.push(tmp);break;}suffixList.add(tmp);
                    }opStack.push(t);break;
                case ")":
                    while (!opStack.isEmpty()) {
                        tmp = opStack.pop();
                        if (tmp.getData().equals("(")) {break;}suffixList.add(tmp);
                    }break;
                default: suffixList.add(t);break;
            }
        }
        while (!opStack.isEmpty()) {suffixList.add(opStack.pop());}return suffixList;
    }
    public ArrayList<OutCode> calculate(CompileFile e, ArrayList<LexToken.TokenD> tds)throws VMException {
        ArrayList<OutCode> bbc = new ArrayList<>();
        for(LexToken.TokenD td:tds){
            if(td.getToken().equals(LexToken.Token.NAME)) throw new RuntimeException("The keyword appears when parsing the expression");
            if(td.getToken().equals(LexToken.Token.NUM))bbc.add(new PushOPStackOutCode(new ExDouble(Double.parseDouble(td.getData()))));
            else if(td.getToken().equals(LexToken.Token.KEY)){
                ExValue valuea = null;
                if(e.value_names.contains(td.getData())){
                    valuea = new ExValue(td.getData(),1);
                }else throw new VMException("Unknown value name.", Main.output);
                bbc.add(new PushOPStackOutCode(valuea));
            }
            else if(td.getToken().equals(LexToken.Token.SEM)){
                if (td.getData().equals("+")) bbc.add(new AddOutCode());
                else if (td.getData().equals("-")) bbc.add(new SubOutCode());
                else if (td.getData().equals("*")) bbc.add(new MulOutCode());
                else if (td.getData().equals("/")) bbc.add(new DivOutCode());
                else if (td.getData().equals("%")) bbc.add(new RemOutCode());
            }
        }

        return bbc;
    }
}
