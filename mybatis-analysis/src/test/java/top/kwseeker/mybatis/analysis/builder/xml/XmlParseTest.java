package top.kwseeker.mybatis.analysis.builder.xml;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.parsing.PropertyParser;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.Properties;

/**
 * 抽离出的MyBatis解析XML配置的过程
 */
public class XmlParseTest {

    private static final String CONFIG_XML = "mybatis-config.xml";

    /**
     * XML解析
     * 解析的细节也是挺复杂的，有空再看吧，可以参考XML.md引用的那两本书
     */
    @Test
    public void testParseXml() throws Exception {
        InputStream inputStream = Resources.getResourceAsStream(CONFIG_XML);

        // 1 创建 XPath
        XPathFactory factory = XPathFactory.newInstance();  //关键代码
        XPath xPath = factory.newXPath();   //关键代码
        // 2 创建 Document 文档
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();   //关键代码
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);    //Features校验
        documentBuilderFactory.setValidating(true); //根据DTDs校验文档
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setIgnoringComments(true);
        documentBuilderFactory.setIgnoringElementContentWhitespace(false);
        documentBuilderFactory.setCoalescing(false);
        documentBuilderFactory.setExpandEntityReferences(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();  //关键代码
        EntityResolver entityResolver = new XMLMapperEntityResolver();
        documentBuilder.setEntityResolver(entityResolver);
        documentBuilder.setErrorHandler(new ErrorHandler() {
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }
            public void warning(SAXParseException exception) throws SAXException {
            }
        });
        Document document = documentBuilder.parse(new InputSource(inputStream));    //关键代码

        // 2 解析根节点 /configuration
        Node configurationNode = (Node) xPath.evaluate("/configuration", document, XPathConstants.NODE);
        //  XNode
        String configurationNodeName = configurationNode.getNodeName();
        Properties ConfigurationNodeAttrs = parseAttributes(configurationNode, null);   //configuration tag 没有属性

        // 3 parseConfiguration
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        //properties
        Node propertiesNode = (Node) xPath.evaluate("properties", configurationNode, XPathConstants.NODE);
        String propertiesNodeName = propertiesNode.getNodeName();
        Properties propertiesNodeAttrs = parseAttributes(propertiesNode, null); //properties tag 没有属性
        //...
        //mapper
        Node mappersNode = (Node) xPath.evaluate("mappers", configurationNode, XPathConstants.NODE);
        String mappersNodeName = mappersNode.getNodeName();
        Properties mappersNodeAttrs = parseAttributes(mappersNode, null);
        //<mappers>
        //    <mapper resource="mapper/BlogMapper.xml"/>
        //</mappers>
        NodeList children = mappersNode.getChildNodes();    // 3个子点　"[#text:\n]" "[mapper:null]" "[#text:\n]"
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Properties attrs = parseAttributes(node, null);
                if ("package".equals(node.getNodeName())) {         //<package name="">
                    //这个应该是注解形式吧（TODO 只是猜想待确认）
                    String mapperPackage = attrs.getProperty("name");
                    configuration.addMappers(mapperPackage);
                } else {                                            //<mapper resource="" url="" class="">
                    String resource = attrs.getProperty("resource");
                    String url = attrs.getProperty("url");
                    String mapperClass = attrs.getProperty("class");
                    if (resource != null && url == null && mapperClass == null) {   //项目中多用这种
                        System.out.println("加载本地Mapper文件并解析到configuration");
                        //XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                        //mapperParser.parse();
                    } else if (resource == null && url != null && mapperClass == null) {
                        System.out.println("URL加载远程Mapper文件并解析到configuration");
                        //XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
                        //mapperParser.parse();
                    } else if (resource == null && url == null && mapperClass != null) {
                        //通过接口名加载接口类, 这个应该是注解形式吧（TODO 只是猜想待确认）
                        System.out.println("Load Mapper Interfaces");
                        //Class<?> mapperInterface = Resources.classForName(mapperClass);
                        //configuration.addMapper(mapperInterface);
                    } else {
                        throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
                    }
                }
            }
        }
    }

    @Test
    public void testParseMapperXML() {

    }

    private Properties parseAttributes(Node n, Properties variables) {
        Properties attributes = new Properties();
        NamedNodeMap attributeNodes = n.getAttributes();
        if (attributeNodes != null) {
            for (int i = 0; i < attributeNodes.getLength(); i++) {
                Node attribute = attributeNodes.item(i);
                String value = PropertyParser.parse(attribute.getNodeValue(), variables);
                attributes.put(attribute.getNodeName(), value);
            }
        }
        return attributes;
    }
}
