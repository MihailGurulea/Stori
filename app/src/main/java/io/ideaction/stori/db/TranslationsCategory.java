
package io.ideaction.stori.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class TranslationsCategory extends RealmObject {

    @SerializedName("name")
    @Expose
    private NameTranslationCategory name;

    public NameTranslationCategory getName() {
        return name;
    }

    public void setName(NameTranslationCategory name) {
        this.name = name;
    }

}
