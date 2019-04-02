package io.ideaction.stori.models;

public class WordModel {

   private String word;
   private String typeOfWord;
   private String phonetic;
    private String description;
    private String streamLink;


    public WordModel(String word, String typeOfWord, String phonetic, String description, String streamLink) {
        this.word = word;
        this.typeOfWord = typeOfWord;
        this.phonetic = phonetic;
        this.description = description;
        this.streamLink = streamLink;
    }

    public String getWord() {

        return word;
    }

    public String getTypeOfWord() {
        return typeOfWord;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public String getDescription() {
        return description;
    }

    public String getStreamLink() {
        return streamLink;
    }
}
