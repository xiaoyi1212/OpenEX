package ex.openex.compile;

import ex.openex.Main;
import ex.openex.VMOutputStream;
import ex.openex.exception.VMException;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class LexToken {
    public enum Token{
        END, //分号语句收尾符
        SEM, //符号
        LP, // 左括号
        LR, // 右括号
        KEY, //关键字
        NAME,
        STR, //字符串
        NUM, //数字
        TEXT, //多行注释
        DOUBLE
    }

    private char[] c;
    private LinkedList<TokenD> tds = new LinkedList<>();
    VMOutputStream player;

    int index;
    Character buf = null;
    private int getChar() throws EOFException{
        if(buf == null){
            if(index >= c.length) throw new EOFException();
            int r = c[index];
            index += 1;
            return r;
        }else{
            char c = buf;
            buf = null;
            return c;
        }
    }
    ArrayList<String> line;
    String file_name;
    public static ArrayList<String> getFile(String name){

        try(BufferedReader reader = new BufferedReader(new FileReader(name))){
            String line;
            ArrayList<String> sb = new ArrayList<>();
            while ((line = reader.readLine())!=null)sb.add(line);
            return sb;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public LexToken(String name,VMOutputStream stream,int jjj){
        this.line = getFile(name);
        this.file_name = name;
        this.player = stream;
        StringBuilder sb = new StringBuilder();
        for(String str:line){
            int i =str.indexOf("//");
            String n = str;
            if(i != -1) n = str.substring(0,i);
            sb.append(n).append(" ");
        }
        c = sb.toString().toCharArray();
        index = 0;
    }
    public LexToken(String data, VMOutputStream player){
        this.player = player;
        this.line = new ArrayList<>();
        line.add(data);
        this.file_name = "<console>";
        StringBuilder sb = new StringBuilder();
        for(String str:line){
            int i =str.indexOf("//");
            String n = str;
            if(i != -1) n = str.substring(0,i);
            sb.append(n).append(" ");
        }
        c = sb.toString().toCharArray();
        index = 0;
    }


    private TokenD run() throws Exception{
        int c;
        StringBuilder sb = new StringBuilder();
        do{
            c = getChar();
        }while (isSpace(c));
        if(isSEM(c)){
            sb.append((char) c);
            return new TokenD(Token.SEM,sb.toString());
        }else if(isNum(c)){
            boolean isdouble = false;
            do {
                sb.append((char) c);
                c = getChar();
                if(c == '.')isdouble = true;
            } while (isNum(c)||c == '.');
            if(isKey(c)){
                do{
                    sb.append((char) c);
                    c = getChar();
                }while (isKey(c)||isNum(c));
                buf = (char)c;
                return new TokenD(Token.KEY,sb.toString());
            }
            buf = (char)c;
            if(isdouble) return new TokenD(Token.DOUBLE,sb.toString());
            else return new TokenD(Token.NUM, sb.toString());
        }else if(isKey(c)) {
            do {
                sb.append((char) c);
                c = getChar();
                if(isSEM(c))break;
            } while (isKey(c) || isNum(c));
            buf = (char) c;
            if(Main.isKey(sb.toString()))return new TokenD(Token.NAME,sb.toString());
            return new TokenD(Token.KEY, sb.toString());
        }else if(c == '/') {
            sb.append((char) c);
            c = getChar();
            if (c == '*') {
                do {
                    do {
                        c = getChar();
                        sb.append((char) c);
                    } while (c != '*');
                    c = getChar();
                    sb.append((char) c);
                } while (c != '/');
                sb.deleteCharAt(0).deleteCharAt(sb.length()-1).deleteCharAt(sb.length()-1);
                return new TokenD(Token.TEXT, sb.toString());
            } else return new TokenD(Token.SEM, "/");
        }else if(c == '*') return new TokenD(Token.SEM,"*");
        else if(c == '"') {
            do {
                c = getChar();
                if(c=='\\'){
                    c= getChar();
                    if(c == 'n'){
                        sb.append("\n");
                    }else if(c == 't') {
                        sb.append("\t");
                    }else if(c == '\\') {
                        sb.append("\\");
                    }else if(c == '"'){
                        sb.append("\"");
                    }else throw new VMException("Unknown escape sequence.",player);
                    continue;
                }
                sb.append((char) c);
            } while (c != '"');
            sb.deleteCharAt(sb.indexOf("\""));
            return new TokenD(Token.STR, sb.toString());
        }else if(c == '=') {
            sb.append((char) c);
            c = getChar();
            if (c == '='||c == '!') {
                sb.append((char) c);
                return new TokenD(Token.SEM, sb.toString());
            }
            buf = (char) c;
            return new TokenD(Token.SEM, sb.toString());
        }else if(c == '>'||c=='<') {
            sb.append((char) c);
            c = getChar();
            if (c == '=') {
                sb.append((char) c);
                return new TokenD(Token.SEM, sb.toString());
            }
            buf = (char) c;
            return new TokenD(Token.SEM, sb.toString());
        }else if(c == '+') {
            sb.append((char) c);
            c = getChar();
            if (c == '=') {
                sb.append((char) c);
                return new TokenD(Token.SEM, sb.toString());
            }
            buf = (char) c;
            return new TokenD(Token.SEM, sb.toString());
        }else if(c == '-'){
            sb.append((char) c);
            c = getChar();
            if (c == '=') {
                sb.append((char) c);
                return new TokenD(Token.SEM, sb.toString());
            }else if(isNum(c)){
                sb.append((char) c);
                return new TokenD(Token.NUM,sb.toString());
            }
            buf = (char) c;
            return new TokenD(Token.SEM, sb.toString());
        }else if(isLP(c)) {
            sb.append((char) c);
            return new TokenD(Token.LP, sb.toString());
        }else if(isLR(c)) {
            sb.append((char) c);
            return new TokenD(Token.LR, sb.toString());
        }else if(c == ';') return new TokenD(Token.END,""+((char)c));
        else {
            int lines = 0,ki = 0,b = 0;
            while (true){
                if(lines >= line.size()-1/*||b >= index*/) break;
                if(ki >= line.get(lines).length()){
                    lines += 1;ki = 0;
                }
                ki += 1;b += 1;
            }
            ki += 1;
            throw new IOException("Unknown lex in file "+file_name+"(lines:"+lines+",chars:"+ki+"): >>"+((char)c)+"<<");
        }
    }

    private boolean isLP(int c){
        return (c=='('||c=='['||c=='{');
    }
    private boolean isLR(int c){
        return (c==')'||c==']'||c=='}');
    }

    private boolean isSpace(int c){
        return ((char)c == ' ');
    }

    private boolean isKey(int c){
        char a = (char) c;
        return (a >= 'a'&&a <= 'z')||(a >= 'A'&&a <= 'Z')||a == '_';
    }

    private boolean isNum(int c){
        return Character.isDigit((char) c);
    }

    private boolean isSEM(int c){
        return (c == ':') || (c == '!') || (c == '.') || (c == ',') || (c == '#') || (c == '$') || (c == '%')|| (c == '&')|| (c == '|');
    }

    public LinkedList<TokenD> launch(){
        try {
            TokenD td = null;
            while ((td = run()) != null) tds.add(td);
            return tds;
        }catch (EOFException eof){
            return tds;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static class TokenD{
        private Token t;
        private String str;
        public TokenD(Token t,String str){
            this.t = t;
            this.str = str;
        }

        public Token getToken() {
            return t;
        }

        public String getData() {
            return str;
        }

        @Override
        public String toString() {
            return t+" || "+str;
        }
    }
}

