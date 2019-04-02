package io.ideaction.stori.fragments.main_menu;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.activities.MainMenuActivity;
import io.ideaction.stori.adapters.VocabularyAdapter;
import io.ideaction.stori.db.TranslatedWord;
import io.ideaction.stori.db.Vocabulary;
import io.ideaction.stori.models.UserModel;
import io.ideaction.stori.utils.CustomChangeLanguageDialog;
import io.ideaction.stori.utils.Languages;
import io.ideaction.stori.utils.SwipeToDeleteCallback;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VocabularyFragment extends Fragment implements VocabularyAdapter.VocabularyAdapterListener, CustomChangeLanguageDialog.DialogLanguageListener, SwipeToDeleteCallback.SwipeToDeleteCallbackListener {

    private static final String TAG = "VocabularyFragment";

    @BindView(R.id.lv_list_with_headers)
    RecyclerView mListWithHeaders;
    @BindView(R.id.rl_empty)
    RelativeLayout mRLEmptyVoc;
    @BindView(R.id.ll_search)
    LinearLayout mLLSearch;
    @BindView(R.id.tv_let_s_train)
    TextView mTVLetSTrain;
    @BindView(R.id.iv_language)
    ImageView mIVLanguage;

    private CustomChangeLanguageDialog mCustomChangeLanguageDialog;

    private MainMenuActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;
    private Realm mRealm;
    private TextToSpeech mTTS;
    private Call<UserModel> mCall;
    private Call<Void> mCallRemoveWord;
    private ArrayList<TranslatedWord> mTranslatedWords;
    private VocabularyAdapter mVocabularyAdapter;


    public static VocabularyFragment newInstance() {
        return new VocabularyFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vocabulary, container, false);

        initFragment();
        initViews();

        return mView;
    }

    private void initFragment() {
        mActivity = (MainMenuActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, mView);

        mIVLanguage.setImageResource(Languages.values()[StoriApplication.getInstance().getLangToLearn()].getRes());

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("stori.realm").build();
        mRealm = Realm.getInstance(configuration);
    }

    private void initViews() {
        Vocabulary vocabulary = mRealm.where(Vocabulary.class).findFirst();
        RealmList<Integer> vocabularies;

        if (vocabulary != null) {

            switch (StoriApplication.getInstance().getLangToLearn()) {
                case 1:
                    vocabularies = vocabulary.getEn();
                    break;
                case 2:
                    vocabularies = vocabulary.getEs();
                    break;
                case 3:
                    vocabularies = vocabulary.getFr();
                    break;
                case 4:
                    vocabularies = vocabulary.getDe();
                    break;
                case 5:
                    vocabularies = vocabulary.getIt();
                    break;
                default:
                    vocabularies = vocabulary.getRu();
                    break;
            }

            if (vocabularies.size() == 0) {
                if (mVocabularyAdapter != null) {
                    mVocabularyAdapter.removeAll();
                }
                mListWithHeaders.setVisibility(View.GONE);
                mRLEmptyVoc.setVisibility(View.VISIBLE);
                mLLSearch.setVisibility(View.GONE);
                mTVLetSTrain.setVisibility(View.GONE);
            } else {
                mListWithHeaders.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
                mTranslatedWords = new ArrayList<>();

                for (Integer integer : vocabularies) {
                    TranslatedWord translatedWord = mRealm.where(TranslatedWord.class).equalTo("id", integer).findFirst();
                    if (translatedWord != null) {
                        mTranslatedWords.add(translatedWord);
                    }
                }

                Collections.sort(mTranslatedWords, (o1, o2) -> o1.getTranslations().getWord().getWordInLanguagesToLearn()
                        .compareToIgnoreCase(o2.getTranslations().getWord().getWordInLanguagesToLearn()));

                mVocabularyAdapter = new VocabularyAdapter(mTranslatedWords, VocabularyFragment.this);
                mListWithHeaders.setAdapter(mVocabularyAdapter);

                ItemTouchHelper itemTouchHelper = new
                        ItemTouchHelper(new SwipeToDeleteCallback(mActivity, mVocabularyAdapter, VocabularyFragment.this));
                itemTouchHelper.attachToRecyclerView(mListWithHeaders);

                mRLEmptyVoc.setVisibility(View.GONE);
                mListWithHeaders.setVisibility(View.VISIBLE);
                mLLSearch.setVisibility(View.VISIBLE);
                mTVLetSTrain.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.iv_language)
    void OnClickLanguage() {
        mCustomChangeLanguageDialog = new CustomChangeLanguageDialog(mActivity, this);
        mCustomChangeLanguageDialog.show();
        mIVLanguage.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_let_s_train)
    void OnClickLetsTrain() {
        if (mTranslatedWords.size() > 9) {
            mActivity.onClickLetsTrain();
        } else {
            Toasty.info(mActivity, "You need to have at least 10 words in your vocabulary.", Toasty.LENGTH_LONG).show();
        }
    }

    public void setVisibleIVLanguage() {
        mIVLanguage.setVisibility(View.VISIBLE);
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

        if (mActivity != null) {
            mActivity = null;
        }

        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        if (mCall != null) {

            if (mCall.isExecuted()) {
                mCall.cancel();
            }

            if (mCall.isCanceled()) {
                mCall = null;
            }
        }
    }

    @Override
    public void onClickSound(int position) {
        clickToSpeak(position);
    }

    private void clickToSpeak(int position) {
        if (mTTS != null) {
            mTTS.shutdown();
        }

        mTTS = new TextToSpeech(mActivity, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = mTTS.setLanguage(new Locale(
                        Languages.values()[StoriApplication.getInstance().getLangToLearn()].getCodeLowerCase(),
                        Languages.values()[StoriApplication.getInstance().getLangToLearn()].getCodeUpperCase())
                );

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                } else {
                    speak(position);
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });
    }

    private void speak(int position) {
        String text = String.valueOf(mTranslatedWords.get(position).getTranslations().getWord().getWordInLanguagesToLearn());
        mTTS.setPitch(0.8f);
        mTTS.setSpeechRate(0.6f);
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);

    }

    @Override
    public void onClickClose() {
        mCustomChangeLanguageDialog.dismiss();
        setVisibleIVLanguage();
    }

    @Override
    public void onClickItem(int position) {
        mActivity.showProgressBar();
        mCall = StoriApplication.apiInterface().updateLangToLearn(
                "Bearer " + StoriApplication.getInstance().getToken(),
                position + 1
        );

        mCall.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                mActivity.hideProgressBar();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        StoriApplication.getInstance().setUserCredentials(response.body());
                    } else {
                        Toasty.error(mActivity, "Response is null", Toasty.LENGTH_LONG).show();
                    }
                } else {
                    Toasty.error(mActivity, "Response isn't successful", Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                mActivity.hideProgressBar();
                Log.e(TAG, t.getMessage(), t);
                Toasty.error(mActivity, "Request error", Toasty.LENGTH_LONG).show();
            }
        });
        StoriApplication.getInstance().setLangToLearn(position + 1);
        mCustomChangeLanguageDialog.dismiss();
        updateFlag();
        setVisibleIVLanguage();
        initViews();
    }

    public void updateFlag() {
        mIVLanguage.setImageResource(Languages.values()[StoriApplication.getInstance().getLangToLearn()].getRes());
    }

    @Override
    public void onWordRemove(int id) {
        mCallRemoveWord = StoriApplication.apiInterface().removeWord(
                "Bearer " + StoriApplication.getInstance().getToken(),
                id
        );

        mCallRemoveWord.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
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