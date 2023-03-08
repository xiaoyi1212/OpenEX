package ex.openex.compile.nbl;


import ex.exvm.obj.ExObject;
import ex.openex.compile.LexToken;

import java.util.ArrayList;
import java.util.Stack;

public class NBLExpression {
    ArrayList<LexToken.TokenD> tds;

    public NBLExpression(ArrayList<LexToken.TokenD> tds){
        this.tds = tds;
    }

    private ArrayList<LexToken.TokenD> first() {

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
                case "*": case "/":
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

        while (!opStack.isEmpty()) {suffixList.add(opStack.pop());}


        return suffixList;

    }

    private double calculate(ArrayList<LexToken.TokenD> ls){

        Stack<LexToken.TokenD> stack = new Stack<>();

        for(LexToken.TokenD td:ls){
            if(td.getToken().equals(LexToken.Token.DOUBLE)){
                stack.push(td);
            }else if(td.getToken().equals(LexToken.Token.SEM)){
                double num2 = Double.parseDouble(stack.pop().getData());
                double num1 = Double.parseDouble(stack.pop().getData());
                double res = 0;
                if (td.getData().equals("+")) {
                    res = num1 + num2;
                } else if (td.getData().equals("-")) {
                    res = num1 - num2;
                } else if (td.getData().equals("*")) {
                    res = num1 * num2;
                } else if (td.getData().equals("/")) {
                    res = num1 / num2;
                }
                stack.push(new LexToken.TokenD(LexToken.Token.NUM,""+res));
            }
        }
        return Double.parseDouble(stack.pop().getData());
    }
    public double run(){
        return calculate(first());
    }
}
