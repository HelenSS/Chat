package storage;

import model.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class XMLHistoryUtil {
    private static final String STORAGE_LOCATION ="C:\\history.xml";
    private static final String MESSAGES = "messages";
    private static final String MESSAGE = "message";
    private static final String ID = "id";
    private static final String USER = "user";
    private static final String MESSAGETEXT = "messageText";

    private XMLHistoryUtil() {
    }

    public static synchronized void createStorage() throws ParserConfigurationException, TransformerException{
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = docFactory.newDocumentBuilder();

        Document doc = documentBuilder.newDocument();
        Element rootElement = doc.createElement(MESSAGES);
        doc.appendChild(rootElement);

        Transformer transformer = getTransformer();

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
        transformer.transform(source, result);
    }

    public static synchronized void addData (Message message) throws ParserConfigurationException, IOException, TransformerException, org.xml.sax.SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();

        Element messageElement = document.createElement(MESSAGE);
        root.appendChild(messageElement);

        messageElement.setAttribute(ID, message.getId());

        Element user = document.createElement(USER);
        user.appendChild(document.createTextNode(message.getUser()));
        messageElement.appendChild(user);

        Element messageText = document.createElement(MESSAGETEXT);
        messageText.appendChild(document.createTextNode(message.getMessageText()));
        messageElement.appendChild(messageText);

        DOMSource source = new DOMSource(document);

        Transformer transformer = getTransformer();

        StreamResult result = new StreamResult(STORAGE_LOCATION);
        transformer.transform(source, result);
    }

    public static synchronized void updateData(Message message) throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException, org.xml.sax.SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Node messageToUpdate = getNodeById(document, message.getId());

        if (messageToUpdate != null) {

            NodeList childNodes = messageToUpdate.getChildNodes();

            for (int i=0; i < childNodes.getLength(); i++) {

                Node node = (Node) childNodes.item(i);

                if (USER.equals(node.getNodeName())) {
                    node.setTextContent(message.getUser());
                }

                if (MESSAGETEXT.equals(node.getNodeName())) {
                    node.setTextContent(message.getMessageText());
                }
            }

            Transformer transformer = getTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult((new File(STORAGE_LOCATION)));
            transformer.transform(source, result);
        } else {
            throw new NullPointerException();
        }
    }

    public static synchronized void deleteData(Message message) throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException, org.xml.sax.SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Node messageToUpdate = getNodeById(document, message.getId());

        if (messageToUpdate != null) {

            NodeList childNodes = messageToUpdate.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {

                Node node = childNodes.item(i);

                if (USER.equals(node.getNodeName())) {
                    node.setTextContent("");
                }

                if (MESSAGETEXT.equals(node.getNodeName())) {
                    node.setTextContent("");
                }

            }

            Transformer transformer = getTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult((new File(STORAGE_LOCATION)));
            transformer.transform(source, result);
        } else {
            throw new NullPointerException();
        }
    }

    public static synchronized boolean doesStorageExist() {
        File file = new File(STORAGE_LOCATION);
        return file.exists();
    }

    public static synchronized List<Message> getMessages() throws SAXException, IOException, ParserConfigurationException, org.xml.sax.SAXException {
        List<Message> messages = new ArrayList<>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        NodeList messageList = root.getElementsByTagName(MESSAGE);
        for (int i=0; i < messageList.getLength(); i++) {
            Element messageElement = (Element) messageList.item(i);
            String id = messageElement.getAttribute(ID);
            String user = messageElement.getElementsByTagName(USER).item(0).getTextContent();
            String messageText = messageElement.getElementsByTagName(MESSAGETEXT).item(0).getTextContent();
            if(user.equals("") && messageText.equals(""))
                continue;
            messages.add(new Message (id, user, messageText));
        }
        return messages;
    }

    public static synchronized int getStorageSize() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        return root.getElementsByTagName(MESSAGE).getLength();
    }

    private static Node getNodeById(Document doc, String id) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//" + "message" + "[@id='" + id + "']");
        return (Node) expr.evaluate(doc, XPathConstants.NODE);
    }

    private static Transformer getTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }
}
