package me.palr.palr_android.models;

/**
 * Created by maazali on 2016-10-15.
 */
public class LoginPayload {
    String email;
    String password;

    public LoginPayload(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
