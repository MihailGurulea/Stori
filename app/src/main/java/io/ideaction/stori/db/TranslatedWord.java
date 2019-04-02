
package io.ideaction.stori.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class TranslatedWord extends RealmObject {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("word")
    @Expose
    private String word;
    @SerializedName("pronunciation")
    @Expose
    private String pronunciation;
    @SerializedName("definition")
    @Expose
    private String definition;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("translations")
    @Expose
    private TranslationsTranslatedWord translations;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TranslationsTranslatedWord getTranslations() {
        return translations;
    }

    public void setTranslations(TranslationsTranslatedWord translations) {
        this.translations = translations;
    }

}
