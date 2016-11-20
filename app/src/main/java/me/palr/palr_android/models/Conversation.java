package me.palr.palr_android.models;

/**
 * Created by maazali on 2016-10-15.
 */
public class Conversation {
    String id;
    User pal;
    User user;
    String conversationDataId;
    String lastMessageDate;
    String createdAt;
    boolean isPermanent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getPal() {
        return pal;
    }

    public void setPal(User pal) {
        this.pal = pal;
    }

    public String getConversationDataId() {
        return conversationDataId;
    }

    public void setConversationDataId(String conversationDataId) {
        this.conversationDataId = conversationDataId;
    }

    public boolean getIsPermanent() {
        return isPermanent;
    }

    public void setPermanent(boolean isPermanent) {
        this.isPermanent = isPermanent;
    }

    public String getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(String lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
