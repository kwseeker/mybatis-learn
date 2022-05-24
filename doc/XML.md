# XML

XML(eXtensible Markup Language, 可扩展标记语言)是用于定义词汇表(自定义标记语言)的元语言(用于描述其他语言的语言)，这是XML的重要性和流行的关键。

XML是一种树结构。

## XML的组成结构 & 特点

### XML组成结构

+ **声明**（Declaration）

  ```xml
  <?xml version="1.0" encoding="ISO-8859-1"?>
  ```

  其中`version`属性定义支持XML规范的版本， `encoding`指定编码格式。

  还有一种可选的`standalone`属性，只与DTDs相关。

+ **元素**（Element）和**属性**（ Attribute）

  元素是启动标签和结束标签括起来的部分，或者是一个空标签。

+ **字符引用**（Character Reference）和**CDATA区段**

  有些字面量不能出现在元素或属性值中，比如“<”, 需要使用字符引用替换；

  CDATA 部分中的所有内容都会被解析器忽略。

+ **命名空间**（Namespace）

  用于引用外部XML时避免命名冲突，

+ **处理指令**

  ```xml
  <? ... ?>
  比如：
  <?xmlstylesheet href="modern.xsl" type="text/xml"?> 
  <?php /* PHP code */ ?>
  ```

+ **语法规则定义**

  + **DTD** (Doucument Type Definition)

    是XML文档的语法文档，用于定义XML语法检查XML内容是否有效。

    ```xml
    <?xml version="1.0"?>
    <!DOCTYPE note [
      <!ELEMENT note (to,from,heading,body)>
      <!ELEMENT to      (#PCDATA)>
      <!ELEMENT from    (#PCDATA)>
      <!ELEMENT heading (#PCDATA)>
      <!ELEMENT body    (#PCDATA)>
    ]>
    <note>
      <to>George</to>
      <from>John</from>
      <heading>Reminder</heading>
      <body>Don't forget the meeting!</body>
    </note>
    ```

    以上 DTD 解释如下：

    !DOCTYPE note (第二行)定义此文档是 note 类型的文档。

    !ELEMENT note (第三行)定义 note 元素有四个元素："to、from、heading,、body"

    !ELEMENT to (第四行)定义 to 元素为 "#PCDATA" 类型

    !ELEMENT from (第五行)定义 from 元素为 "#PCDATA" 类型

    !ELEMENT heading (第六行)定义 heading 元素为 "#PCDATA" 类型

    !ELEMENT body (第七行)定义 body 元素为 "#PCDATA" 类型

    PCDATA 是会被解析器解析的文本。这些文本将被解析器检查实体以及标记。

    > 所有的 XML 文档（以及 HTML 文档）均由以下简单的构建模块构成：
    >
    > + 元素 <!ELEMENT 元素名称 类别> 或 <!ELEMENT 元素名称 (元素内容)>
    > + 属性 <!ATTLIST 元素名称 属性名称 属性类型 默认值>
    > + 实体 <!ENTITY 实体名称 "实体的值">
    > + PCDATA
    > + CDATA

  + **XML Schema**

    XML Schema 是基于 XML 的 DTD 替代者。也称作 XML Schema 定义（XML Schema Definition，XSD）。

+ **CSS样式**

## XML解析-SAX

Simple API for XML (SAX) is an event-based Java API for parsing an XML document sequentially from start to finish. 

Java SAX API : 

```java
javax.xml.parsers.SAXParser;
javax.xml.parsers.SAXParserFactory;
```



## XML解析与创建-DOM

Document Object Model (DOM) is a Java API for parsing an XML document into an in-memory tree of nodes and for creating an XML document from a node tree.

Java DOM API:

```java
javax.xml.parsers.DocumentBuilder;
javax.xml.parsers.DocumentBuilderFactory;
```



## XML解析与创建-StAX

Streaming API for XML (StAX) is a Java API for parsing an XML document sequentially from start to finish and also for creating XML documents. 

Java DOM API:

```java
package javax.xml.stream;
package javax.xml.stream.events;
package javax.xml.stream.util;
```



## 选择节点-XPath

XPath是一种非XML声明性查询语言(由W3C定义)，可以用于简化定位查询DOM树的节点。

Java XPath API:

```java
package javax.xml.xpath;
```



## XML转换-XSLT

XSLT 是首选的 XML 样式表语言。XSLT (eXtensible Stylesheet Language Transformations) 远比 CSS 更加完善。



## 参考资料

+ [XML教程](https://www.w3school.com.cn/xml/index.asp) 适合快速入门

+ 《Java XML and JSON Document Processing for Java SE》Part I
+ 《Beginning XML》