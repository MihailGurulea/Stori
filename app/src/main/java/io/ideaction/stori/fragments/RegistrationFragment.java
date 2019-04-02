package io.ideaction.stori.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

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
import io.ideaction.stori.models.UserModel;
import io.ideaction.stori.models.UserModel;
import io.ideaction.stori.network.GetStorisAsync;
import io.ideaction.stori.utils.JSONParser;
import io.ideaction.stori.utils.Utils;
import io.ideaction.stori.utils.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationFragment extends Fragment {

    private static final String TAG = "RegistrationFragment";

    @BindView(R.id.et_username)
    TextInputEditText mTIETName;
    @BindView(R.id.et_email)
    TextInputEditText mTIETEmail;
    @BindView(R.id.et_password)
    TextInputEditText mTIETPassword;
    @BindView(R.id.et_repeat_password)
    TextInputEditText mTIETRepeatPassword;
    @BindView(R.id.et_username_layer)
    TextInputLayout mTILName;
    @BindView(R.id.et_email_layer)
    TextInputLayout mTILEmail;
    @BindView(R.id.et_password_layer)
    TextInputLayout mTILPassword;
    @BindView(R.id.et_repeat_password_layer)
    TextInputLayout mTILRepeatPassword;
    @BindView(R.id.tv_tutorial_title)
    TextView mTVTutorialTitle;
    @BindView(R.id.log_in_plus_terms_line)
    LinearLayout mLLLogInLine;
    @BindView(R.id.sign_up_image)
    ImageView mIVSignUpImage;

    private LogInActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;
    private Unregistrar mUnregistrar;

    private boolean mIsKeyboardVisible;

    private Call<UserModel> mCall;

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_registration, container, false);

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
        mTIETPassword.setTypeface(StoriApplication.getInstance().getCeraProLight());
        mTIETPassword.setTransformationMethod(new PasswordTransformationMethod());
        mTIETRepeatPassword.setTypeface(StoriApplication.getInstance().getCeraProLight());
        mTIETRepeatPassword.setTransformationMethod(new PasswordTransformationMethod());
    }

    private void initListeners() {
        mUnregistrar = KeyboardVisibilityEvent.registerEventListener(
                mActivity,
                isOpen -> {
                    mIsKeyboardVisible = isOpen;
                    if (isOpen) {
                        keyboardIsVisible();
                    } else {
                        keyboardIsHide();
                    }
                });

        onChangeFocus(mTIETName, getString(R.string.enter_username));

        onChangeFocus(mTIETEmail, getString(R.string.enter_email));

        onChangeFocus(mTIETPassword, getString(R.string.enter_password));

        onChangeFocus(mTIETRepeatPassword, getString(R.string.repeat_password));
    }

    private void keyboardIsVisible() {
        mTVTutorialTitle.setVisibility(View.GONE);
        mLLLogInLine.setVisibility(View.GONE);

        RelativeLayout.LayoutParams imageParam = (RelativeLayout.LayoutParams) mIVSignUpImage.getLayoutParams();
        imageParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        imageParam.addRule(RelativeLayout.ALIGN_PARENT_END);
        imageParam.setMargins(0, (int) Utils.convertDpToPixel(-37F, mActivity), 0, 0);
        mIVSignUpImage.setLayoutParams(imageParam);
        mIVSignUpImage.requestLayout();
    }

    private void keyboardIsHide() {
        mTVTutorialTitle.setVisibility(View.VISIBLE);
        mLLLogInLine.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams imageParam = (RelativeLayout.LayoutParams) mIVSignUpImage.getLayoutParams();
        imageParam.setMargins(0, 0, 0, 0);
        imageParam.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        imageParam.addRule(RelativeLayout.CENTER_VERTICAL);
        mIVSignUpImage.setLayoutParams(imageParam);
        mIVSignUpImage.requestLayout();
    }

    @OnClick(R.id.tv_log_in)
    void onClickLogin() {
        Validations.hideKeyboard(mActivity);
        mActivity.onClickLogIn();
    }

    @OnClick(R.id.next_btn)
    void onClickNextBTN() {
        String username = String.valueOf(mTIETName.getText());
        String email = String.valueOf(mTIETEmail.getText());
        String password = String.valueOf(mTIETPassword.getText());
        String repeatPassword = String.valueOf(mTIETRepeatPassword.getText());

        Validations.hideKeyboard(mActivity);
        mActivity.showProgressBar();
        if (!isRegistrationDataValid(username, email, password, repeatPassword)) {
            mActivity.hideProgressBar();
        } else {
            firstRegistrationCallBack(username, email, password, repeatPassword);
        }
    }

    private boolean isRegistrationDataValid(String username, String email, String password, String repeatPassword) {
        if (TextUtils.isEmpty(username)) {
            mTIETName.setError("Username field is empty");
            return false;
        } else if (username.toCharArray().length < 3) {
            mTIETName.setError("Username should be at least 3 characters");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            mTIETEmail.setError("Email field is empty");
            return false;
        } else if (!Validations.isValidEmail(email)) {
            mTIETEmail.setError("You need to provide a valid E-mail");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            mTIETPassword.setError("Password field is empty");
            return false;
        } else if (!Validations.isValidPassword(password)) {
            mTIETPassword.setError("Password should be at least 6 characters and contain a capital letter, a lowercase letter and a digit");
            return false;
        }

        if (TextUtils.isEmpty(repeatPassword)) {
            mTIETRepeatPassword.setError("Repeat password field is empty");
            return false;
        } else if (!Validations.isPasswordMatching(password, repeatPassword)) {
            mTIETRepeatPassword.setError("Passwords should match");
            return false;
        }

        return true;
    }

    private void firstRegistrationCallBack(String username, String email, String password, String repeatPassword) {
        mCall = StoriApplication.apiInterface().register(
                email,
                username,
                password,
                repeatPassword
        );

        mCall.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                mActivity.hideProgressBar();

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        response.body().setPassword(password);
                        StoriApplication.getInstance().setUserCredentials(response.body());
                        StoriApplication.getInstance().setToken(response.headers().get("Authorization"));
                        Log.i(TAG, StoriApplication.getInstance().getUserCredentials().toString());
                        mActivity.setFromSignUp(false);
                        mActivity.onClickToLanguageSelection();
                    } else {
                        Toasty.error(mActivity, "Credentials is invalid", Toast.LENGTH_LONG).show();
                    }
                } else {
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
}
