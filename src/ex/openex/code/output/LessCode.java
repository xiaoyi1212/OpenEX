package ex.openex.code.output;

import ex.openex.Util;

public class LessCode extends ALUCode{
    @Override
    public byte[] eval() {
        return new byte[]{Util.LESS,Util.VOID_OP_NUM};
    }

    @Override
    public byte getOpNum() {
        return Util.VOID_OP_NUM;
    }
}
