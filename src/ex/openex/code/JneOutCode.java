package ex.openex.code;

public class JneOutCode implements OutCode{
    GroupOutCode bool;
    GroupOutCode block;
    public JneOutCode(GroupOutCode bool,GroupOutCode block){
        this.bool = bool;
        this.block = block;
    }

    public GroupOutCode getBool() {
        return bool;
    }

    public GroupOutCode getBlock() {
        return block;
    }
}
