package me.palr.palr_android.models;

/**
 * Created by maazali on 2016-10-15.
 */
public class Conversation {
    String id;
    User pal;
    User user;
    String conversationDataId;
    String permanent;
    String lastMessageDate;
    String createdAt;

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

    public String getPermanent() {
        return permanent;
    }

    public void setPermanent(String permanent) {
        this.permanent = permanent;
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
