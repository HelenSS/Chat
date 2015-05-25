package controller;

import model.Message;
import model.MessageStorage;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;
import storage.XMLHistoryUtil;
import util.MessageUtil;
import util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

@WebServlet("/chat")
public class MessageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger (MessageServlet.class.getName());

    @Override
    public void init() throws ServletException{
        try {
            try {
                loadHistory();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
        catch (IOException | ParserConfigurationException | TransformerException e) {
            //logger.error(e);
        }
    }

    @Override
    protected  synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doGet");
        String token = request.getParameter(MessageUtil.TOKEN);
        logger.info ("Token " + token);

        if (token != null && !"".equals(token)) {
            //int index = MessageUtil.getIndex(token);
            int index = 0;
            logger.info("Index " + index);
            String messages = formResponse(index);
            response.setContentType(ServletUtil.APPLICATION_JSON);
            PrintWriter out = response.getWriter();
            out.print(messages);
            out.flush();
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, " 'token' parameter needed");
        }
    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info ("doPost");
        String data = ServletUtil.getMessageBody(request);
        logger.info(data);
        try {
            JSONObject json = MessageUtil.stringToJson(data);
            Message message = MessageUtil.jsonToMessage(json);
            MessageStorage.addMessage(message);
            XMLHistoryUtil.addData(message);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException | ParserConfigurationException | TransformerException e) {
            //logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SAXException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doPut");
        String data = ServletUtil.getMessageBody(request);
        logger.info(data);
        try {
            JSONObject json = MessageUtil.stringToJson(data);
            Message message = MessageUtil.jsonToMessage(json);
            XMLHistoryUtil.updateData(message);
            MessageStorage.putMessage(message);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
            //logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("doDelete");
        String data = ServletUtil.getMessageBody(request);
        logger.info(data);
        try {
            JSONObject json = MessageUtil.stringToJson(data);
            Message message = MessageUtil.jsonToMessage(json);
            XMLHistoryUtil.deleteData(message);
            MessageStorage.deleteMessage(message);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
            //logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @SuppressWarnings("uncheked")
    private String formResponse(int index) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MessageUtil.MESSAGES, MessageStorage.getSubMessagesByIndex(index));
        jsonObject.put(MessageUtil.TOKEN, MessageUtil.getToken(MessageStorage.getSize()));
        return jsonObject.toJSONString();
    }

    private void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException, org.xml.sax.SAXException {
       if (!XMLHistoryUtil.doesStorageExist()) {
            XMLHistoryUtil.createStorage();
        }
        MessageStorage.addAll(XMLHistoryUtil.getMessages());
        for (Message i : MessageStorage.getStorage())
            logger.info (i.toString());
    }
}