package io.ideaction.stori.fragments.probation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.activities.ProbationActivity;
import io.ideaction.stori.adapters.ExamAdapter;
import io.ideaction.stori.db.TranslatedWord;
import io.ideaction.stori.db.Vocabulary;
import io.ideaction.stori.db.Word;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

public class ExamFragment extends Fragment implements ExamAdapter.ExamAdapterListener {

    public static final int NOT_SELECTED = 0;
    public static final int SELECTED = 1;
    private static final String TAG = "ExamFragment";
    private static final int CORRECT = 2;
    private static final int WRONG = 3;

    private static final int[] mBtnDrawableResources = new int[]{
            R.drawable.btn_start_exam,
            R.drawable.btn_read_orange,
            R.drawable.btn_read_orange,
            R.drawable.btn_read_orange
    };

    private static final int[] mBtnTextResources = new int[]{
            R.string.check,
            R.string.check,
            R.string.correct_answer_check_btn_txt,
            R.string.wrong_answer_check_btn_txt,
    };

    private int[] mCorrectPosition = new int[10];

    private final static int MAX_QUESTIONS = 10;

    @BindView(R.id.tv_exam_word)
    TextView mTVExamWord;
    @BindView(R.id.pb_chapter_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.back_btn)
    ImageView mBackButton;
    @BindView(R.id.btn_check)
    Button mBTNCheck;
    @BindView(R.id.lv_exam_options_list)
    ListView mLVExamOptionsList;

    private ProbationActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;

    private ExamAdapter mExamAdapter;
    private int mProgressCounter;
    private String[] mExamContent;
    private int mCurrentPosition;
    private int mBtnNextCurrentStatus;
    private int mCorrectAnswers;
    private Realm mRealm;
    private ArrayList<TranslatedWord> mTranslatedWords;
    private ArrayList<ArrayList<String>> mFinallWords;
    private ArrayList<String> mCorrectWords;
    private ArrayList<String> mNativeWords;
    private int mQuestion;

    public static ExamFragment newInstance() {
        return new ExamFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_exam, container, false);

        initFragment();
        initViews();

        return mView;
    }

    private void initFragment() {
        mActivity = (ProbationActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, mView);

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("stori.realm").build();
        mRealm = Realm.getInstance(configuration);

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

            mTranslatedWords = new ArrayList<>();
            ArrayList<TranslatedWord> translatedWords = new ArrayList<>();
            ArrayList<Integer> positions = new ArrayList<>();

            for (Integer integer : vocabularies) {
                TranslatedWord translatedWord = mRealm.where(TranslatedWord.class).equalTo("id", integer).findFirst();
                if (translatedWord != null) {
                    translatedWords.add(translatedWord);
                }
            }

            while (mTranslatedWords.size() < 10) {
                int position = new Random().nextInt(translatedWords.size());
                if (!positions.contains(position)) {
                    mTranslatedWords.add(translatedWords.get(position));
                    positions.add(position);
                }
            }

            mFinallWords = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                mFinallWords.add(new ArrayList<>());
            }
            mCorrectWords = new ArrayList<>();
            mNativeWords = new ArrayList<>();

            for (int j = 0; j < mTranslatedWords.size(); j++) {
                TranslatedWord translatedWord = mTranslatedWords.get(j);
                String word = translatedWord.getTranslations().getWord().getWordInLanguagesToLearn();
                mCorrectWords.add(word);
                mFinallWords.get(j).add(word);

                mNativeWords.add(translatedWord.getTranslations().getWord().getWordInNativeLanguages());

                int randomPosition = new Random().nextInt(word.length());
                char c = word.charAt(randomPosition);

                if (c > 64 && c < 91) {
                    if (90 - c > 2) {
                        for (int i = 0; i < 3; i++) {
                            mFinallWords.get(j).add(word.substring(0, randomPosition) + ++c + word.substring(randomPosition + 1));
                        }
                    } else {
                        for (int i = 0; i < 3; i++) {
                            mFinallWords.get(j).add(word.substring(0, randomPosition) + --c + word.substring(randomPosition + 1));
                        }
                    }
                } else if (c > 96 && c < 123) {
                    if (121 - c > 2) {
                        for (int i = 0; i < 3; i++) {
                            mFinallWords.get(j).add(word.substring(0, randomPosition) + ++c + word.substring(randomPosition + 1));
                        }
                    } else {
                        for (int i = 0; i < 3; i++) {
                            mFinallWords.get(j).add(word.substring(0, randomPosition) + --c + word.substring(randomPosition + 1));
                        }
                    }
                } else if (c > 1039 && c < 1072) {
                    if (1070 - c > 2) {
                        for (int i = 0; i < 3; i++) {
                            mFinallWords.get(j).add(word.substring(0, randomPosition) + ++c + word.substring(randomPosition + 1));
                        }
                    } else {
                        for (int i = 0; i < 3; i++) {
                            mFinallWords.get(j).add(word.substring(0, randomPosition) + --c + word.substring(randomPosition + 1));
                        }
                    }
                } else {
                    if (1102 - c > 2) {
                        for (int i = 0; i < 3; i++) {
                            mFinallWords.get(j).add(word.substring(0, randomPosition) + ++c + word.substring(randomPosition + 1));
                        }
                    } else {
                        for (int i = 0; i < 3; i++) {
                            mFinallWords.get(j).add(word.substring(0, randomPosition) + --c + word.substring(randomPosition + 1));
                        }
                    }
                }
            }

            for (ArrayList<String> strings : mFinallWords) {
                Collections.shuffle(strings);
            }

            for (int i = 0; i < mCorrectWords.size(); i++) {
                for (ArrayList<String> list : mFinallWords) {
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).equals(mCorrectWords.get(i))) {
                            mCorrectPosition[i] = j;
                        }
                    }
                }
            }

            Log.i("dsa", "Dasda");
        }
    }

    private void initViews() {
        mCurrentPosition = -1;
        mProgressBar.setMax(MAX_QUESTIONS);
        increaseProgressCounter();

        mTVExamWord.setText(mNativeWords.get(mQuestion));

        mExamAdapter = new ExamAdapter(mActivity, mFinallWords.get(mQuestion), this);
        mLVExamOptionsList.setAdapter(mExamAdapter);
        setNextButtonUi(NOT_SELECTED);
    }

    public void increaseProgressCounter() {
        mProgressBar.setProgress(++mProgressCounter);
    }

    private void setNextButtonUi(int type) {
        mBtnNextCurrentStatus = type;
        mBTNCheck.setBackgroundResource(mBtnDrawableResources[type]);
        mBTNCheck.setText(mBtnTextResources[type]);

        if (type == NOT_SELECTED) {
            mBTNCheck.setEnabled(false);
            mBTNCheck.setClickable(false);
        } else {
            mBTNCheck.setEnabled(true);
            mBTNCheck.setClickable(true);
        }
    }

    private void setNextQuestion() {
        mTVExamWord.setText(mNativeWords.get(++mQuestion));
        mExamAdapter.next(mFinallWords.get(mQuestion));
        setNextButtonUi(NOT_SELECTED);
    }

    @Override
    public void onClickItem(int position) {
        if (mBtnNextCurrentStatus != CORRECT && mBtnNextCurrentStatus != WRONG) {
            mCurrentPosition = position;
            mExamAdapter.changeUiByClick(position);
        }
    }

    @Override
    public void changeButtonUi(int status) {
        setNextButtonUi(status);
    }

    @OnClick(R.id.btn_check)
    void onClickNext() {
        if (mCurrentPosition != -1) {

            if (mBtnNextCurrentStatus == SELECTED) {
                mExamAdapter.setCorrectAnswer(mCorrectPosition[mProgressCounter - 1]);

                if (mCurrentPosition == mCorrectPosition[mProgressCounter - 1]) {
                    setNextButtonUi(CORRECT);
                    mCorrectAnswers++;
                } else {
                    setNextButtonUi(WRONG);
                }
            } else {

                if (mProgressCounter < MAX_QUESTIONS) {
                    increaseProgressCounter();
                    setNextQuestion();
                } else {
                    mActivity.onClickFinish(mCorrectAnswers, MAX_QUESTIONS);
                }
            }
        }
    }

    @OnClick(R.id.back_btn)
    void onClickCancel() {
        mActivity.onBackPressed();
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
    }
}


