
package io.ideaction.stori.db;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Stori extends RealmObject {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("words")
    @Expose
    private String words;
    @SerializedName("translations")
    @Expose
    private Translations translations;
    @SerializedName("translatedWords")
    @Expose
    private RealmList<TranslatedWord> translatedWords = null;
    @SerializedName("category")
    @Expose
    private Category category;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("audios")
    @Expose
    private Audios audios;
    @SerializedName("isFavorite")
    @Expose
    private Boolean isFavorite;
    @SerializedName("isRead")
    @Expose
    private Boolean isRead;
    @SerializedName("favorites")
    @Expose
    private Integer favorites;
    @SerializedName("isPremium")
    @Expose
    private Boolean isPremium;
    @SerializedName("created_at")
    @Expose
    private CreatedAt createdAt;
    @SerializedName("created_at_timestamp")
    @Expose
    private Integer createdAtTimestamp;
    @SerializedName("created_at_human")
    @Expose
    private String createdAtHuman;

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

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public Translations getTranslations() {
        return translations;
    }

    public void setTranslations(Translations translations) {
        this.translations = translations;
    }

    public RealmList<TranslatedWord> getTranslatedWords() {
        return translatedWords;
    }

    public void setTranslatedWords(RealmList<TranslatedWord> translatedWords) {
        this.translatedWords = translatedWords;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Audios getAudios() {
        return audios;
    }

    public void setAudios(Audios audios) {
        this.audios = audios;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public Integer getFavorites() {
        return favorites;
    }

    public void setFavorites(Integer favorites) {
        this.favorites = favorites;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }

    public CreatedAt getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(CreatedAt createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedAtTimestamp() {
        return createdAtTimestamp;
    }

    public void setCreatedAtTimestamp(Integer createdAtTimestamp) {
        this.createdAtTimestamp = createdAtTimestamp;
    }

    public String getCreatedAtHuman() {
        return createdAtHuman;
    }

    public void setCreatedAtHuman(String createdAtHuman) {
        this.createdAtHuman = createdAtHuman;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
