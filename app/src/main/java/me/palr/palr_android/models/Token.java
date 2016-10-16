package me.palr.palr_android.models;

/**
 * Created by maazali on 2016-10-15.
 */
public class Token {
    String accessToken;

    String userId;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
