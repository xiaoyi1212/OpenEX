package ex.openex.code.output;

import ex.openex.Util;

public class EquCode extends ALUCode{
    @Override
    public byte[] eval() {
        return new byte[]{Util.EQU};
    }

    @Override
    public byte getOpNum() {
        return Util.VOID_OP_NUM;
    }
}
