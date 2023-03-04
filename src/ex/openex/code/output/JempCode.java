package ex.openex.code.output;

import ex.openex.Util;

public class JempCode implements BaseCode{
    byte index;
    public JempCode(byte index){
        this.index = index;
    }

    @Override
    public byte[] eval() {
        return new byte[]{Util.JNE,index};
    }

    @Override
    public byte getOpNum() {
        return index;
    }
}
