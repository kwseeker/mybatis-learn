package top.kwseeker.mybatis.analysis.builder.xml;

import org.apache.ibatis.io.Resources;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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

    @Test
    public void testParseXml() throws Exception {
        InputStream inputStream = Resources.getResourceAsStream(CONFIG_XML);

        // 1 创建 XPath 和 Document 文档 (javax拓展库)
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setIgnoringComments(true);
        documentBuilderFactory.setIgnoringElementContentWhitespace(false);
        documentBuilderFactory.setCoalescing(false);
        documentBuilderFactory.setExpandEntityReferences(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        documentBuilder.setEntityResolver(null);
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
        Document document = documentBuilder.parse(new InputSource(inputStream));

        // 2 evalNode
        Node configNode = (Node) xPath.evaluate("/configuration", document, XPathConstants.NODE);
        //  XNode
        String configNodeName = configNode.getNodeName();
        Properties configNodeAttributes = new Properties();
        NamedNodeMap attributeNodes = configNode.getAttributes();
        if (attributeNodes != null) {
            for (int i = 0; i < attributeNodes.getLength(); i++) {
                Node attribute = attributeNodes.item(i);
                String value = org.apache.ibatis.parsing.PropertyParser.parse(attribute.getNodeValue(), null);
                configNodeAttributes.put(attribute.getNodeName(), value);
            }
        }

        // 3 parseConfiguration
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        //properties
        Node propertiesNode = (Node) xPath.evaluate("properties", configNodeName, XPathConstants.NODE);
        Properties propertiesAttributes = new Properties();
        attributeNodes = propertiesNode.getAttributes();
        if (attributeNodes != null) {
            for (int i = 0; i < attributeNodes.getLength(); i++) {
                Node attribute = attributeNodes.item(i);
                String value = org.apache.ibatis.parsing.PropertyParser.parse(attribute.getNodeValue(), null);
                propertiesAttributes.put(attribute.getNodeName(), value);
            }
        }
        //mapper

    }
}
