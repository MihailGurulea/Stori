package io.ideaction.stori.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.activities.LogInActivity;
import io.ideaction.stori.models.ResetPasswordModel;
import io.ideaction.stori.utils.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FirstForgotPasswordFragment extends Fragment {

    private static final String TAG = "YourFragmentName";
    @BindView(R.id.et_email_layer)
    TextInputLayout mTILEmail;
    @BindView(R.id.et_email)
    TextInputEditText mTIETEmail;


    private LogInActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;

    private Call<ResetPasswordModel> mCall;

    public static FirstForgotPasswordFragment newInstance() {
        return new FirstForgotPasswordFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_first_forgot_password, container, false);

        initFragment();
        initViews();
        showKeyBoard();

        return mView;
    }

    private void initFragment() {
        mActivity = (LogInActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, mView);
    }

    private void initViews() {
        mTIETEmail.requestFocus();
        mTIETEmail.setHint(getString(R.string.enter_email));
    }

    private void showKeyBoard() {
        new CountDownTimer(100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Validations.showKeyboard(mActivity);
            }
        }.start();
    }

    @OnClick(R.id.tv_sign_up)
    void onClickBackToSignUp() {
        Validations.hideKeyboard(mActivity);
        mActivity.onClickSignUp();
    }

    @OnClick(R.id.tv_log_in)
    void onClickBackToLogIn() {
        Validations.hideKeyboard(mActivity);
        mActivity.onClickLogIn();
    }

    @OnClick(R.id.next_btn)
    void onClickNextButton() {
        String email = String.valueOf(mTIETEmail.getText());
        mActivity.showProgressBar();
        Validations.hideKeyboard(mActivity);

        if (!isEmailValid(email)) {
            mActivity.hideProgressBar();
        } else {
            mActivity.setEmail(email);
            forgotPasswordFirstCallback(email);
        }
    }

    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            mTIETEmail.setError("This field is empty");
            return false;
        } else if (!Validations.isValidEmail(email)) {
            mTIETEmail.setError("You need to provide a valid E-mail");
            return false;
        }

        return true;
    }

    private void forgotPasswordFirstCallback(String email) {
        mCall = StoriApplication.apiInterface().forgotPasswordFirstStep(email);

        mCall.enqueue(new Callback<ResetPasswordModel>() {
            @Override
            public void onResponse(Call<ResetPasswordModel> call, Response<ResetPasswordModel> response) {
                mActivity.hideProgressBar();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toasty.success(mActivity, response.body().getMessage(), Toasty.LENGTH_LONG).show();
                    }
                    mActivity.onClickSecondForgotPassword();
                } else {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject json = new JSONObject(response.errorBody().string());

                            if (json.has("message")) {
                                Toasty.error(mActivity, String.valueOf(json.get("message")), Toasty.LENGTH_LONG).show();
                            } else {
                                Toasty.error(mActivity, "Oops, something went wrong...", Toasty.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                            Toasty.error(mActivity, "Oops, something went wrong...", Toasty.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordModel> call, Throwable t) {
                mActivity.hideProgressBar();
                Log.e(TAG, t.getMessage(), t);
                Toasty.error(mActivity, "Oops, something went wrong...", Toasty.LENGTH_LONG).show();
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
}

