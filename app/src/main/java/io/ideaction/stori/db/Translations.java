
package io.ideaction.stori.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Translations extends RealmObject {

    @SerializedName("name")
    @Expose
    private Name name;
    @SerializedName("words")
    @Expose
    private Words words;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Words getWords() {
        return words;
    }

    public void setWords(Words words) {
        this.words = words;
    }

}
