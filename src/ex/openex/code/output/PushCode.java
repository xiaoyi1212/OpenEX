package ex.openex.code.output;

import ex.openex.Util;

public class PushCode implements BaseCode{
    byte const_table_index;
    public PushCode(byte const_table_index){
        this.const_table_index =const_table_index;
    }

    @Override
    public byte[] eval() {
        return new byte[]{Util.PUSH,const_table_index};
    }

    @Override
    public byte getOpNum() {
        return const_table_index;
    }
}
