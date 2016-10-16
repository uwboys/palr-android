package me.palr.palr_android.models;

/**
 * Created by maazali on 2016-10-16.
 */
public class MatchPayload {
    String type;

    public MatchPayload(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
