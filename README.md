# OpenEXScript
* EX编程语言全新4.0系列

<hr>

<p>我们使用GPLv2.0协议进行开源,源代码供参考和学习使用. OpenEX是一个编译解释一体化的脚本语言，其目的并不是为了嵌入式开发或者程序设计，而是供于初学者参考学习：如何自制一个编程语言,我们不建议您使用该语言写程序</p>

> 使用方法请参考<code>api.md</code>文件内容

<hr>

## OpenEX使用更改
* 新版本名称规则更改:
> 解释器与编译器版本分离:
>> 解释器OpenEXVirtualMachine-v(版本号)\
>> 编译器EXScriptCompile-v(版本号)

* 新版指令参数增加以及优化:
> -filename:文件名 '添加一个脚本文件'\
> -binary:文件名 '加载二进制字节码文件'
>> 注意! 一旦增加二进制字节码文件, -filename所添加的文件全部作废
>
> -compile '将所有-filename添加的文件编译成二进制文件'\
> -loadlib:文件名 '加载一个JAR插件作为外部库'\
> -version '显示目前的解释器和编译器版本'
* 新版语法升级:
```js
value local name:"增加了函数+算式的初始值" = exe.system.memory()+1;
list local name_list:"增加了列表类型定义,具体列表操作在util库中的函数";

function main(out,in){
    //全新的函数形参定义以及获取
    //废除了3.0版本的VM库
    exe.system.print(out);
    exe.system.print(in);
}

// 线程名含有SHUTDOWN字母的将作为ShutdownHook添加进线程池,并不会立即启动
// 方法名严格按照'L库名\方法名'的方式添加,不然无法解析
exe.system.thread("线程名","启动方法名");

//新版函数传参无需前置参数标签,直接如JS一样传入即可
//函数参数可加算式
exe.this.main(exe.system.memory()*2,3*(23+4));

//新版支持else语句的使用,经过作者粗略测试，目前没发现BUG
if(true|false){
}else{
}
```
* 新版外部库API更换
> 主类继承<code>ex.exvm.plugin.NativePlugin</code>抽象类并实现其方法\
> 函数实现直接继承<code>ex.exvm.lib.NativeFunction</code>抽象类并实现其方法\
> 配置文件格式不变

<hr>

## OpenEX内部更改

### 编译器
* 修改了BasicParser语法解析器架构,以便适应新版语法
* 修改了算式解析器的架构,方便处理函数参数,变量参数初始值的新版语法
* 新增中间代码转换器,将抽象语法树转换出来的中间代码进一步编译成底层字节码
* 编译器整体被归到<code>ex.openex</code>包下

### 解释器
* 修改了线程池管理机制
* 新增中断处理器,修改异常处理方式(该设想由unknown提供)
* Executor架构更新,新增常量池表与ScriptLoader
* Lib加载器架构重写,去掉繁琐的API,改用每个库一个类
* 增加了ShutdownHook机制,虚拟机正常关闭或由system.shutdown关闭时执行ShutdownHook线程
* 解释器整体被归到<code>ex.exvm</code>包下

<hr>

## 致谢名单
> 感谢以下个人或团体为EX开发做出的贡献
* 去幻想乡的老ART-EX语言语法制定与规划
* flysong-EX虚拟机架构指导
* 芝士傻逼-EX虚拟机BUG协助修复
* Linuxer-其自制超文本标记语言兼容EX
* unknown-异常处理中断版的概念设想
* 桃乃月月子-部分本地库函数制定
* (团体)ZCS团队-帮助宣传EX编程语言
* (团体)DotCS开发组-EX编程语言制作方

<hr>

## 历史更新

* v0.4.0 -初代4.0版本更新,移植3.9代码到新项目
* v0.4.1 -增加列表类型,由list定义
* v0.4.2 -更改虚拟机执行引擎架构,增加常量池概念
* v0.4.3 -更新出中间代码编译器
* v0.4.4 -更新出二进制字节码文件转换器
* v0.4.5 -重写虚拟机线程池架构,增加中断处理器概念
* v0.4.6 -重新移植catch,while,if语句
* v0.4.7 -更改变量初始值语法，升级语法(可在算式中加入函数调用)
* v0.4.8 -更新函数传参规则,废除参数标签以及vm库
* v0.4.9 -更新system库的input函数以及math库
* v0.4.9(v0.3.0) -编译器更新:支持if else语句
