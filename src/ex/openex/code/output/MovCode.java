package ex.openex.code.output;

import ex.openex.Util;

public class MovCode implements BaseCode{
    byte const_table_num;

    @Override
    public byte[] eval() {
        return new byte[]{Util.MOV,const_table_num};
    }

    @Override
    public byte getOpNum() {
        return const_table_num;
    }

    public MovCode(byte const_table_num){
        this.const_table_num = const_table_num;
    }
}
