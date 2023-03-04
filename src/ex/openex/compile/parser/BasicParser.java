package ex.openex.compile.parser;


import ex.openex.Main;
import ex.openex.VMOutputStream;
import ex.openex.code.OutCode;
import ex.openex.code.PushOPStackOutCode;
import ex.openex.compile.LexToken;
import ex.openex.compile.ast.*;
import ex.openex.exception.VMException;
import ex.exvm.obj.ExString;

import java.util.ArrayList;
import java.util.LinkedList;

public class BasicParser {
    LinkedList<LexToken.TokenD> tds;
    int index;

    public BasicParser(String file_name,LinkedList<LexToken.TokenD> tds, VMOutputStream player){
        this.index = 0;
        this.tds = tds;
        this.player = player;
        this.file_name = file_name;
    }
    VMOutputStream player;
    String file_name;

    private LexToken.TokenD getToken(){
        if(index >= tds.size()) return null;
        LexToken.TokenD t = tds.get(index);
        index += 1;
        return t;
    }

    private AstLeaf getLeaf() throws VMException {
        AstLeaf leaf;
        LexToken.TokenD td = getToken();
        try {

            if (td == null) return null;
            if (td.getToken().equals(LexToken.Token.NAME)) {
                switch (td.getData()) {
                    case "include" -> {
                        ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                        do {
                            td = getToken();
                            tds.add(td);
                        } while (!td.getToken().equals(LexToken.Token.END));
                        leaf = new AstIncludeTree();
                        ((AstIncludeTree) leaf).setTds(tds);
                        return leaf;
                    }
                    case "exe" -> {
                        leaf = new AstInvokeTree();
                        ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                        do {
                            td = getToken();
                            tds.add(td);
                        } while (!td.getToken().equals(LexToken.Token.END));
                        ((AstInvokeTree) leaf).setTds(tds);
                        return leaf;
                    }
                    case "function" -> {
                        leaf = new AstFunctionTree();
                        ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                        do {
                            td = getToken();
                            if (td.getToken().equals(LexToken.Token.LP) && td.getData().equals("{")) break;
                            tds.add(td);
                        } while (true);
                        ((AstFunctionTree) leaf).setTds(tds);
                        for (AstTree at : getBlockGroup(td)) {
                            leaf.children().add(at);
                        }
                        return leaf;
                    }
                    case "catch"->{
                        leaf = new AstCatchTree();
                        ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                        do {
                            td = getToken();
                            if (td.getToken().equals(LexToken.Token.LP) && td.getData().equals("{")) break;
                            tds.add(td);
                        } while (true);
                        ((AstCatchTree) leaf).setTds(tds);
                        for (AstTree at : getBlockGroup(td)) {
                            leaf.children().add(at);
                        }
                        return leaf;
                    }
                    case "value" -> {
                        leaf = new AstValueTree();
                        ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                        do {
                            td = getToken();
                            tds.add(td);
                        } while (!(td.getToken().equals(LexToken.Token.SEM) && td.getData().equals("=")));
                        td = getToken();
                        ((AstValueTree) leaf).setTds(tds);

                        leaf.children().add(getValueI(td));
                        return leaf;
                    }
                    case "if" -> {
                        leaf = new AstIfStatement();
                        td = getToken();
                        if (!(td.getToken().equals(LexToken.Token.LP) && td.getData().equals("(")))
                            throw new VMException("The If statement must be followed by '('", player);
                        getIf((AstIfStatement) leaf);
                        return leaf;
                    }
                    case "while" -> {
                        leaf = new AstWhileTree();
                        td = getToken();
                        if (!(td.getToken().equals(LexToken.Token.LP) && td.getData().equals("(")))
                            throw new VMException("The While statement must be followed by '('", player);
                        getWhile((AstWhileTree) leaf);
                        return leaf;
                    }
                    case "set" -> {
                        leaf = new AstSetValue();
                        ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                        do {
                            td = getToken();
                            tds.add(td);
                        } while (!(td.getToken().equals(LexToken.Token.SEM) && td.getData().equals("=")));
                        td = getToken();
                        ((AstSetValue) leaf).setTds(tds);

                        leaf.children().add(getValueI(td));
                        return leaf;
                    }
                    case "list"->{
                        leaf = new AstListTree();
                        ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                        do {
                            td = getToken();
                            tds.add(td);
                        } while (!td.getToken().equals(LexToken.Token.END));

                        ((AstListTree) leaf).setTds(tds);

                        if(!td.getToken().equals(LexToken.Token.END))throw new VMException("Unknown lex in list statement end.", Main.output);
                        return leaf;
                    }
                    default -> throw new VMException("error in parser :" + td, player);
                }
            } else throw new VMException("Unknown lexin parser:" + td,player);
        }catch (Exception npe){
            npe.printStackTrace();
            throw new VMException("The statement must be ended with ';'",player);
        }
    }

    private AstLeaf getWhile(AstWhileTree leaf) throws VMException{
        LexToken.TokenD td;
        int lpbuf = 1;
        ArrayList<LexToken.TokenD> tdd = new ArrayList<>();
        do {
            td = getToken();
            if (td.getToken().equals(LexToken.Token.LP) && td.getData().equals("(")) lpbuf += 1;
            if (td.getToken().equals(LexToken.Token.LR) && td.getData().equals(")")) lpbuf -= 1;
            tdd.add(td);
        }while (lpbuf != 0);
        leaf.setBool(tdd);

        td = getToken();
        if(!(td.getToken().equals(LexToken.Token.LP)&&td.getData().equals("{")))throw new VMException("The While statement must be followes by '{'",player);
        leaf.children().addAll(getWhileGroup(td));
        return leaf;
    }

    private ArrayList<AstTree> getWhileGroup(LexToken.TokenD tdt)throws VMException{
        int lpbuf = 0;if(tdt.getToken().equals(LexToken.Token.LP)&&tdt.getData().equals("{"))lpbuf += 1;
        ArrayList<AstTree> ats = new ArrayList<>();

        do{
            tdt = getToken();

            if(tdt == null)return ats;
            if(tdt.getToken().equals(LexToken.Token.LP)&&tdt.getData().equals("{"))lpbuf += 1;
            if(tdt.getToken().equals(LexToken.Token.LR)&&tdt.getData().equals("}"))lpbuf -= 1;
            if(tdt.getToken().equals(LexToken.Token.NAME)) {
                if (tdt.getData().equals("exe")) {
                    AstInvokeTree leaf1 = new AstInvokeTree();
                    ArrayList<LexToken.TokenD> td = new ArrayList<>();
                    do {
                        tdt = getToken();
                        td.add(tdt);
                    } while (!tdt.getToken().equals(LexToken.Token.END));

                    leaf1.setTds(td);
                    ats.add(leaf1);
                } else if (tdt.getData().equals("value")) {
                    AstValueTree leaf = new AstValueTree();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        tds.add(tdt);
                    } while (!(tdt.getToken().equals(LexToken.Token.SEM) && tdt.getData().equals("=")));
                    tdt = getToken();
                    ((AstValueTree) leaf).setTds(tds);

                    leaf.children().add(getValueI(tdt));
                    ats.add(leaf);
                } else if (tdt.getData().equals("set")) {
                    AstSetValue leaf = new AstSetValue();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        tds.add(tdt);
                    } while (!(tdt.getToken().equals(LexToken.Token.SEM) && tdt.getData().equals("=")));
                    tdt = getToken();
                    ((AstSetValue) leaf).setTds(tds);

                    leaf.children().add(getValueI(tdt));
                    ats.add(leaf);
                } else if (tdt.getData().equals("if")) {
                    AstIfStatement leaf1 = new AstIfStatement();
                    tdt = getToken();
                    if (!(tdt.getToken().equals(LexToken.Token.LP) && tdt.getData().equals("(")))
                        throw new VMException("The If statement must be followed by '('",player);
                    getIf((AstIfStatement) leaf1);
                    ats.add(leaf1);
                } else if (tdt.getData().equals("while")) {
                    AstLeaf leaf1 = new AstWhileTree();
                    tdt = getToken();
                    if (!(tdt.getToken().equals(LexToken.Token.LP) && tdt.getData().equals("(")))
                        throw new VMException("The While statement must be followed by '('", player);
                    getWhile((AstWhileTree) leaf1);
                    ats.add(leaf1);
                } else if(tdt.getData().equals("catch")){
                    AstCatchTree leaf = new AstCatchTree();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        if (tdt.getToken().equals(LexToken.Token.LP) && tdt.getData().equals("{")) break;
                        tds.add(tdt);
                    } while (true);
                    ((AstCatchTree) leaf).setTds(tds);
                    for (AstTree at : getBlockGroup(tdt)) {
                        leaf.children().add(at);
                    }
                    ats.add(leaf);
                } else if (tdt.getData().equals("break")) {
                    ats.add(new AstBreakTree());
                }else if(tdt.getData().equals("list")){
                    AstListTree leaf = new AstListTree();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        tds.add(tdt);
                    } while (!tdt.getToken().equals(LexToken.Token.END));

                    ((AstListTree) leaf).setTds(tds);

                    if(!tdt.getToken().equals(LexToken.Token.END))throw new VMException("Unknown lex in list statement end.",Main.output);

                    ats.add(leaf);
                } else throw new VMException("error in parser :" + tdt,player);
            }
        }while (lpbuf!=0);

        return ats;
    }

    private AstLeaf getIf(AstIfStatement leaf) throws VMException{
        LexToken.TokenD td;
        int lpbuf = 1;
        ArrayList<LexToken.TokenD> tdd = new ArrayList<>();
        do {
            td = getToken();
            if (td.getToken().equals(LexToken.Token.LP) && td.getData().equals("(")) lpbuf += 1;
            if (td.getToken().equals(LexToken.Token.LR) && td.getData().equals(")")) lpbuf -= 1;
            tdd.add(td);
        }while (lpbuf != 0);
        leaf.setBool(tdd);

        td = getToken();
        if(!(td.getToken().equals(LexToken.Token.LP)&&td.getData().equals("{")))throw new VMException("The While statement must be followes by '{'",player);
        leaf.children().addAll(getIfGroup(td));
        return leaf;
    }

    private ArrayList<AstTree> getIfGroup(LexToken.TokenD tdt)throws VMException{
        int lpbuf = 0;if(tdt.getToken().equals(LexToken.Token.LP)&&tdt.getData().equals("{"))lpbuf += 1;
        ArrayList<AstTree> ats = new ArrayList<>();

        do{
            tdt = getToken();

            if(tdt == null)return ats;
            if(tdt.getToken().equals(LexToken.Token.LP)&&tdt.getData().equals("{"))lpbuf += 1;
            if(tdt.getToken().equals(LexToken.Token.LR)&&tdt.getData().equals("}"))lpbuf -= 1;
            if(tdt.getToken().equals(LexToken.Token.NAME)) {
                if (tdt.getData().equals("exe")) {
                    AstInvokeTree leaf1 = new AstInvokeTree();
                    ArrayList<LexToken.TokenD> td = new ArrayList<>();
                    do {
                        tdt = getToken();
                        td.add(tdt);
                    } while (!tdt.getToken().equals(LexToken.Token.END));

                    leaf1.setTds(td);
                    ats.add(leaf1);
                } else if (tdt.getData().equals("value")) {
                    AstValueTree leaf = new AstValueTree();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        tds.add(tdt);
                    } while (!(tdt.getToken().equals(LexToken.Token.SEM) && tdt.getData().equals("=")));
                    tdt = getToken();
                    ((AstValueTree) leaf).setTds(tds);

                    leaf.children().add(getValueI(tdt));
                    ats.add(leaf);
                } else if (tdt.getData().equals("set")) {
                    AstSetValue leaf = new AstSetValue();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        tds.add(tdt);
                    } while (!(tdt.getToken().equals(LexToken.Token.SEM) && tdt.getData().equals("=")));
                    tdt = getToken();
                    ((AstSetValue) leaf).setTds(tds);

                    leaf.children().add(getValueI(tdt));
                    ats.add(leaf);
                } else if (tdt.getData().equals("if")) {
                    AstIfStatement leaf1 = new AstIfStatement();
                    tdt = getToken();
                    if (!(tdt.getToken().equals(LexToken.Token.LP) && tdt.getData().equals("(")))
                        throw new VMException("The If statement must be followed by '('",player);
                    getIf((AstIfStatement) leaf1);
                    ats.add(leaf1);
                } else if (tdt.getData().equals("while")) {
                    AstLeaf leaf1 = new AstWhileTree();
                    tdt = getToken();
                    if (!(tdt.getToken().equals(LexToken.Token.LP) && tdt.getData().equals("(")))
                        throw new VMException("The While statement must be followed by '('", player);
                    getWhile((AstWhileTree) leaf1);
                    ats.add(leaf1);
                }else if(tdt.getData().equals("catch")){
                    AstCatchTree leaf = new AstCatchTree();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        if (tdt.getToken().equals(LexToken.Token.LP) && tdt.getData().equals("{")) break;
                        tds.add(tdt);
                    } while (true);
                    ((AstCatchTree) leaf).setTds(tds);
                    for (AstTree at : getBlockGroup(tdt)) {
                        leaf.children().add(at);
                    }
                    ats.add(leaf);
                } else if (tdt.getData().equals("break")) {
                    ats.add(new AstBreakTree());
                }else if(tdt.getData().equals("return")){
                    AstReturnTree leaf = new AstReturnTree();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        tds.add(tdt);
                    } while (!tdt.getToken().equals(LexToken.Token.END));
                    ((AstReturnTree) leaf).setTds(tds);
                    ats.add(leaf);

                }else if(tdt.getData().equals("list")){
                    AstListTree leaf = new AstListTree();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        tds.add(tdt);
                    } while (!tdt.getToken().equals(LexToken.Token.END));

                    ((AstListTree) leaf).setTds(tds);

                    if(!tdt.getToken().equals(LexToken.Token.END))throw new VMException("Unknown lex in list statement end.",Main.output);

                    ats.add(leaf);
                } else throw new VMException("error in parser :" + tdt,player);
            }
        }while (lpbuf!=0);

        return ats;
    }

    public CompileFile rust() throws VMException{
        AstLeaf leaf;

        CompileFile cm = new CompileFile(file_name);
        index = 0;
        while ((leaf = getLeaf())!=null)cm.trees.add(leaf);

        return cm;
    }
    private ArrayList<AstTree> getBlockGroup(LexToken.TokenD tdt) throws VMException{
        int lpbuf = 0;if(tdt.getToken().equals(LexToken.Token.LP)&&tdt.getData().equals("{"))lpbuf += 1;
        ArrayList<AstTree> ats = new ArrayList<>();

        do{
            tdt = getToken();
            if(tdt == null)return ats;
            if(tdt.getToken().equals(LexToken.Token.LP)&&tdt.getData().equals("{"))lpbuf += 1;
            if(tdt.getToken().equals(LexToken.Token.LR)&&tdt.getData().equals("}"))lpbuf -= 1;
            if(tdt.getToken().equals(LexToken.Token.NAME)){

                if(tdt.getData().equals("exe")){
                    AstInvokeTree leaf1  = new AstInvokeTree();
                    ArrayList<LexToken.TokenD> td = new ArrayList<>();
                    do {
                        tdt = getToken();
                        td.add(tdt);
                    }while (!tdt.getToken().equals(LexToken.Token.END));
                    leaf1.setTds(td);
                    ats.add(leaf1);
                }else if(tdt.getData().equals("value")) {
                    AstValueTree leaf = new AstValueTree();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        tds.add(tdt);
                    } while (!(tdt.getToken().equals(LexToken.Token.SEM) && tdt.getData().equals("=")));
                    tdt = getToken();
                    ((AstValueTree) leaf).setTds(tds);

                    leaf.children().add(getValueI(tdt));
                    ats.add(leaf);
                }else if(tdt.getData().equals("set")){
                    AstSetValue leaf = new AstSetValue();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        tds.add(tdt);
                    } while (!(tdt.getToken().equals(LexToken.Token.SEM) && tdt.getData().equals("=")));
                    tdt = getToken();
                    ((AstSetValue) leaf).setTds(tds);

                    leaf.children().add(getValueI(tdt));
                    ats.add(leaf);
                }else if(tdt.getData().equals("if")) {
                    AstIfStatement leaf1  = new AstIfStatement();
                    tdt = getToken();
                    if (!(tdt.getToken().equals(LexToken.Token.LP) && tdt.getData().equals("(")))
                        throw new VMException("The If statement must be followed by '('",player);
                    getIf((AstIfStatement) leaf1);
                    ats.add(leaf1);
                }else if(tdt.getData().equals("while")) {
                    AstWhileTree leaf1 = new AstWhileTree();
                    tdt = getToken();
                    if (!(tdt.getToken().equals(LexToken.Token.LP) && tdt.getData().equals("(")))
                        throw new VMException("The While statement must be followed by '('", player);
                    getWhile((AstWhileTree) leaf1);
                    ats.add(leaf1);
                }else if(tdt.getData().equals("catch")) {
                    AstCatchTree leaf = new AstCatchTree();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        if (tdt.getToken().equals(LexToken.Token.LP) && tdt.getData().equals("{")) break;
                        tds.add(tdt);
                    } while (true);
                    ((AstCatchTree) leaf).setTds(tds);
                    for (AstTree at : getBlockGroup(tdt)) {
                        leaf.children().add(at);
                    }
                    ats.add(leaf);
                }else if(tdt.getData().equals("return")) {
                    AstReturnTree leaf = new AstReturnTree();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        tds.add(tdt);
                    } while (!tdt.getToken().equals(LexToken.Token.END));
                    ((AstReturnTree) leaf).setTds(tds);
                    ats.add(leaf);
                }else if(tdt.getData().equals("list")){
                    AstListTree leaf = new AstListTree();
                    ArrayList<LexToken.TokenD> tds = new ArrayList<>();
                    do {
                        tdt = getToken();
                        tds.add(tdt);
                    } while (!tdt.getToken().equals(LexToken.Token.END));

                    ((AstListTree) leaf).setTds(tds);

                    if(!tdt.getToken().equals(LexToken.Token.END))throw new VMException("Unknown lex in list statement end.",Main.output);

                    ats.add(leaf);
                }else throw new VMException("error in parser :"+tdt,player);
            }
        }while (lpbuf!=0);

        return ats;
    }

    private AstLeaf getValueI(LexToken.TokenD tdt) throws VMException{
        LexToken.TokenD td = tdt;
        AstLeaf leaf;
        ArrayList<LexToken.TokenD> tdtt = new ArrayList<>();
        if(td.getToken().equals(LexToken.Token.DOUBLE)||td.getToken().equals(LexToken.Token.KEY)||td.getToken().equals(LexToken.Token.NUM)) leaf = new AstNBLTree();
        else if(td.getToken().equals(LexToken.Token.NAME)) {
            if (td.getData().equals("exe")) leaf = new AstInvokeTree();
            else if (td.getData().equals("true") || td.getData().equals("false")) leaf = new AstBoolStatement();
            else if(td.getData().equals("null")) leaf = new AstNoneTree();
            else throw new VMException("Create value error: Unknown variable initializer definition.",player);
        }else if(td.getToken().equals(LexToken.Token.STR)) {
            LexToken.TokenD finalTd = td;
            leaf = new AstLeaf() {
                @Override
                public OutCode eval(CompileFile e) {
                    return new PushOPStackOutCode(new ExString(finalTd.getData()));
                }
            };
        }else if(td.getToken().equals(LexToken.Token.LP)) {
            leaf = new AstBoolStatement();
            tdtt.add(td);
        } else{
            throw new VMException("Create value error: Unknown variable initializer definition.",player);
        }


        if(leaf instanceof AstNBLTree)tdtt.add(td);
        if(leaf instanceof AstBoolStatement)tdtt.add(td);
        do {
            td = getToken();
            if(td.getToken().equals(LexToken.Token.END))break;
            tdtt.add(td);
        } while (true);

        if(leaf instanceof AstBoolStatement) ((AstBoolStatement) leaf).setTds(tdtt);
        if(leaf instanceof AstInvokeTree)((AstInvokeTree) leaf).setTds(tdtt);

        if(leaf instanceof AstNBLTree) ((AstNBLTree) leaf).setTds(tdtt);

        return leaf;
    }
}
