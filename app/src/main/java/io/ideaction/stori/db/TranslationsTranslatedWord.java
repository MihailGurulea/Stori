
package io.ideaction.stori.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class TranslationsTranslatedWord extends RealmObject {

    @SerializedName("word")
    @Expose
    private Word word;
    @SerializedName("pronunciation")
    @Expose
    private Pronunciation pronunciation;
    @SerializedName("definition")
    @Expose
    private Definition definition;
    @SerializedName("type")
    @Expose
    private Type type;

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public Pronunciation getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(Pronunciation pronunciation) {
        this.pronunciation = pronunciation;
    }

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
