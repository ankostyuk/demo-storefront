package ru.nullpointer.cdn.tags;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.lang.Validate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Alexander Yastrebov
 */
class ConfigParser {

    private ConfigParser() {
    }

    static Config parseConfig(String configLocation, ServletContext servletContext) throws Exception {
        Validate.notEmpty(configLocation);

        InputStream is = servletContext.getResourceAsStream(configLocation);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(is);

        Config config = parse(doc);

        is.close();
        return config;
    }

    private static Config parse(Document doc) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Map<String, Container> containerMap = new HashMap<String, Container>();

        Element root = doc.getDocumentElement();
        NodeList containerNodeList = root.getElementsByTagName("container");
        for (int i = 0; i < containerNodeList.getLength(); i++) {
            Element containerItem = (Element) containerNodeList.item(i);
            NodeList propertyNodeList = containerItem.getElementsByTagName("property");

            String name = containerItem.getAttribute("name");
            Validate.notEmpty(name);
            Validate.isTrue(!containerMap.containsKey(name));

            String className = containerItem.getAttribute("class");
            Validate.notEmpty(className);
            className = className.trim();

            Class clazz = Class.forName(className);
            Validate.isTrue(Container.class.isAssignableFrom(clazz));

            Container container = (Container) clazz.newInstance();

            Map<String, String> properties = new HashMap<String, String>();
            for (int j = 0; j < propertyNodeList.getLength(); j++) {
                Element propertyItem = (Element) propertyNodeList.item(j);

                String propName = propertyItem.getAttribute("name");
                Validate.notEmpty(propName);
                propName = propName.trim();

                String propValue = propertyItem.getAttribute("value");

                properties.put(propName, propValue);
            }

            container.setProperties(properties);
            containerMap.put(name, container);
        }
        return new Config(containerMap);
    }
}
