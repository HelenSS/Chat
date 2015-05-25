package util;

import model.Message;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class MessageUtil {
    public static final String TOKEN = "token";
    public static final String MESSAGES = "messages";
    private static final String TE = "te";
    private static final String EN = "en";
    private static final String ID = "id";
    private static final String USER = "user";
    private static final String MESSAGETEXT = "messageText";

    private MessageUtil() {
    }

    public static String getToken(int index) {
        Integer number = index*8 + 11;
        return TE + number + EN;
    }

    public static int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }

    public static JSONObject stringToJson(String data) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(data.trim());
    }

    public static Message jsonToMessage(JSONObject json) {
        Object id = json.get(ID);
        Object user = json.get(USER);
        Object messageText = json.get(MESSAGETEXT);

        if(id != null && user != null && messageText != null) {
            return new Message((String) id, (String) user, (String) messageText);
        }
        return null;
    }
}
