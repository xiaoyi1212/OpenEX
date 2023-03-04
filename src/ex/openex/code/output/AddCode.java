package ex.openex.code.output;

import ex.openex.Util;

public class AddCode extends ALUCode{
    @Override
    public byte[] eval() {
        return new byte[]{Util.ADD,Util.VOID_OP_NUM};
    }

    @Override
    public byte getOpNum() {
        return Util.VOID_OP_NUM;
    }
}
