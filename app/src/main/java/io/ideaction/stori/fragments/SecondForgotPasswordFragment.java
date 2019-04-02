package io.ideaction.stori.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.activities.LogInActivity;
import io.ideaction.stori.activities.MainMenuActivity;
import io.ideaction.stori.models.UserModel;
import io.ideaction.stori.network.GetStorisAsync;
import io.ideaction.stori.utils.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SecondForgotPasswordFragment extends Fragment implements GetStorisAsync.GetStorisAsyncListener {

    private static final String TAG = "SecondForgotPasswordFg";

    @BindView(R.id.et_code)
    TextInputEditText mTIETToken;
    @BindView(R.id.et_new_password)
    TextInputEditText mTIETNewPassword;
    @BindView(R.id.et_repeat_new_password)
    TextInputEditText mTIETRepeatNewPassword;


    private LogInActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;
    private Unregistrar mUnregistrar;

    private boolean mIsKeyboardVisible;

    private Call<UserModel> mCall;

    public static SecondForgotPasswordFragment newInstance() {
        return new SecondForgotPasswordFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_second_forgot_password, container, false);

        initFragment();
        initListeners();
        initViews();

        return mView;
    }

    private void initFragment() {
        mActivity = (LogInActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, mView);
    }

    private void initViews() {
        mTIETNewPassword.setTypeface(StoriApplication.getInstance().getCeraProLight());
        mTIETNewPassword.setTransformationMethod(new PasswordTransformationMethod());
        mTIETRepeatNewPassword.setTypeface(StoriApplication.getInstance().getCeraProLight());
        mTIETRepeatNewPassword.setTransformationMethod(new PasswordTransformationMethod());
    }

    private void initListeners() {
        mUnregistrar = KeyboardVisibilityEvent.registerEventListener(
                mActivity,
                isOpen -> {
                    mIsKeyboardVisible = isOpen;
                });

        onChangeFocus(mTIETToken, getString(R.string.enter_code));

        onChangeFocus(mTIETNewPassword, getString(R.string.enter_new_password));

        onChangeFocus(mTIETRepeatNewPassword, getString(R.string.enter_new_password));
    }


    private boolean areFieldsValidated(String code, String pwd1, String pwd2) {
        if (TextUtils.isEmpty(code)) {
            mTIETToken.setError("This field should not be empty");
            return false;
        }

        if (TextUtils.isEmpty(pwd1)) {
            mTIETNewPassword.setError("This field should not be empty");
            return false;
        } else if (!Validations.isValidPassword(pwd1)) {
            mTIETNewPassword.setError("Password should be at least 6 characters and contain a capital letter, a lowercase letter and a digit");
            return false;
        }

        if (TextUtils.isEmpty(pwd2)) {
            mTIETRepeatNewPassword.setError("This field should not be empty");
            return false;
        }

        if (!Validations.isPasswordMatching(pwd1, pwd2)) {
            mTIETRepeatNewPassword.setError("Passwords should match");
            return false;
        }

        return true;
    }

    @OnClick(R.id.next_btn)
    void onClickNextButton() {
        String code = String.valueOf(mTIETToken.getText());
        String pwd1 = String.valueOf(mTIETNewPassword.getText());
        String pwd2 = String.valueOf(mTIETRepeatNewPassword.getText());

        mActivity.showProgressBar();
        Validations.hideKeyboard(mActivity);

        if (!areFieldsValidated(code, pwd1, pwd2)) {
            mActivity.hideProgressBar();
        } else {
            forgotPasswordSecondCallback(code, pwd1, pwd2);
        }
    }

    private void forgotPasswordSecondCallback(String code, String pwd1, String pwd2) {
        mCall = StoriApplication.apiInterface().forgotPasswordSecondStep(
                mActivity.getEmail(),
                code,
                pwd1,
                pwd2
        );

        mCall.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    response.body().setPassword(pwd1);
                    StoriApplication.getInstance().setUserCredentials(response.body());
                    Log.i(TAG, StoriApplication.getInstance().getUserCredentials().toString());
                    Toasty.success(mActivity, "Your password has been changed successfully! Thank you.", Toasty.LENGTH_LONG).show();
                    new GetStorisAsync(SecondForgotPasswordFragment.this).execute();
                } else {
                    mActivity.hideProgressBar();
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
            public void onFailure(Call<UserModel> call, Throwable t) {
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

        if (mUnregistrar != null) {
            mUnregistrar.unregister();
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

    private void onChangeFocus(TextInputEditText field, String hint) {

        field.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (field != null)
                    field.setHint(hint);

                if (!mIsKeyboardVisible)
                    Validations.showKeyboard(mActivity);
            } else {
                if (field != null)
                    field.setHint("");
            }
        });
    }

    @Override
    public void onError(Throwable e) {
        mActivity.hideProgressBar();
        if (e != null) {
            Log.e(TAG, e.getMessage(), e);
            Toasty.error(mActivity, "Oops, something went wrong...", Toasty.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSuccess() {
        mActivity.hideProgressBar();
        startActivity(MainMenuActivity.startActivity(mActivity));
    }
}

