package me.palr.palr_android.models;

/**
 * Created by maazali on 2016-10-15.
 */
public class User {
    String id;
    String name;
    String email;
    String gender;
    String country;
    String password;
    Integer age;
    String ethnicity;
    String[] hobbies;
    String imageUrl;
    boolean inMatchProcess;
    boolean isTemporarilyMatched;

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String location) {
        this.country= location;
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

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String[] getHobbies() {
        return hobbies;
    }

    public void setHobbies(String[] hobbies) {
        this.hobbies = hobbies;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isInMatchProcess() {
        return inMatchProcess;
    }

    public void setInMatchProcess(boolean inMatchProcess) {
        this.inMatchProcess = inMatchProcess;
    }

    public boolean isTemporarilyMatched() {
        return isTemporarilyMatched;
    }

    public void setTemporarilyMatched(boolean temporarilyMatched) {
        isTemporarilyMatched = temporarilyMatched;
    }
}
