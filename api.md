# OpenEX ApplicationProgramInterace
* OpenEX-v0.4.8-JavaEdition
* OpenEXv4.0系列使用文档

<hr>

* 请使用JRE或JDk17以上环境运行
* <code>java -jar OpenEX-v(版本号).jar -命令参数</code>
* 本程序可以运行在任何具有JAVA运行环境的操作系统中,无需下载第三方JAR库
* 目前已知可运行系统<code>Windows,Linux,MacOS,UNIX</code>

<hr>

## system (系统库)
|函数名|参数|返回值|用法|备注|
|:---|:---|:---|:---|:---|
|print|string|void|控制台标准输出|-|
|input|void|string|获取控制台输入|-|
|shutdown|integer|void|关闭程序|-|
|thread|string<code>线程名</code>,string<code>运行方法路径</code>|void|向线程池创建一个新的线程|-|

## math (数学处理)
|函数名|参数|返回值|用法|备注|
|:---|:---|:---|:---|:---|
|sin|double|double|返回角的三角正弦值，参数以孤度为单位|-|
|cos|double|double|返回角的三角余弦值，参数以孤度为单位|-|
|tan|double|double|返回角的三角正切值，参数以弧度为单位|-|
|cbrt|double|double|返回一个数字的平方根|-|
|sqrt|double|double|返回一个数字的立方根|-|

## util(工具库)
|函数名|参数|返回值|用法|备注|
|:---|:---|:---|:---|:---|
|add_list|list<code>列表</code>,obj<code>任意类型值</code>|void|向指定列表添加一个元素|-|
|set_list|list<code>列表</code>,int<code>索引</code>,obj<code>任意类型值</code>|void|设置列表指定元素的数据|-|
|remove_list|list<code>列表</code>,obj<code>任意类型值</code>|void|删除列表指定元素|
|get_list|list<code>列表</code>,int<code>索引</code>|obj|获取列表指定元素|


<hr>

# 关键字与保留字
|符号|作用|备注|
|:---|:---|:---|
|value|定义一个变量|-|
|list|定义一个列表|-|
|local|私有访问修饰符|-|
|global|公共访问修饰符|-|
|while|循环定义|-|
|if|判断定义|-|
|true|'真'布尔值|-|
|false|'假'布尔值|-|
|null|空值|-|
|include|导入库|-|
|return|函数返回|-|
|break|循环退出|保留字,实际循环并没有对break的处理能力|
|exe|函数调用头|-|
|function|函数定义|
|catch|中断表定义|-|
|throw|触发一个中断|-|
|this|代表代码所在的ScriptLoader|一个脚本文件对应一个ScriptLoader|

<hr>

# 布尔表达式
> v : 变量或其他布尔表达式

|符号|说明|用法|备注|
|:---|:---|:---|:---|
|!|结果取反|!v|-|
|&|与比较|v&v|-|
|\||或比较|v\|v|-|

<hr>

# 代码示例

> 定义
>> 变量 value local/global 名字:"备注" = 初始值;
``` js
value local name:"局部变量" = 10; //线程私有,一条线程内的脚本可以相互使用
value gobal name1:"全局变量" = true; //线程公共,不同线程可以相互使用
name = 20; //更改变量初始值
name1 = 10; //不建议,可能会抛出类型不匹配异常
```

>> 函数 function 函数名(参数...) {代码体}
```js
function name(){
    /*CODE*/
}
```

> 调用
>> 外部库
>>> 函数 exe.库名.函数名(形参值...);
```js
exe.system.print("Hello! World!");
exe.this.name(); //调用脚本函数
```

> 流程控制
>> if 
>>> if(boolean){/*code*/} 
```js
if(1+1==2){
    exe.system.print("Hello! World!");
}
```
>> while
>>> while(boolean) {/*code*/}
```js
while(true){
    exe.system.print("Hello! World!");
}
```

<hr>

# 注意事项
* OpenEX编译器尽最大努力支持某些高级语法以及语法糖
* 部分语法报错可能不准确，蛋事不代表没有语法错误(
* 作者能力有限，欢迎大佬来帮忙完善或技术指导
* 以下是不支持的高级语法或语法糖
```js
if(exe.this.name() == null) /*CODE*/; //不能在if或while语句布尔表达式内直接引用函数
// 正确写法:
value a:"备注" = exe.this.name() == null;
if(a) /*CODE*/
```



