package io.ideaction.stori.fragments.probation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.ideaction.stori.R;
import io.ideaction.stori.activities.ProbationActivity;

public class ExamResultFragment extends Fragment {

    private static final String TAG = "ExamResultFragment";
    private static final int HALF_SCREEN = 0;
    private static final int BAD = 0;
    private static final int GOOD = 1;
    private static final int GREAT = 2;
    private static final int DEFAULT = 3;
    private static final String CORRECT_ANSWERS = "io.ideaction.stori.fragments.probation.correctAnswers";
    private static final String NR_OF_QUESTIONS = "io.ideaction.stori.fragments.probation.nrOfQuestions";

    @BindView(R.id.iv_oval_shape)
    ImageView mIVOvalShape;
    @BindView(R.id.tv_result_motivation)
    TextView mTVResultText;
    @BindView(R.id.tv_exam_result)
    TextView mTVExamResult;
    @BindView(R.id.btn_once_again)
    Button mBTNOnceAgain;
    @BindView(R.id.btn_finish)
    Button mBTNFinish;

    private ProbationActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;

    private int mNumberOfQuestions;
    private int mNumberOfCorrectAnswers;
    private double mPercentageOfCorrectAnswers;

    public static ExamResultFragment newInstance(int i, int j) {
        ExamResultFragment examResultFragment = new ExamResultFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(CORRECT_ANSWERS, i);
        bundle.putInt(NR_OF_QUESTIONS, j);
        examResultFragment.setArguments(bundle);

        return examResultFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mNumberOfQuestions = getArguments().getInt(NR_OF_QUESTIONS, 0);
            mNumberOfCorrectAnswers = getArguments().getInt(CORRECT_ANSWERS, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_exam_result, container, false);

        mPercentageOfCorrectAnswers = (double)mNumberOfCorrectAnswers / (double)mNumberOfQuestions;
        initFragment();
        initViews();

        return mView;
    }

    private void initViews() {
        mTVExamResult.setText(String.valueOf(mNumberOfCorrectAnswers + "/" + mNumberOfQuestions));

        if (mPercentageOfCorrectAnswers < 0.1) {
            changeFragmentUi(DEFAULT);
        } else if (mPercentageOfCorrectAnswers > 0.1 && mPercentageOfCorrectAnswers <= 0.5) {
            changeFragmentUi(BAD);
        } else if (mPercentageOfCorrectAnswers < 0.9) {
            changeFragmentUi(GOOD);
        } else if (mPercentageOfCorrectAnswers >= 0.9) {
            changeFragmentUi(GREAT);
        }
    }

    private void initFragment() {
        mActivity = (ProbationActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, mView);
    }

    private void changeFragmentUi(int type) {
        if (type == BAD) {
            mIVOvalShape.setImageResource(R.drawable.ic_oval_weak);
            mTVResultText.setText(getString(R.string.you_can_do_much_better));
            mBTNFinish.setBackgroundResource(R.drawable.btn_listen_grey);
            mBTNOnceAgain.setVisibility(View.VISIBLE);
        } else if (type == GOOD) {
            mIVOvalShape.setImageResource(R.drawable.ic_oval_medium);
            mTVResultText.setText(getString(R.string.not_bad));
            mBTNFinish.setBackgroundResource(R.drawable.btn_listen_grey);
            mBTNOnceAgain.setVisibility(View.VISIBLE);
        } else if (type == GREAT) {
            mIVOvalShape.setImageResource(R.drawable.ic_oval_great);
            mTVResultText.setText(getString(R.string.great));
            mBTNFinish.setBackgroundResource(R.drawable.btn_read_orange);
            mBTNOnceAgain.setVisibility(View.GONE);
        } else if (type == DEFAULT){
            mIVOvalShape.setImageResource(R.drawable.ic_oval_default);
            mTVResultText.setText(getString(R.string.you_can_do_much_better));
            mBTNFinish.setBackgroundResource(R.drawable.btn_listen_grey);
            mBTNOnceAgain.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.btn_finish)
    void onClickFinish(){
        mActivity.onBackPressed();
    }

    @OnClick(R.id.btn_once_again)
    void onClickOnceAgain(){
        changeFragmentUi(DEFAULT);
        mNumberOfCorrectAnswers = 0;
        mActivity.onClickOnceAgain();
    }

}
