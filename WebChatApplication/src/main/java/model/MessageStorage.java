package model;

import java.util.*;

public final class MessageStorage {
    private static final List<Message> INSTANSE = Collections.synchronizedList(new ArrayList<Message>());

    private MessageStorage() {
    }

    public static List<Message> getStorage() {
        return INSTANSE;
    }

    public static void addMessage(Message message) {
        INSTANSE.add(message);
    }

    public static void addAll(Message [] messages) {
        INSTANSE.addAll(Arrays.asList(messages));
    }

    public static void addAll(List<Message> messages) {
        INSTANSE.addAll(messages);
    }

    public static void putMessage (Message message) {
        for (int i=0; i<INSTANSE.size(); i++) {
            if (INSTANSE.get(i).getId().equals(message.getId())) {
                INSTANSE.get(i).setMessageText(message.getMessageText());
                return;
            }
        }
    }

    public static void deleteMessage (Message message) {
        for (int i=0; i<INSTANSE.size(); i++) {
            if (INSTANSE.get(i).getId().equals(message.getId())) {
                INSTANSE.get(i).setMessageText("");
                INSTANSE.get(i).setUser("");
                return;
            }
        }
    }

    public static int getSize() {
        return INSTANSE.size();
    }

    public static List<Message> getSubMessagesByIndex(int index) {
        return INSTANSE.subList(index, INSTANSE.size());
    }

    /*public static Message getMessageById(String id) {
        for (Message message : INSTANSE) {
            if (message.getId().equals(id)); {
                return message;
            }
        }
        return null;
    }*/

    public static void cleanStorage() {
        INSTANSE.clear();
    }
}
