import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class XML{

    public static ArrayList<Server> parse(String xmlConfigurationPath) throws FileNotFoundException {

        ArrayList<Server> serverList = null;
        try {
            serverList = new ArrayList<>();

            File xmlConfigurationFile = new File(xmlConfigurationPath);

            DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
            DocumentBuilder factory = builder.newDocumentBuilder();

            Document document = factory.parse(xmlConfigurationFile);

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xPath = xPathfactory.newXPath();

            XPathExpression xPathExp = xPath.compile("//system/servers/server[@type]");
            NodeList nodes = (NodeList) xPathExp.evaluate(document, XPathConstants.NODESET);

            for (int temp = 0; temp < nodes.getLength(); temp++) {

                Node node = nodes.item(temp);

                String type = node.getAttributes().getNamedItem("type").getNodeValue();
                int limit = Integer.parseInt(node.getAttributes().getNamedItem("limit").getNodeValue());
                int bootupTime = Integer.parseInt(node.getAttributes().getNamedItem("bootupTime").getNodeValue());
                float hourlyRate = Float.parseFloat(node.getAttributes().getNamedItem("hourlyRate").getNodeValue());
                int coreCount = Integer.parseInt(node.getAttributes().getNamedItem("coreCount").getNodeValue());
                int memory = Integer.parseInt(node.getAttributes().getNamedItem("memory").getNodeValue());
                int disk = Integer.parseInt(node.getAttributes().getNamedItem("disk").getNodeValue());

                Server s = new Server(type, limit, bootupTime, hourlyRate, coreCount, memory, disk);
                serverList.add(s);
            }
        } catch (FileNotFoundException e) {
            // Do not catch file not found exception, as this should be handled by calling class
            throw e;
        } catch (Exception e) {
            // All other exceptions should exit the program immediately.
            java.lang.System.out.println("FATAL: Unexpected exception in XML parser");
            e.printStackTrace();
            java.lang.System.exit(-1);
        }
        return serverList;
    }
}