package model;

public class Message {
    private String id;
    private String user;
    private String messageText;
    private transient String operation;

    public Message (String id, String user, String messageText) {
        this.id = id;
        this.user = user;
        this.messageText = messageText;
        //this.operation = "post";
    }

    public String getId() {
        return  id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText (String messageText) {
        this.messageText = messageText;
    }

    /*public void setOperation (String operation) {
        this.operation = operation;
    }*/

    public String toString() {
        return "{\"id\":\"" + this.id + "\",\"user\":\"" + this.user + "\",\"messageText\":\"" + this.messageText + "\"}";
    }
}
