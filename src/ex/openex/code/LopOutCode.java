package ex.openex.code;

public class LopOutCode implements OutCode{
    GroupOutCode bool;
    GroupOutCode block;

    public LopOutCode(GroupOutCode bool,GroupOutCode block){
        this.bool = bool;
        this.block = block;
    }

    public GroupOutCode getBlock() {
        return block;
    }

    public GroupOutCode getBool() {
        return bool;
    }
}
