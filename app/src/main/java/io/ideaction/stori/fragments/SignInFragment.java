package io.ideaction.stori.fragments;

import android.os.Bundle;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

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
import io.ideaction.stori.utils.Utils;
import io.ideaction.stori.utils.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SignInFragment extends Fragment implements GetStorisAsync.GetStorisAsyncListener {

    private static final String TAG = "SignInFragment";

    @BindView(R.id.title)
    TextView mTVTitle;
    @BindView(R.id.et_email)
    TextInputEditText mTIETEmail;
    @BindView(R.id.et_email_layer)
    TextInputLayout mTILEmail;
    @BindView(R.id.et_password)
    TextInputEditText mTIETPassword;
    @BindView(R.id.next_btn)
    Button mBTNNext;
    @BindView(R.id.fb_btn)
    Button mBTNFaceBook;
    @BindView(R.id.google_btn)
    Button mBTNGoogle;
    @BindView(R.id.tv_connect_with)
    TextView mTVConnectWith;
    @BindView(R.id.tv_sign_up)
    TextView mTVSignUp;
    @BindView(R.id.tv_forgot_password)
    TextView mTVForgotPassword;
    @BindView(R.id.sign_up_image)
    ImageView mIVSignUpImage;


    private LogInActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;
    private Unregistrar mUnregistrar;
    private Call<UserModel> mCallLogIn;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_sign_in, container, false);

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
        if (!TextUtils.isEmpty(StoriApplication.getInstance().getUserEmail())) {
            mTIETEmail.setText(StoriApplication.getInstance().getUserEmail());
        }
        if (!TextUtils.isEmpty(StoriApplication.getInstance().getUserPassword())) {
            mTIETPassword.setText(StoriApplication.getInstance().getUserPassword());
        }
    }

    private void initListeners() {
        mUnregistrar = KeyboardVisibilityEvent.registerEventListener(
                mActivity,
                isOpen -> {
                    if (isOpen) {
                        keyboardIsVisible();
                    } else {
                        keyboardIsHidden();
                    }
                });

        onChangeFocus(mTIETEmail, getString(R.string.enter_email));

        onChangeFocus(mTIETPassword, getString(R.string.enter_password));
    }

    private void changeVisibilityOfViewsTo(int visibility) {
        mTVTitle.setVisibility(visibility);
        mTVConnectWith.setVisibility(visibility);
        mBTNFaceBook.setVisibility(visibility);
        mBTNGoogle.setVisibility(visibility);
    }

    private void keyboardIsVisible() {
        changeVisibilityOfViewsTo(GONE);


        mIVSignUpImage.animate().translationY(Utils.getScreenHeight(mActivity) / -4);

        RelativeLayout.LayoutParams emailParam = (RelativeLayout.LayoutParams) mTILEmail.getLayoutParams();
        emailParam.addRule(RelativeLayout.BELOW, R.id.tv_tutorial_title);
        emailParam.setMargins((int) Utils.convertDpToPixel(26F, mActivity), (int) Utils.convertDpToPixel(10F, mActivity), 0, 0);
        mTILEmail.setLayoutParams(emailParam);
        mTILEmail.requestLayout();
    }

    private void keyboardIsHidden() {
        changeVisibilityOfViewsTo(VISIBLE);

        mIVSignUpImage.animate().translationY(0);

        RelativeLayout.LayoutParams emailParam = (RelativeLayout.LayoutParams) mTILEmail.getLayoutParams();
        emailParam.addRule(RelativeLayout.BELOW, R.id.title);
        emailParam.setMargins((int) Utils.convertDpToPixel(26F, mActivity), (int) Utils.convertDpToPixel(20F, mActivity), 0, 0);
        mTILEmail.setLayoutParams(emailParam);
        mTILEmail.requestLayout();
    }

    @OnClick(R.id.next_btn)
    void onClickNextButton() {
        String email = String.valueOf(mTIETEmail.getText());
        String password = String.valueOf(mTIETPassword.getText());

        Validations.hideKeyboard(mActivity);
        mActivity.showProgressBar();

        if (!dataValidation(email, password)) {
            mActivity.hideProgressBar();
        } else {
            login(email, password);
        }
    }

    private boolean dataValidation(String email, String password) {
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

        return true;
    }

    private void login(String email, String password) {
        mCallLogIn = StoriApplication.apiInterface().login(email, password);

        mCallLogIn.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        response.body().setPassword(password);
                        StoriApplication.getInstance().setUserCredentials(response.body());
                        StoriApplication.getInstance().setToken(response.headers().get("Authorization"));
                        Log.i(TAG, StoriApplication.getInstance().getUserCredentials().toString());

                        if (response.body().getLang_to_learn() == 0 || response.body().getNative_lang() == 0) {
                            mActivity.hideProgressBar();
                            mActivity.setFromSignUp(true);
                            mActivity.onClickToLanguageSelection();
                        } else {
                            mActivity.setFromSignUp(false);
                            new GetStorisAsync(SignInFragment.this).execute();
                        }
                    } else {
                        mActivity.hideProgressBar();
                        Toasty.error(mActivity, "Credentials is invalid", Toast.LENGTH_LONG).show();
                    }
                } else {
                    mActivity.hideProgressBar();
                    Toasty.error(mActivity, "Credentials is invalid", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                mActivity.hideProgressBar();
                Log.i(TAG, t.getMessage(), t);
                Toasty.error(mActivity, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.tv_forgot_password)
    void onClickForgotPassword() {
        Validations.hideKeyboard(mActivity);
        mActivity.onClickFirstForgotPassword();
    }

    @OnClick(R.id.tv_sign_up)
    void onClickSignUp() {
        Validations.hideKeyboard(mActivity);
        mActivity.onClickSignUp();
    }

    @OnClick(R.id.fb_btn)
    void onClickLoginFacebook() {
        mActivity.onClickLoginFacebook();
    }

    @OnClick(R.id.google_btn)
    void onClickLoginGoogle() {
        mActivity.onClickLoginGoogle();
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

        if (mCallLogIn != null) {

            if (mCallLogIn.isExecuted()) {
                mCallLogIn.cancel();
            }

            if (mCallLogIn.isCanceled()) {
                mCallLogIn = null;
            }
        }
    }

    private void onChangeFocus(TextInputEditText field, String hint) {
        field.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (field != null)
                    field.setHint(hint);
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
            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onSuccess() {
        mActivity.hideProgressBar();
        startActivity(MainMenuActivity.startActivity(mActivity));
    }
}
