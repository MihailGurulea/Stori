package io.ideaction.stori.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class UserModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    private String password;
    @SerializedName("native_lang")
    @Expose
    private int native_lang;
    @SerializedName("lang_to_learn")
    @Expose
    private int lang_to_learn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public int getNative_lang() {
        return native_lang;
    }

    public void setNative_lang(int native_lang) {
        this.native_lang = native_lang;
    }

    public int getLang_to_learn() {
        return lang_to_learn;
    }

    public void setLang_to_learn(int lang_to_learn) {
        this.lang_to_learn = lang_to_learn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NotNull
    @Override
    public String toString() {
        return "Id: " + id + "\n" +
                "Email: " + email + "\n" +
                "Name: " + name + "\n" +
                "Native lang: " + native_lang + "\n" +
                "Lang to learn: " + lang_to_learn;
    }
}
