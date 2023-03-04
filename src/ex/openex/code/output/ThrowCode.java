package ex.openex.code.output;

import ex.openex.Util;

public class ThrowCode implements BaseCode{
    byte const_table_index;

    public ThrowCode(byte const_table_index){
        this.const_table_index = const_table_index;
    }

    @Override
    public byte[] eval() {
        return new byte[]{Util.THR,const_table_index};
    }

    @Override
    public byte getOpNum() {
        return const_table_index;
    }
}
