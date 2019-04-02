package io.ideaction.stori.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.db.Stori;
import io.ideaction.stori.fragments.start_stori.ListenStoriFragment;
import io.ideaction.stori.fragments.start_stori.ReadStoriFragment;
import io.ideaction.stori.fragments.start_stori.StartStoriFragment;
import io.ideaction.stori.utils.FragmentEntranceSide;
import io.ideaction.stori.utils.Languages;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartStoriActivity extends AppCompatActivity {

    private static final String TAG = "StartStoriActivity";
    private static final String STORI_ID = "io.ideaction.stori.activities.storiId";
    public final static int BACK = 0;
    public final static int CLOSE = 1;


    @BindView(R.id.btn_add_favorite)
    Button mBTNAddFavorite;
    @BindView(R.id.btn_back)
    ImageView mBTNBack;

    @BindView(R.id.cover_image)
    ImageView mIVCover;

    private Fragment mCurrentFragment;
    private Realm mRealm;
    private int mStoriId;
    private boolean isFavorite;
    private Stori mStoriSuggested;
    private Call<Void> mCall;

    public static Intent startActivity(Context context, int storiId) {
        Intent intent = new Intent(context, StartStoriActivity.class);

        intent.putExtra(STORI_ID, storiId);

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

        setContentView(R.layout.activity_start_stori);

        ButterKnife.bind(this);

        mStoriId = getIntent().getIntExtra(STORI_ID, 0);

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("stori.realm").build();
        mRealm = Realm.getInstance(configuration);

        mStoriSuggested = mRealm.where(Stori.class).equalTo("id", mStoriId).findFirst();

        if (mStoriSuggested != null) {
            Picasso.get().load(mStoriSuggested.getImage()).resize(1280, 720).into(mIVCover);
        }

        if (mStoriSuggested.getIsFavorite()) {
            isFavorite = true;
            mBTNAddFavorite.setBackgroundResource(R.drawable.ic_full_heart);
        } else {
            isFavorite = false;
            mBTNAddFavorite.setBackgroundResource(R.drawable.ic_stori_like_btn);
        }

        onClickStartStoriFragment();
    }

    @OnClick(R.id.btn_back)
    void onClickBackButton() {
        if (mCurrentFragment instanceof ReadStoriFragment) {
            ReadStoriFragment readStoriFragment = (ReadStoriFragment) mCurrentFragment;
            if(readStoriFragment.getChapterCounter() == readStoriFragment.getTotalNumberOfChapters()){
                readStoriFragment.changeNextBTN();
            }
            switch (readStoriFragment.getChapterCounter()) {
                case 1:
                    onBackPressed();
                    break;
                case 2:
                    readStoriFragment.decreaseChapterCounter();
                    changeBackButtonImage(CLOSE);
                    readStoriFragment.changeWordsByChapter();
                    break;
                default:
                    readStoriFragment.decreaseChapterCounter();
                    readStoriFragment.changeWordsByChapter();
                    break;
            }

        } else {
            onBackPressed();
        }
    }

    @OnClick(R.id.btn_add_favorite)
    void onClickAddFavoriteGoToVocabulary(){
        if (!isFavorite) {
            isFavorite = true;
            mBTNAddFavorite.setBackgroundResource(R.drawable.ic_full_heart);
            mRealm.executeTransactionAsync(r -> {
                Stori stori = r.where(Stori.class).equalTo("id", mStoriId).findFirst();

                if (stori != null) {
                    stori.setIsFavorite(true);
                }
            });
        } else {
            isFavorite = false;
            mBTNAddFavorite.setBackgroundResource(R.drawable.ic_stori_like_btn);
            mRealm.executeTransactionAsync(r -> {
                Stori stori = r.where(Stori.class).equalTo("id", mStoriId).findFirst();

                if (stori != null) {
                    stori.setIsFavorite(false);
                }
            });
        }

        mCall = StoriApplication.apiInterface().likeStori(
                "Bearer " + StoriApplication.getInstance().getToken(),
                mStoriId
        );

        mCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toasty.success(StartStoriActivity.this, "Response is successful", Toasty.LENGTH_LONG).show();
                } else {
                    Toasty.error(StartStoriActivity.this, "Response isn't successful", Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
                Toasty.error(StartStoriActivity.this, "Request error", Toasty.LENGTH_LONG).show();
            }
        });
    }

    public void changeBackButtonImage(int type){
        if(type == BACK){
            mBTNBack.setImageResource(R.drawable.ic_read_stori_arrow);
        } else {
            mBTNBack.setImageResource(R.drawable.ic_stori_back_btn);
        }
    }

    public void onClickStartStoriFragment() {
        setFragment(StartStoriFragment.newInstance(mStoriId), FragmentEntranceSide.BOTTOM);
    }

    public void onClickListenStoriFragment() {
        setFragment(ListenStoriFragment.newInstance(mStoriId), FragmentEntranceSide.BOTTOM);
    }

    public void onClickReadStoriFragment() {
        setFragment(ReadStoriFragment.newInstance(mStoriId), FragmentEntranceSide.BOTTOM);
    }

    public void setFragment(Fragment fragment, FragmentEntranceSide fragmentEntranceSide) {
        mCurrentFragment = fragment;

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
        fragmentTransaction.replace(R.id.activity_container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
}
