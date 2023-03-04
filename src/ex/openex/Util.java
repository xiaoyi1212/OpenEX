package ex.openex;

import java.util.ArrayList;

public class Util {
    public static final byte PUSH = 0x0f;
    public static final byte POP = 0x0c;
    public static final byte ADD = 0x06;
    public static final byte SUB = 0x07;
    public static final byte MUL = 0x08;
    public static final byte DIV = 0x09;
    public static final byte IML = 0x12;
    public static final byte INV = 0x1a;
    public static final byte AND = 0x1f;
    public static final byte OR =  0x23;
    public static final byte NOT = 0x0e;
    public static final byte NUL = 0x0a;
    public static final byte MOV = 0x05;
    public static final byte VOID_OP_NUM = 0x00;
    public static final byte LESS = 0x01;
    public static final byte BIG = 0x02;
    public static final byte LESS_EQU = 0x03;
    public static final byte BIG_EQU = 0x04;
    public static final byte EQU = 0x05;
    public static final byte JNE = 0x10;
    public static final byte RET = 0x11;

    public static ArrayList<Byte> removeFirst(ArrayList<Byte> a,int indexof){
        if (indexof > 0) {
            a.subList(0, indexof).clear();
        }
        return a;
    }
}
