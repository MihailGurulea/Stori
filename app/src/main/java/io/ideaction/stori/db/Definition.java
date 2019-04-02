
package io.ideaction.stori.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.utils.Languages;
import io.realm.RealmObject;

public class Definition extends RealmObject {

    @SerializedName("en")
    @Expose
    private String en;
    @SerializedName("es")
    @Expose
    private String es;
    @SerializedName("fr")
    @Expose
    private String fr;
    @SerializedName("de")
    @Expose
    private String de;
    @SerializedName("it")
    @Expose
    private String it;
    @SerializedName("ru")
    @Expose
    private String ru;

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getEs() {
        return es;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public String getFr() {
        return fr;
    }

    public void setFr(String fr) {
        this.fr = fr;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getIt() {
        return it;
    }

    public void setIt(String it) {
        this.it = it;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getDefinitionInNativeLanguages() {
        switch (Languages.values()[StoriApplication.getInstance().getNativeLang()]) {
            case FRENCH:
                return getFr();
            case GERMAN:
                return getDe();
            case ITALIAN:
                return getIt();
            case SPANISH:
                return getEs();
            case RUSSIAN:
                return getRu();
            default:
                return getEn();
        }
    }

    public String getDefinitionInLanguagesToLearn() {
        switch (Languages.values()[StoriApplication.getInstance().getLangToLearn()]) {
            case FRENCH:
                return getFr();
            case GERMAN:
                return getDe();
            case ITALIAN:
                return getIt();
            case SPANISH:
                return getEs();
            case RUSSIAN:
                return getRu();
            default:
                return getEn();
        }
    }
}
