package io.ideaction.stori.db;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Vocabulary extends RealmObject {

    @SerializedName("en")
    @Expose
    private RealmList<Integer> en = new RealmList<>();
    @SerializedName("es")
    @Expose
    private RealmList<Integer> es = new RealmList<>();
    @SerializedName("fr")
    @Expose
    private RealmList<Integer> fr = new RealmList<>();
    @SerializedName("de")
    @Expose
    private RealmList<Integer> de = new RealmList<>();
    @SerializedName("it")
    @Expose
    private RealmList<Integer> it = new RealmList<>();
    @SerializedName("ru")
    @Expose
    private RealmList<Integer> ru = new RealmList<>();

    public RealmList<Integer> getEn() {
        return en;
    }

    public void setEn(RealmList<Integer> en) {
        this.en = en;
    }

    public RealmList<Integer> getEs() {
        return es;
    }

    public void setEs(RealmList<Integer> es) {
        this.es = es;
    }

    public RealmList<Integer> getFr() {
        return fr;
    }

    public void setFr(RealmList<Integer> fr) {
        this.fr = fr;
    }

    public RealmList<Integer> getDe() {
        return de;
    }

    public void setDe(RealmList<Integer> de) {
        this.de = de;
    }

    public RealmList<Integer> getIt() {
        return it;
    }

    public void setIt(RealmList<Integer> it) {
        this.it = it;
    }

    public RealmList<Integer> getRu() {
        return ru;
    }

    public void setRu(RealmList<Integer> ru) {
        this.ru = ru;
    }
}