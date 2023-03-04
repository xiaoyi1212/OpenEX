package ex.openex.code;

import ex.openex.compile.parser.CodeOptimization;
import ex.exvm.obj.ExObject;

public class RetOutCode implements OutCode{
    ExObject obj;
    public RetOutCode(ExObject obj){
        this.obj = obj;
    }

    public byte getType(){

        switch (obj.getType()){
            case STRING -> {
                return CodeOptimization.ConstTableTask.STRING;
            }
            case INT -> {
                return CodeOptimization.ConstTableTask.INTEGER;
            }
            case DOUBLE -> {
                return CodeOptimization.ConstTableTask.DOUBLE;
            }
            case BOOL -> {
                return CodeOptimization.ConstTableTask.BOOL;
            }
            case NULL -> {
                return CodeOptimization.ConstTableTask.NULL;
            }
            case OBJECT -> {
                return CodeOptimization.ConstTableTask.VALUE_NAME;
            }
        }
        return 0x00;
    }

    public String getObj() {
        return obj.getData();
    }

    @Override
    public String toString() {
        return "ret "+obj;
    }
}
