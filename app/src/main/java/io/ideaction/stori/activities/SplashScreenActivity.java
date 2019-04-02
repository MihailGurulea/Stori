package io.ideaction.stori.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.network.GetStorisAsync;
import io.ideaction.stori.utils.Languages;

public class SplashScreenActivity extends AppCompatActivity implements GetStorisAsync.GetStorisAsyncListener {

    private static final String TAG = "SplashScreenActivity";

    private static final Locale[] mLocales = {
            Locale.GERMANY,
            new Locale(Languages.SPANISH.getCodeLowerCase(), Languages.SPANISH.getCodeUpperCase()),
            Locale.FRANCE,
            Locale.US,
            new Locale(Languages.RUSSIAN.getCodeLowerCase(), Languages.RUSSIAN.getCodeUpperCase()),
            Locale.ITALY

    };
    private TextToSpeech mSpeech;
    private int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale localeNat = new Locale(Languages.values()[StoriApplication.getInstance().getNativeLang()].getCodeLowerCase());
        Locale.setDefault(localeNat);
        Configuration config = new Configuration();
        config.locale = localeNat;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_splash_screen);

        mSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                for (Locale locale : mLocales) {
                    result = mSpeech.setLanguage(locale);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not available, attempting download");
                        Intent installIntent = new Intent();
                        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                        startActivity(installIntent);
                    }
                }
            } else {
                Log.e("TTS", "Initialization Failed!");
            }
        }, "com.google.android.tts");

        if (TextUtils.isEmpty(StoriApplication.getInstance().getToken()) ||
                StoriApplication.getInstance().getNativeLang() == 0 ||
                StoriApplication.getInstance().getLangToLearn() == 0) {
            new Handler().postDelayed(this::startActivity, 1000);
        } else {
            new GetStorisAsync(this).execute();
        }
    }

    private void startActivity() {
        if (StoriApplication.getInstance().getTutorial()) {
            startActivity(LogInActivity.startActivity(SplashScreenActivity.this));
        } else {
            startActivity(TutorialActivity.startActivity(SplashScreenActivity.this));
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e != null) {
            Toast.makeText(SplashScreenActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.getMessage(), e);
        } else {
            Toasty.error(SplashScreenActivity.this, "Error", Toasty.LENGTH_LONG).show();
        }

        startActivity(LogInActivity.startActivity(SplashScreenActivity.this));
    }

    @Override
    public void onSuccess() {
        startActivity(MainMenuActivity.startActivity(SplashScreenActivity.this));
    }
}
