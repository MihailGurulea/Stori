package io.ideaction.stori.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.adapters.TutorialPagerAdapter;
import io.ideaction.stori.fragments.tutorial.FirstTutorialFragment;
import io.ideaction.stori.fragments.tutorial.SecondTutorialFragment;
import io.ideaction.stori.fragments.tutorial.ThirdTutorialFragment;
import io.ideaction.stori.utils.FadeOutTransformation;
import io.ideaction.stori.utils.Languages;

public class TutorialActivity extends AppCompatActivity {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private TutorialPagerAdapter mPagerAdapter;

    public static Intent startActivity(Context context) {
        return new Intent(context, TutorialActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = new Locale(Languages.values()[StoriApplication.getInstance().getNativeLang()].getCodeLowerCase());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_tutorial_acitivity);

        ButterKnife.bind(this);

        initViewPager();
    }

    @OnClick(R.id.next_btn)
    public void onClickGetStarted() {
        StoriApplication.getInstance().setTutorial(true);
        startActivity(LogInActivity.startActivity(TutorialActivity.this));
    }

    private void initViewPager() {
        mPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());

        mPagerAdapter.addFragments(new FirstTutorialFragment());
        mPagerAdapter.addFragments(new SecondTutorialFragment());
        mPagerAdapter.addFragments(new ThirdTutorialFragment());

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, new FadeOutTransformation());
    }
}
