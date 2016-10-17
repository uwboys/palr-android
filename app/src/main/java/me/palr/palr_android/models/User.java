package me.palr.palr_android.models;

/**
 * Created by maazali on 2016-10-15.
 */
public class User {
    String id;
    String name;
    String email;
    String gender;
    String location;
    String password;
    Integer age;
    boolean inMatchProcess;
    boolean isMatched;


    public User() {}

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password, String gender) {
        this(name, email, password);
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public boolean isInMatchProcess() {
        return inMatchProcess;
    }

    public void setInMatchProcess(boolean inMatchProcess) {
        this.inMatchProcess = inMatchProcess;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }
}
