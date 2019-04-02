package io.ideaction.stori.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.activities.LogInActivity;
import io.ideaction.stori.activities.MainMenuActivity;
import io.ideaction.stori.adapters.LanguageSelectionAdapter;
import io.ideaction.stori.models.UserModel;
import io.ideaction.stori.network.GetStorisAsync;
import io.ideaction.stori.utils.JSONParser;
import io.ideaction.stori.utils.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguageSelectionFragment extends Fragment implements GetStorisAsync.GetStorisAsyncListener {

    private static final String TAG = "LanguageSelectionFg";

    private static final int ENGLISH = 1;
    private static final int SPANISH = 2;
    private static final int FRENCH = 3;
    private static final int GERMAN = 4;
    private static final int ITALIAN = 5;
    private static final int RUSSIAN = 6;

    @BindView(R.id.list_view)
    ListView mListView;
    @BindView(R.id.tv_language_title)
    TextView mLanguageTitle;
    @BindView(R.id.btn_ready_start)
    Button mBTNReadyStart;

    private LogInActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;

    private LanguageSelectionAdapter mLanguageSelectionAdapter;
    private int mClickedCount;
    private Call<UserModel> mCall;

    public static LanguageSelectionFragment newInstance() {
        return new LanguageSelectionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_language_selection, container, false);

        initFragment();
        initViews();
        initListeners();

        return mView;
    }

    private void initFragment() {
        mActivity = (LogInActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, mView);
    }

    private void initViews() {
        mLanguageSelectionAdapter = new LanguageSelectionAdapter(mActivity);
        mListView.setAdapter(mLanguageSelectionAdapter);
    }

    private void initListeners() {
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            if (mClickedCount == 0) {
                mLanguageSelectionAdapter.onClickItemFirst(position);
                languageDidSelect();

                mClickedCount++;
            } else {
                if (position == mLanguageSelectionAdapter.getPositionSecond()) {
                    mLanguageSelectionAdapter.onClickItemSecond(-1);
                } else {
                    mLanguageSelectionAdapter.onClickItemSecond(position);
                }
            }

            if (mLanguageSelectionAdapter.getPositionFirst() != position && mClickedCount == 1) {
                if (mLanguageSelectionAdapter.getPositionSecond() == -1) {
                    mBTNReadyStart.setVisibility(View.GONE);
                } else {
                    mBTNReadyStart.setVisibility(View.VISIBLE);
                }
            } else {
                mBTNReadyStart.setVisibility(View.GONE);
            }
        });
    }

    @OnClick(R.id.btn_ready_start)
    void onClickStartLearning() {
        Validations.hideKeyboard(mActivity);
        mActivity.showProgressBar();
        secondRegistrationCallBack();
    }

    @OnClick(R.id.back_btn)
    void onClickBackBTN() {
        mActivity.onBackPressed();
    }

    private void languageDidSelect() {
        mLanguageTitle.setText(getString(R.string.would_like_to_learn));
    }

    private void secondRegistrationCallBack() {
        int nativeLang = mLanguageSelectionAdapter.getPositionFirst() + 1;
        int langToLearn = mLanguageSelectionAdapter.getPositionSecond() + 1;

        mCall = StoriApplication.apiInterface().updateLanguage(
                "Bearer " + StoriApplication.getInstance().getToken(),
                nativeLang,
                langToLearn
        );

        mCall.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    StoriApplication.getInstance().setUserCredentials(response.body());
                    Log.i(TAG, StoriApplication.getInstance().getUserCredentials().toString());
                    new GetStorisAsync(LanguageSelectionFragment.this).execute();
                } else {
                    mActivity.hideProgressBar();
                    try {
                        if (response.errorBody() != null) {
                            Toasty.error(mActivity, JSONParser.registerJSON(response.errorBody().string()), Toasty.LENGTH_LONG).show();
                        } else {
                            Toasty.error(mActivity, "Oops, something went wrong...", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        Toasty.error(mActivity, "Oops, something went wrong...", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                mActivity.hideProgressBar();
                Log.d(TAG, t.getMessage(), t);
                Toasty.error(mActivity, "Oops, something went wrong...", Toast.LENGTH_LONG).show();
            }
        });
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
    public void onError(Throwable e) {
        mActivity.hideProgressBar();
        if (e != null) {
            Log.d(TAG, e.getMessage(), e);
            Toasty.error(mActivity, "Oops, something went wrong...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSuccess() {
        mActivity.hideProgressBar();
        startActivity(MainMenuActivity.startActivity(mActivity));
    }
}
