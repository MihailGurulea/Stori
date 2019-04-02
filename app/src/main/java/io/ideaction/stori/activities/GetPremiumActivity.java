package io.ideaction.stori.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.utils.Languages;

public class GetPremiumActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private static final String TAG = "GetPremiumActivity";
    BillingProcessor bp;
    @BindView(R.id.ll_back)
    LinearLayout mBackLL;
    @BindView(R.id.ll_unlimited_words)
    LinearLayout mUnlimitedWordsLL;
    @BindView(R.id.ll_no_ads)
    LinearLayout mNoAdsLL;
    @BindView(R.id.ll_unlimited_stories)
    LinearLayout mUnlimitedStoriesLL;
    @BindView(R.id.rl_montly_plan)
    RelativeLayout mMontlyPlan;
    @BindView(R.id.rl_quarterly_plan)
    RelativeLayout mQuarterlyPlan;
    @BindView(R.id.rl_annual_plan)
    RelativeLayout mAnnualPlan;
    @BindView(R.id.btn_trial)
    Button mTrialButton;

    public static Intent startActivity(Context context) {
        return new Intent(context, GetPremiumActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = new Locale(Languages.values()[StoriApplication.getInstance().getNativeLang()].getCodeLowerCase());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_get_premium);
        bp = new BillingProcessor(this, null, this);

        ButterKnife.bind(this);

    }

    @OnClick(R.id.ll_back)
    void onClickBack() {
        onBackPressed();
    }

    @OnClick(R.id.rl_montly_plan)
    void onClickMonthly() {
        bp.subscribe(GetPremiumActivity.this, "android.test.subscribe");
    }

    @OnClick(R.id.rl_quarterly_plan)
    void onClickQuarterly() {
        bp.subscribe(GetPremiumActivity.this, "android.test.subscribe");
    }

    @OnClick(R.id.rl_annual_plan)
    void onClickAnnual() {
        bp.subscribe(GetPremiumActivity.this, "android.test.subscribe");
    }

    @Override
    public void onBackPressed() {
        startActivity(MainMenuActivity.startActivityFromGetPremium(GetPremiumActivity.this));
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Toasty.success(GetPremiumActivity.this, "You have successfully subscribed", Toasty.LENGTH_LONG).show();

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toasty.error(GetPremiumActivity.this, "Error", Toasty.LENGTH_LONG).show();
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
}
