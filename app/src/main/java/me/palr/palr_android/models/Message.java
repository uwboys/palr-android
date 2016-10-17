package me.palr.palr_android.models;

/**
 * Created by maazali on 2016-10-16.
 */
public class Message {
    String id;
    String content;
    User createdBy;
    String conversationDataId;

    public Message(String content, String conversationDataId) {
        this.content = content;
        this.conversationDataId = conversationDataId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getConversationDataId() {
        return conversationDataId;
    }

    public void setConversationDataId(String conversationDataId) {
        this.conversationDataId = conversationDataId;
    }
}
