package io.ideaction.stori.fragments.main_menu;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.activities.MainMenuActivity;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    @BindView(R.id.sign_out)
    TextView mSignOutTextView;
    @BindView(R.id.get_premium_tv)
    TextView mGetPremiumTextView;
    @BindView(R.id.send_us_your_feedback_tv)
    TextView mSendFeedbackTextView;
    @BindView(R.id.text_to_a_friend_tv)
    TextView mTextAFriendTextView;
    @BindView(R.id.rate_us_on_app_store_tv)
    TextView mRaTeUsTextView;
    @BindView(R.id.privacy_policy)
    TextView mPrivacyPolicyTextView;
    @BindView(R.id.s_spinner)
    Spinner mSpinner;

    private MainMenuActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        initFragment();

        setSpinner();

        return mView;
    }

    private void setSpinner() {
        String[] languages = getResources().getStringArray(R.array.spinnerLanguages);
        ArrayList<String> sortedLanguages = new ArrayList<>();
        sortedLanguages.add(languages[StoriApplication.getInstance().getNativeLang() - 1]);

        for (int i = 0; i < languages.length; i++) {
            if (i != StoriApplication.getInstance().getNativeLang() - 1) {
                sortedLanguages.add(languages[i]);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, sortedLanguages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent refresh = new Intent(mActivity, MainMenuActivity.class);
                mSpinner.setSelection(position);
                if (position != 0) {
                    Locale locale;
                    int langPosition;

                    if (String.valueOf(mSpinner.getSelectedItem()).equals(getResources().getString(R.string.english))) {
                        locale = new Locale("en", "US");
                        langPosition = 1;
                        startActivity(refresh);
                        mActivity.finish();
                    } else if (String.valueOf(mSpinner.getSelectedItem()).equals(getResources().getString(R.string.spanish))) {
                        locale = new Locale("es", "ES");
                        langPosition = 2;
                        startActivity(refresh);
                        mActivity.finish();
                    } else if (String.valueOf(mSpinner.getSelectedItem()).equals(getResources().getString(R.string.french))) {
                        locale = new Locale("fr", "FR");
                        langPosition = 3;
                        startActivity(refresh);
                        mActivity.finish();
                    } else if (String.valueOf(mSpinner.getSelectedItem()).equals(getResources().getString(R.string.german))) {
                        locale = new Locale("de", "DE");
                        langPosition = 4;
                        startActivity(refresh);
                        mActivity.finish();
                    } else if (String.valueOf(mSpinner.getSelectedItem()).equals(getResources().getString(R.string.italian))) {
                        locale = new Locale("it", "IT");
                        langPosition = 5;
                        startActivity(refresh);
                        mActivity.finish();
                    } else {
                        locale = new Locale("ru", "RU");
                        langPosition = 6;
                        startActivity(refresh);
                        mActivity.finish();
                    }

                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.locale = locale;
                    res.updateConfiguration(conf, dm);
                    StoriApplication.getInstance().setNativeLang(langPosition);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mSpinner.setAdapter(adapter);
    }

    private void initFragment() {
        mActivity = (MainMenuActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, mView);
    }

    @OnClick(R.id.get_premium_tv)
    void onClickGetPremium() {
        mActivity.onClickGetPremium();
    }

    @OnClick(R.id.text_to_a_friend_tv)
    void onClickTextToAFriend() {
        mActivity.onClickTextToAFriend();
    }

    @OnClick(R.id.rate_us_on_app_store_tv)
    void onClickRateUs() {
        mActivity.onClickRateUs();
    }

    @OnClick(R.id.send_us_your_feedback_tv)
    void onClickSendUsFeedback() {
        mActivity.onClickSendUsFeedback();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mActivity != null) {
            mActivity = null;
        }

        if (mView != null) {
            mView = null;
        }

        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }

    @OnClick(R.id.sign_out)
    void onClickSignOutBTN() {
        mActivity.onClickSignOut();
    }
}