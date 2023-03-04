package ex.openex.code.output;

import ex.openex.Util;

public class RetCode implements BaseCode{
    @Override
    public byte[] eval() {
        return new byte[]{Util.RET,Util.VOID_OP_NUM};
    }

    @Override
    public byte getOpNum() {
        return 0;
    }
}
