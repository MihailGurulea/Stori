package io.ideaction.stori.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jaychang.sa.AuthCallback;
import com.jaychang.sa.SocialUser;
import com.jaychang.sa.facebook.SimpleAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.fragments.FirstForgotPasswordFragment;
import io.ideaction.stori.fragments.LanguageSelectionFragment;
import io.ideaction.stori.fragments.RegistrationFragment;
import io.ideaction.stori.fragments.SecondForgotPasswordFragment;
import io.ideaction.stori.fragments.SignInFragment;
import io.ideaction.stori.models.UserModel;
import io.ideaction.stori.network.GetStorisAsync;
import io.ideaction.stori.utils.FragmentEntranceSide;
import io.ideaction.stori.utils.Languages;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInActivity extends AppCompatActivity implements GetStorisAsync.GetStorisAsyncListener {

    private static final String TAG = "LogInActivity";
    private static final String FACEBOOK = "facebook";
    private static final String GOOGLE = "google";

    @BindView(R.id.progress_circular)
    ProgressBar mProgressBar;

    private String mEmail;
    private boolean mIsFromSignUp;

    private Call<UserModel> mCall;

    public static Intent startActivity(Context context) {
        return new Intent(context, LogInActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = new Locale(Languages.values()[StoriApplication.getInstance().getNativeLang()].getCodeLowerCase());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_log_in);

        ButterKnife.bind(this);

        setFragment(SignInFragment.newInstance(), FragmentEntranceSide.STANDARD);
    }

    public void setFragment(Fragment fragment, FragmentEntranceSide fragmentEntranceSide) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (fragmentEntranceSide) {
            case RIGHT:
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left);
                break;
            case LEFT:
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right);
                break;
            case BOTTOM:
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_from_bottom);
                break;
        }
        fragmentTransaction.replace(R.id.log_in_container, fragment);
        fragmentTransaction.commit();
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void setFromSignUp(boolean fromSignUp) {
        mIsFromSignUp = fromSignUp;
    }

    public void onClickSignUp() {
        setFragment(RegistrationFragment.newInstance(), FragmentEntranceSide.LEFT);
    }

    public void onClickLogIn() {
        setFragment(SignInFragment.newInstance(), FragmentEntranceSide.RIGHT);
    }

    public void onClickToLanguageSelection() {
        setFragment(LanguageSelectionFragment.newInstance(), FragmentEntranceSide.RIGHT);
    }

    public void onClickFirstForgotPassword() {
        setFragment(FirstForgotPasswordFragment.newInstance(), FragmentEntranceSide.RIGHT);
    }

    public void onClickSecondForgotPassword() {
        setFragment(SecondForgotPasswordFragment.newInstance(), FragmentEntranceSide.RIGHT);
    }

    public void onClickLoginFacebook() {
        showProgressBar();
        SimpleAuth.connectFacebook(new ArrayList<>(), new AuthCallback() {
            @Override
            public void onSuccess(SocialUser socialUser) {
                Log.d(TAG, "userId:" + socialUser.userId);
                Log.d(TAG, "email:" + socialUser.email);
                Log.d(TAG, "accessToken:" + socialUser.accessToken);
                Log.d(TAG, "profilePictureUrl:" + socialUser.profilePictureUrl);
                Log.d(TAG, "username:" + socialUser.username);
                Log.d(TAG, "fullName:" + socialUser.fullName);
                Log.d(TAG, "pageLink:" + socialUser.pageLink);

                String username = socialUser.fullName.toLowerCase().replace(" ", "");
                socialLogin(socialUser.email, username, FACEBOOK, socialUser.userId, socialUser.profilePictureUrl);
            }

            @Override
            public void onError(Throwable error) {
                hideProgressBar();
                Log.d(TAG, error.getMessage(), error);
                Toasty.error(LogInActivity.this, "Oops, something went wrong...", Toasty.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                hideProgressBar();
                Log.d(TAG, "Canceled");
                Toasty.info(LogInActivity.this, "Canceled", Toasty.LENGTH_LONG).show();
            }
        });
    }

    public void onClickLoginGoogle() {
        showProgressBar();
        List<String> scopes = Arrays.asList(
                "https://www.googleapis.com/auth/userinfo.email",
                "https://www.googleapis.com/auth/userinfo.profile"
        );

        com.jaychang.sa.google.SimpleAuth.connectGoogle(scopes, new AuthCallback() {
            @Override
            public void onSuccess(SocialUser socialUser) {
                Log.d(TAG, "userId:" + socialUser.userId);
                Log.d(TAG, "email:" + socialUser.email);
                Log.d(TAG, "accessToken:" + socialUser.accessToken);
                Log.d(TAG, "profilePictureUrl:" + socialUser.profilePictureUrl);
                Log.d(TAG, "username:" + socialUser.username);
                Log.d(TAG, "fullName:" + socialUser.fullName);
                Log.d(TAG, "pageLink:" + socialUser.pageLink);

                String username = socialUser.fullName.toLowerCase().replace(" ", "");
                socialLogin(socialUser.email, username, GOOGLE, socialUser.userId, socialUser.profilePictureUrl);
            }

            @Override
            public void onError(Throwable error) {
                hideProgressBar();
                Log.d(TAG, error.getMessage(), error);
                Toasty.error(LogInActivity.this, "Oops, something went wrong...", Toasty.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                hideProgressBar();
                Log.d(TAG, "Canceled");
                Toasty.info(LogInActivity.this, "Canceled", Toasty.LENGTH_LONG).show();
            }
        });
    }

    private void socialLogin(String email, String username, String provider, String userId, String avatarLink) {
        mCall = StoriApplication.apiInterface().socialLogin(
                email,
                username,
                provider,
                userId,
                avatarLink
        );

        mCall.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    response.body().setPassword("");
                    StoriApplication.getInstance().setUserCredentials(response.body());
                    StoriApplication.getInstance().setToken(response.headers().get("Authorization"));
                    Log.i(TAG, StoriApplication.getInstance().getUserCredentials().toString());
                    if (response.body().getLang_to_learn() == 0 || response.body().getNative_lang() == 0) {
                        hideProgressBar();
                        setFromSignUp(true);
                        onClickToLanguageSelection();
                    } else {
                        setFromSignUp(false);
                        new GetStorisAsync(LogInActivity.this).execute();
                    }
                } else {
                    hideProgressBar();
                    Toasty.error(LogInActivity.this, "Credentials is invalid", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                hideProgressBar();
                Log.i(TAG, t.getMessage(), t);
                Toasty.error(LogInActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showProgressBar() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.GONE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.log_in_container);

        if (fragment instanceof LanguageSelectionFragment) {
            if (mIsFromSignUp) {
                setFragment(SignInFragment.newInstance(), FragmentEntranceSide.LEFT);
            } else {
                setFragment(RegistrationFragment.newInstance(), FragmentEntranceSide.LEFT);
            }
        } else if (fragment instanceof RegistrationFragment) {
            setFragment(SignInFragment.newInstance(), FragmentEntranceSide.RIGHT);
        } else if (fragment instanceof FirstForgotPasswordFragment) {
            setFragment(SignInFragment.newInstance(), FragmentEntranceSide.LEFT);
        } else if (fragment instanceof SecondForgotPasswordFragment){
            setFragment(FirstForgotPasswordFragment.newInstance(), FragmentEntranceSide.RIGHT);
        } else if (fragment instanceof SignInFragment) {
            finishAffinity();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

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
        hideProgressBar();
        if (e != null) {
            Log.i(TAG, e.getMessage(), e);
            Toasty.error(LogInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSuccess() {
        hideProgressBar();
        startActivity(MainMenuActivity.startActivity(LogInActivity.this));
    }
}
