package io.ideaction.stori.fragments.start_stori;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.activities.StartStoriActivity;
import io.ideaction.stori.db.Stori;
import io.ideaction.stori.db.TranslatedWord;
import io.ideaction.stori.db.Vocabulary;
import io.ideaction.stori.utils.CustomDialog;
import io.ideaction.stori.utils.Languages;
import io.ideaction.stori.utils.Utils;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadStoriFragment extends Fragment implements CustomDialog.CustomDialogListener {

    private static final String TAG = "ReadStoriFragment";
    private static final String STORI_ID = "io.ideaction.stori.fragments.start_stori.storiId";

    @BindView(R.id.tv_number_of_the_current_chapter)
    TextView mTVNumberOfTheCurrentChapter;
    @BindView(R.id.pb_chapter_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_chapters_text)
    TextView mTVChaptersText;
    @BindView(R.id.btn_next)
    Button mBTNNext;
    @BindView(R.id.fl_read_words)
    FlowLayout mFlowLayout;

    private StartStoriActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;
    private Realm mRealm;

    private ArrayList<String> mListOfWords;
    private String[] mListOfChapters;
    private String mStoriText;
    private ArrayList<TextView> mTextViews;

    private int mChapterCounter;
    private int mTotalNumberOfChapters;
    private int mStoriId;
    private Stori mStori;
    private TextToSpeech mTTS;
    private CustomDialog mCustomDialog;
    private Call<Void> mCall;
    private Call<Void> mCallReadStori;

    public static ReadStoriFragment newInstance(int storiId) {
        ReadStoriFragment readStoriFragment = new ReadStoriFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(STORI_ID, storiId);
        readStoriFragment.setArguments(bundle);

        return readStoriFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStoriId = getArguments().getInt(STORI_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_read_stori, container, false);

        initFragment();
        initViews();

        return mView;
    }

    private void initFragment() {
        mActivity = (StartStoriActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, mView);

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("stori.realm").build();
        mRealm = Realm.getInstance(configuration);

        mStori = mRealm.where(Stori.class).equalTo("id", mStoriId).findFirst();

        mStoriText = mStori.getTranslations().getWords().getWordsInLanguagesToLearn();
        mStoriText = mStoriText.replaceAll("\\n", "");
        mListOfChapters = mStoriText.split("\\$");
        setTotalNumberOfChapters(mListOfChapters.length);
        mProgressBar.setMax(getTotalNumberOfChapters());
        setChapterCounter(getChapterCounter() + 1);
    }

    private void initListOfWords() {
        mListOfWords = new ArrayList<>();
        mTextViews = new ArrayList<>();
        if (mFlowLayout.getChildCount() > 0) {
            mFlowLayout.removeAllViews();
        }

        for (String string : mListOfChapters[getChapterCounter() - 1].replaceAll("[^a-zA-Zа-яА-Я ]", "").split(" +")) {
            mListOfWords.add(string);
        }

        for (String s : mListOfWords) {
            TextView textView = new TextView(mActivity);
            ViewGroup.MarginLayoutParams textViewParams = new ViewGroup.MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            textViewParams.setMargins(
                    (int) Utils.convertDpToPixel(2, mActivity),
                    (int) Utils.convertDpToPixel(1, mActivity),
                    (int) Utils.convertDpToPixel(2, mActivity),
                    (int) Utils.convertDpToPixel(3, mActivity));
            textView.setLayoutParams(textViewParams);
            textView.setBackgroundResource(R.drawable.read_stori_words_white);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setElevation(Utils.convertDpToPixel(2, mActivity));
            textView.setPadding(
                    (int) Utils.convertDpToPixel(18, mActivity),
                    (int) Utils.convertDpToPixel(9, mActivity),
                    (int) Utils.convertDpToPixel(18, mActivity),
                    (int) Utils.convertDpToPixel(9, mActivity));
            textView.setTextSize(13);
            textView.setTypeface(StoriApplication.getInstance().getCeraProRegular());
            textView.setText(s);
            textView.requestLayout();
            mTextViews.add(textView);
            textView.setOnClickListener(v -> {
                TranslatedWord translatedWord;
                switch (StoriApplication.getInstance().getLangToLearn()) {
                    case 1:
                        translatedWord = mRealm.where(TranslatedWord.class).equalTo("translations.word.en", String.valueOf(textView.getText()), Case.INSENSITIVE).findFirst();
                        break;
                    case 2:
                        translatedWord = mRealm.where(TranslatedWord.class).equalTo("translations.word.es", String.valueOf(textView.getText()), Case.INSENSITIVE).findFirst();
                        break;
                    case 3:
                        translatedWord = mRealm.where(TranslatedWord.class).equalTo("translations.word.fr", String.valueOf(textView.getText()), Case.INSENSITIVE).findFirst();
                        break;
                    case 4:
                        translatedWord = mRealm.where(TranslatedWord.class).equalTo("translations.word.de", String.valueOf(textView.getText()), Case.INSENSITIVE).findFirst();
                        break;
                    case 5:
                        translatedWord = mRealm.where(TranslatedWord.class).equalTo("translations.word.it", String.valueOf(textView.getText()), Case.INSENSITIVE).findFirst();
                        break;
                    default:
                        translatedWord = mRealm.where(TranslatedWord.class).equalTo("translations.word.ru", String.valueOf(textView.getText()), Case.INSENSITIVE).findFirst();
                        break;
                }
                if (translatedWord != null) {
                    textView.setBackgroundResource(R.drawable.read_stori_words_yellow);
                    textView.setTextColor(getResources().getColor(android.R.color.white));
                    mCustomDialog = new CustomDialog(mActivity, translatedWord, ReadStoriFragment.this);
                    mCustomDialog.setOnDismissListener(dialog -> {
                        textView.setBackgroundResource(R.drawable.read_stori_words_white);
                        textView.setTextColor(getResources().getColor(android.R.color.black));
                    });
                    mCustomDialog.show();
                }
            });

            mFlowLayout.addView(textView);
        }
    }

    private void initViews() {
        mTVNumberOfTheCurrentChapter.setText(getChapterCounter() + "/" + mTotalNumberOfChapters);
        mTVChaptersText.setText(mListOfChapters[getChapterCounter() - 1]);
        initListOfWords();
    }

    public int getChapterCounter() {
        return mChapterCounter;
    }

    public void setChapterCounter(int chapterCounter) {
        mChapterCounter = chapterCounter;
    }

    public void increaseChapterCounter() {
        ++mChapterCounter;
        mProgressBar.setProgress(mChapterCounter);
    }

    public void decreaseChapterCounter() {
        --mChapterCounter;
        mProgressBar.setProgress(mChapterCounter);
    }

    public int getTotalNumberOfChapters() {
        return mTotalNumberOfChapters;
    }

    public void setTotalNumberOfChapters(int totalNumberOfChapters) {
        mTotalNumberOfChapters = totalNumberOfChapters;
    }

    public void changeWordsByChapter() {
        initViews();
    }

    public void changeNextBTN() {
        mBTNNext.setBackgroundResource(R.drawable.btn_pause_white_with_grey_border);
        mBTNNext.setText(R.string.next);
        mBTNNext.setTextColor(Color.parseColor("#7C7C7C"));
    }

    @OnClick(R.id.btn_next)
    void onClickNext() {
        if (mChapterCounter < mTotalNumberOfChapters - 1) {
            increaseChapterCounter();
            changeWordsByChapter();
            mActivity.changeBackButtonImage(mActivity.BACK);
            initViews();
        } else if (mChapterCounter == mTotalNumberOfChapters - 1) {
            increaseChapterCounter();
            changeWordsByChapter();
            mBTNNext.setBackgroundResource(R.drawable.btn_read_orange);
            mBTNNext.setText(R.string.finish);
            mBTNNext.setTextColor(Color.parseColor("#FFFFFF"));
            initViews();
        } else if (mChapterCounter == mTotalNumberOfChapters) {
            mCallReadStori = StoriApplication.apiInterface().readStori(
                    "Bearer " + StoriApplication.getInstance().getToken(),
                    mStoriId
            );

            mCallReadStori.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        mRealm.executeTransaction(r -> mStori.setRead(true));
                        mActivity.onBackPressed();
                        Toasty.success(mActivity, "Response is successful", Toasty.LENGTH_LONG).show();
                    } else {
                        Toasty.error(mActivity, "Response isn't successful", Toasty.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, t.getMessage(), t);
                    Toasty.error(mActivity, "Request error", Toasty.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
        }

        if (mView != null) {
            mView = null;
        }

        if (mCall != null) {

            if (mCall.isExecuted()) {
                mCall.cancel();
            }

            if (mCall.isCanceled()) {
                mCall = null;
            }
        }

        if (mCallReadStori != null) {

            if (mCallReadStori.isExecuted()) {
                mCallReadStori.cancel();
            }

            if (mCallReadStori.isCanceled()) {
                mCallReadStori = null;
            }
        }
    }

    @Override
    public void speakWord(String word, int id) {
        if (mTTS != null) {
            mTTS.shutdown();
        }

        mTTS = new TextToSpeech(mActivity, status -> {

            if (status == TextToSpeech.SUCCESS) {

                int result = mTTS.setLanguage(new Locale(
                        Languages.values()[StoriApplication.getInstance().getLangToLearn()].getCodeLowerCase(),
                        Languages.values()[StoriApplication.getInstance().getLangToLearn()].getCodeUpperCase())
                );

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                    Toasty.error(mActivity, "This language is not supported by your phone's Operating System", Toasty.LENGTH_SHORT).show();
                    String url = "https://support.google.com/accessibility/android/answer/6006983?hl=en&ref_topic=9078845";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    mActivity.startActivity(i);

                }

                mTTS.setPitch(0.8f);
                mTTS.setSpeechRate(0.6f);
                mTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);

            } else {
                Log.e("TTS", "Initialization failed");
            }
        });
    }

    @Override
    public void dismiss() {
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
    }

    @Override
    public void addToVocabulary(int id) {
        boolean exist;
        Vocabulary v = mRealm.where(Vocabulary.class).findFirst();

        if (v != null) {
            switch (StoriApplication.getInstance().getLangToLearn()) {
                case 1:
                    exist = v.getEn().contains(id);
                    break;
                case 2:
                    exist = v.getEs().contains(id);
                    break;
                case 3:
                    exist = v.getFr().contains(id);
                    break;
                case 4:
                    exist = v.getDe().contains(id);
                    break;
                case 5:
                    exist = v.getIt().contains(id);
                    break;
                default:
                    exist = v.getRu().contains(id);
                    break;
            }

            if (exist) {
                dismiss();
                return;
            }
        }

        mRealm.executeTransaction(
                r -> {
                    Vocabulary vocabulary = r.where(Vocabulary.class).findFirst();

                    if (vocabulary != null) {
                        switch (StoriApplication.getInstance().getLangToLearn()) {
                            case 1:
                                vocabulary.getEn().add(id);
                                break;
                            case 2:
                                vocabulary.getEs().add(id);
                                break;
                            case 3:
                                vocabulary.getFr().add(id);
                                break;
                            case 4:
                                vocabulary.getDe().add(id);
                                break;
                            case 5:
                                vocabulary.getIt().add(id);
                                break;
                            default:
                                vocabulary.getRu().add(id);
                                break;
                        }
                    }
                }
        );

        mCall = StoriApplication.apiInterface().saveWord("Bearer " + StoriApplication.getInstance().getToken(), id);

        mCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    dismiss();
                    Toasty.success(mActivity, "Response is successful", Toasty.LENGTH_LONG).show();
                } else {
                    Toasty.error(mActivity, "Response isn't successful", Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                Toasty.error(mActivity, "Request error", Toasty.LENGTH_LONG).show();
            }
        });
    }
}
