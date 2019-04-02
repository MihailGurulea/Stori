package io.ideaction.stori;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.grandcentrix.tray.AppPreferences;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import io.ideaction.stori.models.UserModel;
import io.ideaction.stori.network.StoriAPI;
import io.ideaction.stori.utils.Languages;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoriApplication extends Application {

    private static final String TUTORIAL = "io.ideaction.stori.tutorial";
    private static final String USER_NAME = "io.ideaction.stori.userName";
    private static final String USER_EMAIL = "io.ideaction.stori.userEmail";
    private static final String USER_PASSWORD = "io.ideaction.stori.userPassword";
    private static final String NATIVE_LANG = "io.ideaction.stori.nativeLanguage";
    private static final String LANG_TO_LEARN = "io.ideaction.stori.languageToLearn";
    private static final String TOKEN = "io.ideaction.stori.token";
    private static final String BASE_URL = "https://www.readstori.com/";

    private static StoriApplication sStoriApplication;
    private static AppPreferences sAppPreferences;
    private static Retrofit sRetrofit;

    private static Typeface CERA_PRO_LIGHT;
    private static Typeface CERA_PRO_BOLD;
    private static Typeface CERA_PRO_REGULAR;
    private static Typeface CERA_PRO_MEDIUM;
    private static Typeface SF_PRO_TEXT_MEDIUM;
    private static Typeface SF_PRO_TEXT_REGULAR;
    private static Typeface SF_PRO_TEXT_SEMIBOLD;
    private static Typeface SWEET_PEA;
    private static Typeface AVENIR_BOOK;

    public static StoriApplication getInstance() {
        return sStoriApplication;
    }

    public static StoriAPI apiInterface() {
        return sRetrofit.create(StoriAPI.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().name("stori.realm").build();
        Realm.setDefaultConfiguration(configuration);

        sStoriApplication = this;
        sAppPreferences = new AppPreferences(this);

        CERA_PRO_LIGHT = Typeface.createFromAsset(getAssets(), "fonts/cera_pro_light.ttf");
        CERA_PRO_BOLD = Typeface.createFromAsset(getAssets(), "fonts/cera_pro_bold.ttf");
        CERA_PRO_REGULAR = Typeface.createFromAsset(getAssets(), "fonts/cera_pro_regular.ttf");
        CERA_PRO_MEDIUM = Typeface.createFromAsset(getAssets(), "fonts/cera_pro_medium.ttf");
        SF_PRO_TEXT_MEDIUM = Typeface.createFromAsset(getAssets(), "fonts/sf_pro_text_medium.ttf");
        SF_PRO_TEXT_REGULAR = Typeface.createFromAsset(getAssets(), "fonts/sf_pro_text_regular.ttf");
        SF_PRO_TEXT_SEMIBOLD = Typeface.createFromAsset(getAssets(), "fonts/sf_pro_text_semibold.ttf");
        SWEET_PEA = Typeface.createFromAsset(getAssets(), "fonts/sweet_pea.ttf");
        AVENIR_BOOK = Typeface.createFromAsset(getAssets(), "fonts/avenir_book.ttf");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        sRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public Typeface getCeraProLight() {
        return CERA_PRO_LIGHT;
    }

    public Typeface getCeraProBold() {
        return CERA_PRO_BOLD;
    }

    public Typeface getCeraProRegular() {
        return CERA_PRO_REGULAR;
    }

    public Typeface getSfProTextMedium() {
        return SF_PRO_TEXT_MEDIUM;
    }

    public Typeface getSfProTextRegular() {
        return SF_PRO_TEXT_REGULAR;
    }

    public Typeface getSfProTextSemibold() {
        return SF_PRO_TEXT_SEMIBOLD;
    }

    public Typeface getSweetPea() {
        return SWEET_PEA;
    }

    public Typeface getAvenirBook() {
        return AVENIR_BOOK;
    }

    public Typeface getCeraProMedium() {
        return CERA_PRO_MEDIUM;
    }

    public boolean getTutorial() {
        return sAppPreferences.getBoolean(TUTORIAL, false);
    }

    public void setTutorial(boolean tutorial) {
        sAppPreferences.put(TUTORIAL, tutorial);
    }

    public String getUserName() {
        return sAppPreferences.getString(USER_NAME, "");
    }

    public void setUserName(String userName) {
        sAppPreferences.put(USER_NAME, userName);
    }

    public String getUserEmail() {
        return sAppPreferences.getString(USER_EMAIL, "");
    }

    public void setUserEmail(String userEmail) {
        sAppPreferences.put(USER_EMAIL, userEmail);
    }

    public String getUserPassword() {
        return sAppPreferences.getString(USER_PASSWORD, "");
    }

    public void setUserPassword(String userPassword) {
        sAppPreferences.put(USER_PASSWORD, userPassword);
    }

    public String getToken() {
        return sAppPreferences.getString(TOKEN, "");
    }

    public void setToken(String token) {
        sAppPreferences.put(TOKEN, token);
    }

    public int getNativeLang() {
        return sAppPreferences.getInt(NATIVE_LANG, 0);
    }

    public void setNativeLang(int nativeLang) {
        sAppPreferences.put(NATIVE_LANG, nativeLang);
    }

    public int getLangToLearn() {
        return sAppPreferences.getInt(LANG_TO_LEARN, 0);
    }

    public void setLangToLearn(int langToLearn) {
        sAppPreferences.put(LANG_TO_LEARN, langToLearn);
    }

    public UserModel getUserCredentials() {
        UserModel userModel = new UserModel();
        userModel.setName(sAppPreferences.getString(USER_NAME, ""));
        userModel.setEmail(sAppPreferences.getString(USER_EMAIL, ""));
        userModel.setPassword(sAppPreferences.getString(USER_PASSWORD, ""));
        userModel.setNative_lang(sAppPreferences.getInt(NATIVE_LANG, 0));
        userModel.setLang_to_learn(sAppPreferences.getInt(LANG_TO_LEARN, 0));

        return userModel;
    }

    public void setUserCredentials(UserModel userModel) {
        sAppPreferences.put(USER_NAME, userModel.getName());
        sAppPreferences.put(USER_EMAIL, userModel.getEmail());
        if (!TextUtils.isEmpty(userModel.getPassword())) {
            sAppPreferences.put(USER_PASSWORD, userModel.getPassword());
        }
        sAppPreferences.put(NATIVE_LANG, userModel.getNative_lang());
        sAppPreferences.put(LANG_TO_LEARN, userModel.getLang_to_learn());
    }

    public void changeNativeLanguage(Languages language) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language.getCodeLowerCase()));
        res.updateConfiguration(conf, dm);
    }
}
