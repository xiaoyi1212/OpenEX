package ex.openex.compile.nbl;


import ex.exvm.obj.ExDouble;
import ex.openex.Main;
import ex.openex.code.*;
import ex.openex.compile.LexToken;
import ex.openex.compile.ast.AstInvokeTree;
import ex.openex.compile.parser.CompileFile;
import ex.openex.exception.VMException;
import ex.exvm.obj.ExInt;
import ex.exvm.obj.ExString;
import ex.exvm.obj.ExValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

public class IntNBLExpression {

    ArrayList<LexToken.TokenD> tds;

    public IntNBLExpression(ArrayList<LexToken.TokenD> tds){
        this.tds = tds;
    }

    public ArrayList<LexToken.TokenD> nblLexValue() {
        Stack<LexToken.TokenD> opStack = new Stack<>();
        ArrayList<LexToken.TokenD> suffixList = new ArrayList<>();
        for (Iterator<LexToken.TokenD> iterator = tds.iterator(); iterator.hasNext(); ) {
            LexToken.TokenD tmp;
            LexToken.TokenD t = iterator.next();
            switch (t.getData()) {
                case "(" -> opStack.push(t);
                case "+", "-" -> {
                    while (opStack.size() != 0) {
                        tmp = opStack.pop();
                        if (tmp.getData().equals("(")) {
                            opStack.push(tmp);
                            break;
                        }
                        suffixList.add(tmp);
                    }
                    opStack.push(t);
                }
                case "*", "/" -> {
                    while (opStack.size() != 0) {
                        tmp = opStack.pop();
                        if (tmp.getData().equals("+") || tmp.getData().equals("-") || tmp.getData().equals("(")) {
                            opStack.push(tmp);
                            break;
                        }
                        suffixList.add(tmp);
                    }
                    opStack.push(t);
                }
                case ")" -> {
                    while (!opStack.isEmpty()) {
                        tmp = opStack.pop();
                        if (tmp.getData().equals("(")) {
                            break;
                        }
                        suffixList.add(tmp);
                    }
                }
                default ->{
                    if(t.getToken().equals(LexToken.Token.NAME)){
                        int exe_index = 1;boolean isfirst = false;
                        ArrayList<LexToken.TokenD> v_tds = new ArrayList<>();
                        do{
                            t = iterator.next();
                            v_tds.add(t);
                            if(t.getToken().equals(LexToken.Token.LP)&&t.getData().equals("(")&&isfirst) exe_index += 1;
                            if(t.getToken().equals(LexToken.Token.LP)&&t.getData().equals("(")) isfirst = true;
                            if(t.getToken().equals(LexToken.Token.LR)&&t.getData().equals(")")) exe_index-= 1;
                        }while (exe_index > 0);
                        Collections.reverse(v_tds);
                        v_tds.add(new LexToken.TokenD(LexToken.Token.NAME,"exe"));
                        Collections.reverse(v_tds);

                        suffixList.addAll(v_tds);

                        continue;
                    }
                    suffixList.add(t);
                }
            }
        }

        while (!opStack.isEmpty()) {suffixList.add(opStack.pop());}

        return suffixList;
    }
    public ArrayList<OutCode> calculate(CompileFile e, ArrayList<LexToken.TokenD> tds)throws VMException {
        ArrayList<OutCode> bbc = new ArrayList<>();
        for (Iterator<LexToken.TokenD> iterator = tds.iterator(); iterator.hasNext(); ) {
            LexToken.TokenD td = iterator.next();
            if (td.getToken().equals(LexToken.Token.NAME)) {
                if(td.getData().equals("exe")){
                    int exe_index = 1;boolean isfirst = false;
                    ArrayList<LexToken.TokenD> v_tds = new ArrayList<>();
                    do{
                        td = iterator.next();
                        v_tds.add(td);
                        if(td.getToken().equals(LexToken.Token.LP)&&td.getData().equals("(")&&isfirst) exe_index += 1;
                        if(td.getToken().equals(LexToken.Token.LP)&&td.getData().equals("(")) isfirst = true;
                        if(td.getToken().equals(LexToken.Token.LR)&&td.getData().equals(")")) exe_index-= 1;
                    }while (exe_index > 0);


                    AstInvokeTree ait = new AstInvokeTree();
                    ait.setTds(v_tds);
                    bbc.add(ait.eval(e));

                    continue;
                }else throw new VMException("The keyword appears when parsing the expression", Main.output);
            }
            if (td.getToken().equals(LexToken.Token.NUM)) bbc.add(new PushOPStackOutCode(new ExInt(Integer.parseInt(td.getData()))));
            else if(td.getToken().equals(LexToken.Token.DOUBLE)) bbc.add(new PushOPStackOutCode(new ExDouble(Double.parseDouble(td.getData()))));
            else if (td.getToken().equals(LexToken.Token.KEY)) {
                ExValue valuea = null;
                if (e.value_names.contains(td.getData())) {
                    valuea = new ExValue(td.getData(), 1);
                } else throw new VMException("Unknown value name.", Main.output);
                bbc.add(new PushOPStackOutCode(valuea));
            } else if (td.getToken().equals(LexToken.Token.SEM)) {
                if (td.getData().equals("+")) bbc.add(new AddOutCode());
                else if (td.getData().equals("-")) bbc.add(new SubOutCode());
                else if (td.getData().equals("*")) bbc.add(new MulOutCode());
                else if (td.getData().equals("/")) bbc.add(new DivOutCode());
            } else if (td.getToken().equals(LexToken.Token.STR)) bbc.add(new PushOPStackOutCode(new ExString(td.getData())));
        }

        return bbc;
    }
}
