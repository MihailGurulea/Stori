package io.ideaction.stori.utils;

import io.ideaction.stori.R;

public enum Languages {
    STANDARD(R.drawable.ic_english_flag, "en", "US"),
    ENGLISH(R.drawable.ic_english_flag, "en", "US"),
    SPANISH(R.drawable.ic_spanish_flag, "es", "ES"),
    FRENCH(R.drawable.ic_french_flag, "fr", "FR"),
    GERMAN(R.drawable.ic_german_flag, "de", "DE"),
    ITALIAN(R.drawable.ic_italian_flag, "it", "IT"),
    RUSSIAN(R.drawable.ic_russian_flag, "ru", "RU");

    private int mRes;
    private String mCodeLowerCase;
    private String mCodeUpperCase;

    Languages(int res, String codeLowerCase, String codeUpperCase) {
        mRes = res;
        mCodeLowerCase = codeLowerCase;
        mCodeUpperCase = codeUpperCase;
    }

    public int getRes() {
        return mRes;
    }

    public String getCodeLowerCase() {
        return mCodeLowerCase;
    }

    public String getCodeUpperCase() {
        return mCodeUpperCase;
    }
}
