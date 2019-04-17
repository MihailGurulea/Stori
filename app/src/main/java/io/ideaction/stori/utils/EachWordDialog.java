package io.ideaction.stori.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Objects;

import io.ideaction.stori.R;
import io.ideaction.stori.db.TranslatedWord;

public class EachWordDialog extends Dialog {

    private TranslatedWord mTranslatedWord;
    private Button mBTNPlay;
    private Button mBTNAddToVocabulary;
    private TextView mSelectedWord;
    private TextView mPhonetics;
    private TextView mSelectedWordInNativeLang;
    private RelativeLayout mRelativeLayoutDialog;
    private CustomDialogListener mCustomDialogListener;

    public EachWordDialog(Activity activity, TranslatedWord translatedWord, CustomDialogListener customDialogListener) {
        super(activity);
        mTranslatedWord = translatedWord;
        mCustomDialogListener = customDialogListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_read_stori);

        mRelativeLayoutDialog = findViewById(R.id.rl_dialog);
        mBTNPlay = findViewById(R.id.btn_play);
        mBTNAddToVocabulary = findViewById(R.id.btn_add_to_voc);
        mSelectedWord = findViewById(R.id.tv_word_you_selected);
        mPhonetics = findViewById(R.id.tv_phonetics);
        mSelectedWordInNativeLang = findViewById(R.id.tv_word_you_selected_native);
        mSelectedWord.setText(mTranslatedWord.getTranslations().getWord().getWordInLanguagesToLearn());
        mPhonetics.setText("[" + mTranslatedWord.getTranslations().getPronunciation().getPronunciationInLanguagesToLearn() + "]");
        mSelectedWordInNativeLang.setText(mTranslatedWord.getTranslations().getWord().getWordInNativeLanguages());

        mRelativeLayoutDialog.setOnClickListener(v -> mCustomDialogListener.dismiss());

        mBTNPlay.setOnClickListener(v -> mCustomDialogListener.speakWord(mTranslatedWord.getTranslations().getWord().getWordInLanguagesToLearn(), mTranslatedWord.getId()));

        mBTNAddToVocabulary.setOnClickListener(v -> mCustomDialogListener.addToVocabulary(mTranslatedWord.getId()));
    }

    public interface CustomDialogListener {
        void speakWord(String word, int id);

        void dismiss();

        void addToVocabulary(int id);
    }
}
