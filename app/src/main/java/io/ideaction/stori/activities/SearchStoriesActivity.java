package io.ideaction.stori.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.adapters.SearchGridAdapter;
import io.ideaction.stori.db.Stori;
import io.ideaction.stori.utils.Languages;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class SearchStoriesActivity extends AppCompatActivity implements SearchGridAdapter.SearchGridAdapterListener {

    private static final String TAG = "SearchStoriesActivity";

    @BindView(R.id.progress_circular)
    ProgressBar mProgressBar;
    @BindView(R.id.search_icon)
    ImageView mIVSearchIcon;
    @BindView(R.id.suggested_stori_image)
    ImageView mIVSuggestedStoriImage;
    @BindView(R.id.suggested_stori_image_opacity)
    ImageView mIVSuggestedStoriImageOpacity;
    @BindView(R.id.tv_suggested_stori_text)
    TextView mTVSuggestedStoriText;
    @BindView(R.id.tv_search_mirror)
    TextView mTVSearchMirror;
    @BindView(R.id.et_search_field)
    EditText mETSearchField;
    @BindView(R.id.grid_of_stories)
    GridView mGVStoriesGrid;
    @BindView(R.id.tv_no_results)
    TextView mTVNoResults;

    private SearchGridAdapter mSearchGridAdapter;
    private Realm mRealm;

    private boolean ignore = false;
    private String newText;
    private Stori mStoriSuggested;
    RealmResults<Stori> mStoris;

    public static Intent startActivity(Context context) {
        return new Intent(context, SearchStoriesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = new Locale(Languages.values()[StoriApplication.getInstance().getNativeLang()].getCodeLowerCase());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_search_stories);

        ButterKnife.bind(this);

        initViews();
        initListeners();
    }

//    @OnClick(R.id.suggested_stori_image)
//    void onClickSuggestedImage(){
//            startActivity(StartStoriActivity.startActivity(this));
//    }

    public void initViews() {
        RealmConfiguration configuration = new RealmConfiguration.Builder().name("stori.realm").build();
        mRealm = Realm.getInstance(configuration);

        int countStori = mRealm.where(Stori.class).findAll().size();

        if (countStori > 0) {
            mStoriSuggested = mRealm.where(Stori.class).findAll().get(new Random().nextInt(countStori - 1));

            if (mStoriSuggested != null) {
                mIVSuggestedStoriImage.setVisibility(View.VISIBLE);
                mIVSuggestedStoriImageOpacity.setVisibility(View.VISIBLE);
                mTVSuggestedStoriText.setVisibility(View.VISIBLE);
                if (mStoriSuggested.getImage() != null && mStoriSuggested.getImage().length() > 0) {
                    Picasso.get().load(mStoriSuggested.getImage()).resize(1280, 720).into(mIVSuggestedStoriImage);
                }
                mTVSuggestedStoriText.setText(mStoriSuggested.getTranslations().getName().getNameInNativeLanguages());
                mIVSuggestedStoriImage.setOnClickListener(v -> startActivity(StartStoriActivity.startActivity(SearchStoriesActivity.this, mStoriSuggested.getId())));
            }
        }

        mSearchGridAdapter = new SearchGridAdapter(SearchStoriesActivity.this, this);
        mGVStoriesGrid.setAdapter(mSearchGridAdapter);
    }

    private void initListeners() {

        mETSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newText = s.toString();

                if (newText.length() > 0) {
                    mIVSearchIcon.setImageResource(R.drawable.ic_search_icon_dark_grey);
                } else {
                    mIVSearchIcon.setImageResource(R.drawable.ic_search_icon_light_grey);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ignore)
                    return;

                if (!(newText.length() == 0)) {
                    startUpdate();
                    mTVSearchMirror.setText(newText);
                    mStoris = mRealm.where(Stori.class).contains("name", String.valueOf(s), Case.INSENSITIVE).findAll();
                    if (mStoris != null && mStoris.size() > 0) {
                        mSearchGridAdapter.updateStoris(mStoris);
                        mGVStoriesGrid.setVisibility(View.VISIBLE);
                        mTVNoResults.setVisibility(View.GONE);
                        mIVSuggestedStoriImage.setVisibility(View.GONE);
                        mIVSuggestedStoriImageOpacity.setVisibility(View.GONE);
                        mTVSuggestedStoriText.setVisibility(View.GONE);
                    } else {
                        mStoris = null;
                        mSearchGridAdapter.updateStoris(mStoris);
                        mGVStoriesGrid.setVisibility(View.GONE);
                        mTVNoResults.setVisibility(View.VISIBLE);
                        mIVSuggestedStoriImage.setVisibility(View.GONE);
                        mIVSuggestedStoriImageOpacity.setVisibility(View.GONE);
                        mTVSuggestedStoriText.setVisibility(View.GONE);
                    }
                    stopUpdate();
                } else {
                    startUpdate();
                    mTVSearchMirror.setText(R.string.stori_suggestion);
                    mGVStoriesGrid.setVisibility(View.GONE);
                    mTVNoResults.setVisibility(View.GONE);
                    mIVSuggestedStoriImage.setVisibility(View.VISIBLE);
                    mIVSuggestedStoriImageOpacity.setVisibility(View.VISIBLE);
                    mTVSuggestedStoriText.setVisibility(View.VISIBLE);
                    stopUpdate();
                }
            }
        });

    }

    @OnClick(R.id.cancel_search)
    void onClickBackBTN() {
        onBackPressed();
    }

    private void startUpdate() {
        ignore = true;
    }

    private void stopUpdate() {
        ignore = false;
    }

    public void showProgresBar() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.GONE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgresBar() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClickStoriItem(int position) {
        if (mStoris != null && mStoris.size() > position) {
            startActivity(StartStoriActivity.startActivity(SearchStoriesActivity.this, mStoris.get(position).getId()));
        }
    }
}


