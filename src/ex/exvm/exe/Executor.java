package ex.exvm.exe;

import ex.exvm.ScriptManager;
import ex.exvm.lib.LibManager;
import ex.exvm.lib.NativeFunction;
import ex.exvm.lib.NativeLib;
import ex.exvm.ScriptLoader;
import ex.openex.code.output.*;
import ex.openex.compile.parser.CodeOptimization;
import ex.exvm.obj.*;

import java.util.*;

public class Executor {
    ScriptLoader main;
    public ScriptLoader executing;
    Stack<ExObject> op_stack;
    LibManager lib;
    EXThread thread;
    IntException ie;

    public void setStatus(EXThread.Status status){
        this.thread.status = status;
    }

    public IntException getIntException() {
        return ie;
    }

    public ScriptLoader getMain() {
        return main;
    }


    public ScriptLoader getExecuting() {
        return executing;
    }

    public Executor(ScriptLoader main){
        op_stack = new Stack<>();
        this.main = main;
        lib = LibManager.loadLibrary();
        ie = new IntException(this);
    }

    public void setThread(EXThread thread) {
        this.thread = thread;
    }

    public void push(ExObject obj){
        op_stack.push(obj);
    }
    public ExObject peek(){
        return op_stack.peek();
    }
    public ExObject pop(){
        return op_stack.pop();
    }

    private void alu(BaseCode bc,ScriptLoader exeing){
        if(bc instanceof AddCode){
            ExObject obj = pop();
            ExObject obj1 = pop();
            if(obj instanceof ExString||obj1 instanceof ExString){
                push(new ExString(obj1.getData() + obj.getData()));
                return;
            }
            if(obj instanceof ExDouble||obj1 instanceof ExDouble){
                push(new ExDouble(Double.parseDouble(obj1.getData()) + Double.parseDouble(obj.getData())));
                return;
            }
            push(new ExInt(Integer.parseInt(obj1.getData())+Integer.parseInt(obj.getData())));
        }else if(bc instanceof SubCode){
            ExObject obj = pop();
            ExObject obj1 = pop();
            if(obj instanceof ExString||obj1 instanceof ExString){
                ie.throwError(IntException.Error_Type.OPERATOR_TYPE_ERROR,"[STRING]类型的变量不能进行减数运算",bc,exeing);
            }
            if(obj instanceof ExDouble||obj1 instanceof ExDouble){
                push(new ExDouble(Double.parseDouble(obj1.getData()) - Double.parseDouble(obj.getData())));
                return;
            }
            push(new ExInt(Integer.parseInt(obj1.getData())-Integer.parseInt(obj.getData())));
        }else if(bc instanceof MulCode){
            ExObject obj = pop();
            ExObject obj1 = pop();
            if(obj instanceof ExString||obj1 instanceof ExString){
                ie.throwError(IntException.Error_Type.OPERATOR_TYPE_ERROR,"[STRING]类型的变量不能进行乘数运算",bc,exeing);
            }
            if(obj instanceof ExDouble||obj1 instanceof ExDouble){
                push(new ExDouble(Double.parseDouble(obj1.getData()) * Double.parseDouble(obj.getData())));
                return;
            }
            push(new ExInt(Integer.parseInt(obj1.getData())*Integer.parseInt(obj.getData())));
        }else if(bc instanceof DivCode){
            ExObject obj = pop();
            ExObject obj1 = pop();
            if(obj instanceof ExString||obj1 instanceof ExString){
                ie.throwError(IntException.Error_Type.OPERATOR_TYPE_ERROR,"[STRING]类型的变量不能除数减数运算",bc,exeing);
            }
            if(obj instanceof ExDouble||obj1 instanceof ExDouble){
                push(new ExDouble(Double.parseDouble(obj1.getData()) / Double.parseDouble(obj.getData())));
                return;
            }
            push(new ExInt(Integer.parseInt(obj1.getData())/Integer.parseInt(obj.getData())));
        }else if(bc instanceof EquCode){
            ExObject obj = pop();
            ExObject obj1 = pop();
            if(!obj.getType().equals(obj1.getType())){
                push(new ExBool(false));
                return;
            }
            push(new ExBool(obj.getData().equals(obj1.getData())));
        }else if(bc instanceof BigCode){
            ExObject obj = pop();
            ExObject obj1 = pop();
            if(obj.getType().equals(ExObject.Type.STRING)||obj1.getType().equals(ExObject.Type.STRING)) ie.throwError(IntException.Error_Type.OPERATOR_TYPE_ERROR,"[STRING]类型的变量不能进行比较运算",bc,exeing);
            if(!obj.getType().equals(obj1.getType())){
                push(new ExBool(false));
                return;
            }

            push(new ExBool(Double.parseDouble(obj1.getData())>Double.parseDouble(obj.getData())));
        } else if(bc instanceof LessCode){
            ExObject obj = pop();
            ExObject obj1 = pop();
            if(obj.getType().equals(ExObject.Type.STRING)||obj1.getType().equals(ExObject.Type.STRING)) ie.throwError(IntException.Error_Type.OPERATOR_TYPE_ERROR,"[STRING]类型的变量不能进行比较运算",bc,exeing);

            if(!obj.getType().equals(obj1.getType())){
                push(new ExBool(false));
                return;
            }
            push(new ExBool(Double.parseDouble(obj1.getData())<Double.parseDouble(obj.getData())));
        }else if(bc instanceof BigEquCode){
            ExObject obj = pop();
            ExObject obj1 = pop();
            if(obj.getType().equals(ExObject.Type.STRING)||obj1.getType().equals(ExObject.Type.STRING)) ie.throwError(IntException.Error_Type.OPERATOR_TYPE_ERROR,"[STRING]类型的变量不能进行比较运算",bc,exeing);

            if(!obj.getType().equals(obj1.getType())){
                push(new ExBool(false));
                return;
            }
            push(new ExBool(Double.parseDouble(obj1.getData())>=Double.parseDouble(obj.getData())));
        }else if(bc instanceof LessEquCode){
            ExObject obj = pop();
            ExObject obj1 = pop();
            if(obj.getType().equals(ExObject.Type.STRING)||obj1.getType().equals(ExObject.Type.STRING)) ie.throwError(IntException.Error_Type.OPERATOR_TYPE_ERROR,"[STRING]类型的变量不能进行比较运算",bc,exeing);
            if(!obj.getType().equals(obj1.getType())){
                push(new ExBool(false));
                return;
            }
            push(new ExBool(Double.parseDouble(obj1.getData())<=Double.parseDouble(obj.getData())));
        }else if(bc instanceof NotCode){
            ExObject obj = pop();

            if(!obj.getType().equals(ExObject.Type.BOOL))ie.throwError(IntException.Error_Type.OPERATOR_TYPE_ERROR,"["+obj.getType()+"]类型不能进行‘非’逻辑运算",bc,exeing);
            push(new ExBool(!Boolean.parseBoolean(obj.getData())));
        }else if(bc instanceof AndCode){
            ExObject obj = pop();
            ExObject obj1 = pop();
            if(!(obj.getType().equals(ExObject.Type.BOOL)&&obj1.getType().equals(ExObject.Type.BOOL))) ie.throwError(IntException.Error_Type.OPERATOR_TYPE_ERROR,"[UNKNOWN]类型的变量不能进行比较运算",bc,exeing);
            push(new ExBool(Boolean.parseBoolean(obj1.getData())&&Boolean.parseBoolean(obj.getData())));
        } else if(bc instanceof OrCode){
            ExObject obj = pop();
            ExObject obj1 = pop();
            if(!(obj.getType().equals(ExObject.Type.BOOL)&&obj1.getType().equals(ExObject.Type.BOOL))) ie.throwError(IntException.Error_Type.OPERATOR_TYPE_ERROR,"[UNKNOWN]类型的变量不能进行比较运算",bc,exeing);
            push(new ExBool(Boolean.parseBoolean(obj1.getData())||Boolean.parseBoolean(obj.getData())));
        }
    }

    public void start(){
        executing = main;
        byte jemp_index = 0;
        try {
            for (ListIterator<BaseCode> it = executing.getBcs().listIterator(); it.hasNext(); ) {
                BaseCode bc = it.next();
                if (jemp_index > 0) {
                    jemp_index -= 1;
                    continue;
                }
                if (bc instanceof PushCode) {
                    ExObject obj = null;
                    String data = executing.getTable().get(bc.getOpNum()).getExeData();


                    switch (executing.getTable().get(bc.getOpNum()).getType()) {
                        case CodeOptimization.ConstTableTask.STRING -> obj = new ExString(data);
                        case CodeOptimization.ConstTableTask.BOOL -> obj = new ExBool(Boolean.parseBoolean(data));
                        case CodeOptimization.ConstTableTask.INTEGER -> obj = new ExInt(Integer.parseInt(data));
                        case CodeOptimization.ConstTableTask.DOUBLE -> obj = new ExDouble(Double.parseDouble(data));
                        case CodeOptimization.ConstTableTask.NULL -> obj = new ExNull();
                        case CodeOptimization.ConstTableTask.VALUE_NAME -> {
                            for (ExValue ev : ScriptManager.values) {
                                if (ev.getName().equals(data)) {
                                    obj = ev.getValue(this);
                                    break;
                                }
                            }
                            for (ExValue ev : getExecuting().getPreValues()) {
                                if (ev.getName().equals(data)) {
                                    obj = ev.getValue(this);
                                    break;
                                }
                            }
                        }case CodeOptimization.ConstTableTask.LIST -> {

                            for (ExList ev : ScriptManager.lists) {
                                if (ev.getName().equals(data)) {
                                    obj = ev;
                                    break;
                                }
                            }
                            for (ExList ev : getExecuting().getPreLists()) {
                                if (ev.getName().equals(data)) {
                                    obj = ev;
                                    break;
                                }
                            }
                        }
                    }


                    push(obj);
                } else if (bc instanceof ImportCode) {
                    String name = executing.getTable().get(bc.getOpNum()).getExeData();
                    NativeLib n = null;
                    boolean isout = false;
                    for (NativeLib nl : lib.getLibs())
                        if (nl.getName().equals(name)) {
                            n = nl;
                            break;
                        }

                    for (ScriptLoader sl : ScriptManager.sls) {
                        if (sl.getLibName().equals(name)) {
                            isout = true;
                            break;
                        }
                    }


                    if (isout) continue;
                    if (n == null) ie.throwError(IntException.Error_Type.NOT_FOUND_LIB,"Not found lib:" + name,bc,executing);
                    ScriptManager.nls.add(n);
                } else if (bc instanceof InvokeCode) {
                    String path = executing.getTable().get(bc.getOpNum()).getExeData();

                    if (path.toCharArray()[0] == 'L') {
                        path = path.replaceFirst("L", "");
                        String lib = path.split("/")[0];
                        String name = path.split("/")[1];

                        NativeFunction nff = null;
                        for (NativeLib nl : ScriptManager.nls)
                            if (nl.getName().equals(lib)) {
                                for (NativeFunction nf : nl.getFunctions()) {
                                    if (nf.getName().equals(name)) {
                                        nff = nf;
                                        break;
                                    }
                                }
                            }

                        if (nff == null) {
                            int i = 1;
                            try {
                                for (FunctionGroup fg : ScriptManager.fgs) {
                                    String path_d;
                                    if (!executing.getLibName().equals(fg.getLib_name())) {
                                        ScriptLoader sl = null;
                                        for (ScriptLoader sm : ScriptManager.sls) {
                                            if (sm.getLibName().equals(fg.getLib_name())) {
                                                sl = sm;
                                                executing = sl;
                                            }
                                        }
                                        if (sl == null)
                                            ie.throwError(IntException.Error_Type.NOT_FOUND_LIB,"找不到指定库:" + fg.getLib_name(),bc,executing);
                                        path_d = sl.getTable().get(fg.getPath()).getExeData();

                                    } else path_d = executing.getTable().get(fg.getPath()).getExeData();

                                    if (path_d.toCharArray()[0] == 'L') {
                                        path_d = path_d.replaceFirst("L", "");
                                        String lib_d = path_d.split("/")[0];
                                        String name_d = path_d.split("/")[1];
                                        if (lib_d.equals(lib) && name_d.equals(name)) {
                                            subExecutor(fg.getBcs(), executing, path_d);
                                            i = 0;
                                            break;
                                        }
                                    } else ie.throwError(IntException.Error_Type.FUNCTION_PATH_ERROR,"函数调用路径类型不正确:" + path_d,bc,executing);
                                }
                            } catch (IndexOutOfBoundsException e) {
                                ie.throwError(IntException.Error_Type.FUNCTION_PATH_ERROR,"无法找到函数索引:" + bc.getOpNum() + ",请检查函数定义是否在调用语句前.",bc,executing);
                            }

                            executing = main;

                            if (i == 0) continue;
                        }

                        executing = main;

                        if (nff == null) ie.throwError(IntException.Error_Type.NOT_FOUND_FUNCTION,"Not found function:(" + path + ")",bc,executing);
                        nff.invoke(this);
                    }
                } else if (bc instanceof LoadCode) {
                    ExValue ev = new ExValue(executing.getTable().get(bc.getOpNum()).getExeData(), 1);
                    ev.setValue(pop());
                    executing.getPreValues().add(ev);
                } else if (bc instanceof MovCode) {
                    ExValue ev = new ExValue(executing.getTable().get(bc.getOpNum()).getExeData(), 1);
                    ev.setValue(pop());
                    ScriptManager.values.add(ev);
                } else if (bc instanceof ALUCode) alu(bc,executing);
                else if (bc instanceof JempCode) {
                    ExObject obj = pop();
                    if (obj.getType().equals(ExObject.Type.BOOL)) {
                        if (!Boolean.parseBoolean(obj.getData())) {
                            jemp_index = bc.getOpNum();

                        }
                    }
                } else if (bc instanceof RejmpCode) {
                    ArrayList<BaseCode> bools = new ArrayList<>(), blocks = new ArrayList<>();
                    for (int i = 0; i < ((RejmpCode) bc).getIndexBool(); i++) bools.add(it.next());
                    for (int i = 0; i < ((RejmpCode) bc).getIndexBlock(); i++) blocks.add(it.next());

                    while (true) {
                        subExecutor(bools, executing, "<while_group>");
                        ExObject o = pop();
                        if (o.getType().equals(ExObject.Type.BOOL)) {
                            if (!Boolean.parseBoolean(o.getData())) break;
                        } else ie.throwError(IntException.Error_Type.OPERATOR_TYPE_ERROR,"while语句布尔表达式解析有误",bc,executing);

                        subExecutor(blocks, executing, "<while_group>");
                    }
                } else if (bc instanceof ReLoadCode) {
                    ExValue ev = null;
                    for (ExValue evv : executing.getPreValues()) {
                        if (evv.getName().equals(executing.getTable().get(bc.getOpNum()).getExeData())) {
                            ev = evv;
                            evv.setValue(pop());
                            break;
                        }
                    }
                    if (ev == null) {
                        for (ExValue evv : ScriptManager.values) {
                            if (evv.getName().equals(executing.getTable().get(bc.getOpNum()).getExeData())) {
                                evv.setValue(pop());
                                break;
                            }
                        }
                    }
                }else if(bc instanceof LListCode) {
                    ExList ev = new ExList(executing.getTable().get(bc.getOpNum()).getExeData(), 1);
                    executing.getPreLists().add(ev);
                }else if(bc instanceof MListCode){
                    ExList ev = new ExList(executing.getTable().get(bc.getOpNum()).getExeData(), 1);
                    ScriptManager.lists.add(ev);
                }else if(bc instanceof ThrowCode){
                    String name = executing.getTable().get(bc.getOpNum()).getExeData();
                    ie.throwError(IntException.Error_Type.valueOf(name),"<code_throw>",bc,executing);
                }else if(bc instanceof JmpCode){
                    jemp_index = bc.getOpNum();
                    continue;
                }
            }
        }catch (EmptyStackException e){
            ie.throwError(IntException.Error_Type.STACK_ERROR,"EXECUTOR操作栈异常,可能运算出现问题或一个函数并没有返回值",new PushCode((byte) 0),executing);
        }catch (NullPointerException e){
            ie.throwError(IntException.Error_Type.NULL_PRINT_ERROR,"EXECUTOR内部发生空指针异常,可能加载了一个不存在的变量导致",new LoadCode((byte) 0),executing);
        }
    }


    public void subExecutor(ArrayList<BaseCode> bcs,ScriptLoader exeing,String funcname){
        try {
            byte jemp_index = 0;

            for (ListIterator<BaseCode> it = bcs.listIterator(); it.hasNext(); ) {
                BaseCode bc = it.next();
                if(jemp_index > 0){
                    jemp_index -= 1;
                    continue;
                }
                if (bc instanceof PushCode) {
                    ExObject obj = new ExObject();
                    String data = exeing.getTable().get(bc.getOpNum()).getExeData();

                    switch (exeing.getTable().get(bc.getOpNum()).getType()) {
                        case CodeOptimization.ConstTableTask.STRING -> obj = new ExString(data);
                        case CodeOptimization.ConstTableTask.BOOL -> obj = new ExBool(Boolean.parseBoolean(data));
                        case CodeOptimization.ConstTableTask.INTEGER -> obj = new ExInt(Integer.parseInt(data));
                        case CodeOptimization.ConstTableTask.DOUBLE -> obj = new ExDouble(Double.parseDouble(data));
                        case CodeOptimization.ConstTableTask.NULL -> obj = new ExNull();
                        case CodeOptimization.ConstTableTask.VALUE_NAME -> {
                            for (ExValue ev : ScriptManager.values) {
                                if (ev.getName().equals(data)) {
                                    obj = ev.getValue(this);
                                    break;
                                }
                            }
                            for (ExValue ev : getExecuting().getPreValues()) {
                                if (ev.getName().equals(data)) {
                                    obj = ev.getValue(this);
                                    break;
                                }
                            }
                        }case CodeOptimization.ConstTableTask.LIST -> {
                            for (ExList ev : ScriptManager.lists) {
                                if (ev.getName().equals(data)) {
                                    obj = ev;
                                    break;
                                }
                            }
                            for (ExList ev : getExecuting().getPreLists()) {
                                if (ev.getName().equals(data)) {
                                    obj = ev;
                                    break;
                                }
                            }
                        }
                    }


                    push(obj);
                } else if (bc instanceof InvokeCode) {

                    String path = exeing.getTable().get(bc.getOpNum()).getExeData();

                    if (path.toCharArray()[0] == 'L') {
                        path = path.replaceFirst("L", "");
                        String lib = path.split("/")[0];
                        String name = path.split("/")[1];

                        NativeFunction nff = null;
                        for (NativeLib nl : ScriptManager.nls)
                            if (nl.getName().equals(lib)) {
                                for (NativeFunction nf : nl.getFunctions()) {
                                    if (nf.getName().equals(name)) {
                                        nff = nf;
                                        break;
                                    }
                                }
                            }

                        if (nff == null) {
                            int i = 1;
                            for (FunctionGroup fg : ScriptManager.fgs) {
                                String path_d;
                                if (!exeing.getLibName().equals(fg.getLib_name())) {
                                    ScriptLoader sl = null;
                                    for (ScriptLoader sm : ScriptManager.sls) {
                                        if (sm.getLibName().equals(fg.getLib_name())) {
                                            sl = sm;
                                        }
                                    }
                                    if (sl == null) ie.throwError(IntException.Error_Type.NOT_FOUND_LIB,"找不到指定库:" + fg.getLib_name(),bc,exeing);
                                    path_d = sl.getTable().get(fg.getPath()).getExeData();
                                } else path_d = exeing.getTable().get(fg.getPath()).getExeData();

                                if (path_d.toCharArray()[0] == 'L') {
                                    path_d = path_d.replaceFirst("L", "");
                                    String lib_d = path_d.split("/")[0];
                                    String name_d = path_d.split("/")[1];
                                    if (lib_d.equals(lib) && name_d.equals(name)) {
                                        ScriptLoader buffer = null;

                                        for (ScriptLoader sl : ScriptManager.sls)
                                            if (sl.getLibName().equals(fg.getLib_name())) {
                                                buffer = exeing;
                                                exeing = sl;
                                            }

                                        subExecutor(fg.getBcs(), exeing, path_d);
                                        i = 0;
                                        exeing = buffer;
                                        break;
                                    }
                                } else ie.throwError(IntException.Error_Type.FUNCTION_PATH_ERROR,"函数调用路径类型不正确:" + path_d,bc,exeing);
                            }
                            if (i == 0) continue;
                        }


                        if (nff == null) ie.throwError(IntException.Error_Type.NOT_FOUND_FUNCTION,"Not found function:(" + path + ")",bc,exeing);

                        nff.invoke(this);
                    }
                }else if(bc instanceof LListCode) {
                    ExList ev = new ExList(exeing.getTable().get(bc.getOpNum()).getExeData(), 1);
                    executing.getPreLists().add(ev);
                }else if(bc instanceof MListCode){
                    ExList ev = new ExList(exeing.getTable().get(bc.getOpNum()).getExeData(), 1);
                    ScriptManager.lists.add(ev);
                } else if (bc instanceof LoadCode) {
                    ExValue ev = new ExValue(exeing.getTable().get(bc.getOpNum()).getExeData(), 1);
                    ev.setValue(pop());
                    executing.getPreValues().add(ev);

                } else if (bc instanceof MovCode) {
                    ExValue ev = new ExValue(exeing.getTable().get(bc.getOpNum()).getExeData(), 1);
                    ev.setValue(pop());
                    ScriptManager.values.add(ev);
                } else if (bc instanceof ALUCode) alu(bc,exeing);
                else if (bc instanceof JempCode) {
                    ExObject obj = pop();
                    if (obj.getType().equals(ExObject.Type.BOOL)) {
                        if (!Boolean.parseBoolean(obj.getData())) {
                            jemp_index = bc.getOpNum();
                        }
                    }
                }else if (bc instanceof RejmpCode) {
                    ArrayList<BaseCode> bools = new ArrayList<>(),blocks = new ArrayList<>();
                    for (int i = 0; i < ((RejmpCode) bc).getIndexBool(); i++) bools.add(it.next());
                    for (int i = 0; i < ((RejmpCode) bc).getIndexBlock(); i++) blocks.add(it.next());

                    while (true){
                        subExecutor(bools,exeing,"<while_group>");
                        ExObject o = pop();
                        if(o.getType().equals(ExObject.Type.BOOL)){
                            if(!Boolean.parseBoolean(o.getData()))break;
                        }else ie.throwError(IntException.Error_Type.OPERATOR_TYPE_ERROR,"while语句布尔表达式解析有误",bc,exeing);

                        subExecutor(blocks,exeing,"<while_group>");
                    }
                }else if(bc instanceof ReLoadCode){
                    ExValue ev = null;
                    for(ExValue evv:executing.getPreValues()){
                        if (evv.getName().equals(exeing.getTable().get(bc.getOpNum()).getExeData())){
                            ev = evv;
                            evv.setValue(pop());
                            break;
                        }
                    }
                    if(ev == null) {
                        for (ExValue evv : ScriptManager.values) {
                            if (evv.getName().equals(exeing.getTable().get(bc.getOpNum()).getExeData())) {
                                ev = evv;
                                evv.setValue(pop());
                                break;
                            }
                        }
                    }
                    if(ev == null)ie.throwError(IntException.Error_Type.NOT_FOUND_VALUE_ERROR,"找不到指定变量:"+exeing.getTable().get(bc.getOpNum()).getExeData(),bc,exeing);
                }else if(bc instanceof RetCode){
                    return;
                }else if(bc instanceof JmpCode){
                    for (int i = 0; i < bc.getOpNum(); i++) bc = it.next();
                }
            }
        }catch (EmptyStackException ese){
            ie.throwError(IntException.Error_Type.FUNCTION_PATH_ERROR,"函数调用参数不正确:"+funcname,new InvokeCode((byte) 0),exeing);
        }
    }
}
