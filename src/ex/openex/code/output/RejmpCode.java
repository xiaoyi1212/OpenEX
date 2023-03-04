package ex.openex.code.output;

import ex.openex.Util;

public class RejmpCode implements BaseCode{
    byte index_bool;
    byte index_block;
    public RejmpCode(byte index_bool,byte index_block){
        this.index_bool = index_bool;
        this.index_block = index_block;
    }

    public byte getIndexBool() {
        return index_bool;
    }

    public byte getIndexBlock() {
        return index_block;
    }

    @Override
    public byte[] eval() {
        return new byte[]{Util.JNE,index_bool,index_block};
    }

    @Override
    public byte getOpNum() {
        return (byte) (index_bool+index_block);
    }
}
