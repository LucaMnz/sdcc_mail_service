package model;

public class Mail {

    private int id;
    private String tag;
    private String object;
    private String message;
    private String attachment;

    public Mail(String tag, String object, String message, String attachment) {
        this.tag = tag;
        this.object = object;
        this.message = message;
        this.attachment = attachment;
    }

    public Mail(int id, String tag, String object, String message, String attachment) {
        this.id = id;
        this.tag = tag;
        this.object = object;
        this.message = message;
        this.attachment = attachment;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public int getId() {
        return id;
    }
}
