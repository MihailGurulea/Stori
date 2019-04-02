package io.ideaction.stori.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.fragments.main_menu.MainMenuFragment;
import io.ideaction.stori.fragments.main_menu.ProfileFragment;
import io.ideaction.stori.fragments.main_menu.VocabularyFragment;
import io.ideaction.stori.utils.FragmentEntranceSide;
import io.ideaction.stori.utils.Languages;

public class MainMenuActivity extends AppCompatActivity {

    private static final String ARRIVED_FROM_GET_PREMIUM = "io.ideaction.stori.activities.arrivedFromGetPremium";
    private static final String TAG = "MainMenuActivity";

    @BindView(R.id.progress_circular)
    ProgressBar mProgressBar;
    @BindView(R.id.bottom_nav_menu)
    BottomNavigationView mBottomNavigationView;

    public static Intent startActivity(Context context) {
        return new Intent(context, MainMenuActivity.class);
    }

    public static Intent startActivityFromGetPremium(Context context) {
        Intent intent = new Intent(context, MainMenuActivity.class);
        intent.putExtra(ARRIVED_FROM_GET_PREMIUM, true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = new Locale(Languages.values()[StoriApplication.getInstance().getNativeLang()].getCodeLowerCase());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main_menu);

        ButterKnife.bind(this);

        setBottomNavigationView();
    }

    private void setBottomNavigationView() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_learn:
                    setFragment(MainMenuFragment.newInstance(), FragmentEntranceSide.STANDARD);
                    break;
                case R.id.nav_vocabulary:
                    setFragment(VocabularyFragment.newInstance(), FragmentEntranceSide.STANDARD);
                    break;
                case R.id.nav_profile:
                    setFragment(ProfileFragment.newInstance(), FragmentEntranceSide.STANDARD);
                    break;
            }

            return true;
        });

        if (getIntent() != null && getIntent().getBooleanExtra(ARRIVED_FROM_GET_PREMIUM, false)) {
            mBottomNavigationView.setSelectedItemId(R.id.nav_profile);
        } else {
            mBottomNavigationView.setSelectedItemId(R.id.nav_learn);
        }
    }

    private void setFragment(Fragment fragment, FragmentEntranceSide fragmentEntranceSide) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.log_in_container, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        switch (fragmentEntranceSide) {
            case RIGHT:
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left);
                break;
            case LEFT:
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right);
                break;
        }
        fragmentTransaction.commit();
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

    public void onClickGetPremium() {
        startActivity(GetPremiumActivity.startActivity(MainMenuActivity.this));
    }

    public void onClickSearch() {
        startActivity(SearchStoriesActivity.startActivity(MainMenuActivity.this));
    }

    public void onClickLetsTrain() {
        startActivity(ProbationActivity.startActivity(MainMenuActivity.this));
    }

    public void onClickStori(int storiId) {
        startActivity(StartStoriActivity.startActivity(MainMenuActivity.this, storiId));
    }

    public void onClickTextToAFriend() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Stori");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.google.android.apps.plus");
        startActivity(Intent.createChooser(intent, "Share with"));
    }

    public void onClickRateUs() {
        Uri uri = Uri.parse("market://details?id=" + MainMenuActivity.this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.plus" + MainMenuActivity.this.getPackageName())));
        }
    }

    public void onClickSendUsFeedback() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:abc@xyz.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Honest feedback");
        startActivity(Intent.createChooser(emailIntent, "Send feedback"));
    }

    public void onClickSignOut() {
        new SweetAlertDialog(MainMenuActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Do you really want to sign out?")
                .setConfirmText("Yes,I do!")
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    startActivity(LogInActivity.startActivity(MainMenuActivity.this));
                    MainMenuActivity.this.finishAffinity();
                })
                .setCancelButton("Nope", SweetAlertDialog::dismissWithAnimation)
                .show();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.log_in_container);

        if (fragment instanceof ProfileFragment ||
                fragment instanceof VocabularyFragment) {
            mBottomNavigationView.setSelectedItemId(R.id.nav_learn);
        } else {
            finishAffinity();
            System.exit(0);
        }
    }

}