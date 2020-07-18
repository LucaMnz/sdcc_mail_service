package service;

public interface MailServiceInterface {

    public boolean updateObject(String newObject, String tag);

    public boolean updateMessage(String newMessage, String tag);

    public boolean updateAttachment(String newAttachment, String tag);

    boolean SendMail(String tag, int userID);
}
