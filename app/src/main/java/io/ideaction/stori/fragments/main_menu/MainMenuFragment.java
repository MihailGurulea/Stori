package io.ideaction.stori.fragments.main_menu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.activities.MainMenuActivity;
import io.ideaction.stori.adapters.StoriAdaper;
import io.ideaction.stori.db.Category;
import io.ideaction.stori.db.Stori;
import io.ideaction.stori.models.UserModel;
import io.ideaction.stori.utils.CustomChangeLanguageDialog;
import io.ideaction.stori.utils.Languages;
import io.ideaction.stori.utils.StoriType;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenuFragment extends Fragment implements CustomChangeLanguageDialog.DialogLanguageListener, StoriAdaper.StoriAdapterListener {

    private static final String TAG = "MainMenuFragment";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_next_cat)
    LinearLayout mLLNextCat;
    @BindView(R.id.ll_popular_stories_cat)
    LinearLayout mLLPopularStories;
    @BindView(R.id.ll_categories_cat)
    LinearLayout mLLCategories;
    @BindView(R.id.ll_favorites_cat)
    LinearLayout mLLFavorites;
    @BindView(R.id.tv_next)
    TextView mTVNext;
    @BindView(R.id.tv_popular_stories)
    TextView mTVPopularStories;
    @BindView(R.id.tv_categories)
    TextView mTVCategories;
    @BindView(R.id.tv_favorites)
    TextView mTVFavorites;
    @BindView(R.id.tv_no_content)
    TextView mTVNoContent;
    @BindView(R.id.iv_next_dot)
    ImageView mIVNextDot;
    @BindView(R.id.iv_popular_stories_dot)
    ImageView mIVPopularStoriesDot;
    @BindView(R.id.iv_categories_dot)
    ImageView mIVCategoriesDot;
    @BindView(R.id.iv_favorites_dot)
    ImageView mIVFavoritesDot;
    @BindView(R.id.iv_language)
    ImageView mIVLanguage;
    @BindView(R.id.iv_suggested)
    ImageView mIVSuggested;
    @BindView(R.id.btn_suggested)
    Button mBtnSuggested;

    private MainMenuActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;
    private Realm mRealm;
    private StoriAdaper mStoriAdaper;
    private ArrayList<Stori> mStoris;
    private RealmResults<Category> mCategories;
    private ArrayList<String> mCategoriesTitle;
    private GridLayoutManager mGridLayoutManager;
    private Stori mStoriSuggested;
    private Call<UserModel> mCall;

    private CustomChangeLanguageDialog mCustomChangeLanguageDialog;

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        initFragment();
        initRecyclerView();

        return mView;
    }

    private void initFragment() {
        mActivity = (MainMenuActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, mView);

        mIVLanguage.setImageResource(Languages.values()[StoriApplication.getInstance().getLangToLearn()].getRes());

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("stori.realm").build();
        mRealm = Realm.getInstance(configuration);

        int countStori = mRealm.where(Stori.class).findAll().size();

        if (countStori > 0) {
            mStoriSuggested = mRealm.where(Stori.class).findAll().get(new Random().nextInt(countStori - 1));

            if (mStoriSuggested != null) {
                if (mStoriSuggested.getImage() != null && mStoriSuggested.getImage().length() > 0) {
                    Picasso.get().load(mStoriSuggested.getImage()).resize(1280, 720).into(mIVSuggested);
                }
                mBtnSuggested.setText(mStoriSuggested.getName());
                mBtnSuggested.setOnClickListener(v -> mActivity.onClickStori(mStoriSuggested.getId()));
            }
        }

        mCategories = mRealm.where(Category.class).distinct("name").findAll();
        mCategoriesTitle = new ArrayList<>();

        for (Category category : mCategories) {
            mCategoriesTitle.add(category.getName());
        }
    }

    private void initRecyclerView() {
        mStoriAdaper = new StoriAdaper(null, this);
        mGridLayoutManager = new GridLayoutManager(mActivity, 2, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mStoriAdaper);
    }

    @OnClick({R.id.ll_next_cat, R.id.ll_popular_stories_cat, R.id.ll_favorites_cat, R.id.ll_categories_cat})
    void onClickCategories(LinearLayout linearLayout) {
        switch (linearLayout.getId()) {
            case R.id.ll_next_cat:
                updateRecyclerView(StoriType.NEW, null);
                changeCategoryUI(0);
                break;
            case R.id.ll_popular_stories_cat:
                updateRecyclerView(StoriType.POPULAR_STORIES, null);
                changeCategoryUI(1);
                break;
            case R.id.ll_categories_cat:
                changeCategoryUI(2);
                ListPopupWindow listPopupWindow = new ListPopupWindow(mActivity);
                listPopupWindow.setAdapter(new ArrayAdapter<>(mActivity, R.layout.item_list_popup_window, mCategoriesTitle));
                listPopupWindow.setModal(true);
                listPopupWindow.setAnchorView(mLLCategories);
                listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
                listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
                listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
                    updateRecyclerView(StoriType.CATEGORIES, mCategoriesTitle.get(position));
                    listPopupWindow.dismiss();
                });
                listPopupWindow.show();
                break;
            case R.id.ll_favorites_cat:
                updateRecyclerView(StoriType.FAVORITES, null);
                changeCategoryUI(3);
                break;
        }
    }

    private void updateRecyclerView(StoriType storiType, String categoryName) {
        switch (storiType) {
            case FAVORITES:
                mStoris = new ArrayList<>();

                for (Stori stori : mRealm.where(Stori.class).findAll()) {
                    if (stori.getIsFavorite()) mStoris.add(stori);
                }

                mStoriAdaper.updateStori(mStoris);
                break;
            case CATEGORIES:
                mStoris = new ArrayList<>();

//                for (Category category : mRealm.where(Category.class).distinct("name").sort("name", Sort.ASCENDING).findAll()) {
                for (Stori stori : mRealm.where(Stori.class).equalTo("category.name", categoryName).findAll()) {
                    mStoris.add(stori);
                }
//                }

                mStoriAdaper.updateStori(mStoris);
                break;
            case POPULAR_STORIES:
                mStoris = new ArrayList<>();

                for (Stori stori : mRealm.where(Stori.class).sort("favorites", Sort.DESCENDING).findAll()) {
                    mStoris.add(stori);
                }

                mStoriAdaper.updateStori(mStoris);
                break;
            default:
                mStoris = new ArrayList<>();
                long currentMilliseconds = System.nanoTime();

                for (Stori stori : mRealm.where(Stori.class).findAll()) {
                    if (currentMilliseconds - stori.getCreatedAt().getDate().getTime() <= 604800000) {
                        mStoris.add(stori);
                    }
                }
                mStoriAdaper.updateStori(mStoris);
                break;
        }
        checkIfStoriListIsEmpty();
    }

    private void checkIfStoriListIsEmpty() {
        if (mStoris.size() > 0) {
            mTVNoContent.setVisibility(View.GONE);
        } else {
            mTVNoContent.setVisibility(View.VISIBLE);

        }
    }

    private void changeCategoryUI(int position) {
        mTVNext.setTextColor(Color.parseColor("#C9C9C9"));
        mIVNextDot.setVisibility(View.INVISIBLE);
        mTVPopularStories.setTextColor(Color.parseColor("#C9C9C9"));
        mIVPopularStoriesDot.setVisibility(View.INVISIBLE);
        mTVCategories.setTextColor(Color.parseColor("#C9C9C9"));
        mIVCategoriesDot.setVisibility(View.INVISIBLE);
        mTVFavorites.setTextColor(Color.parseColor("#C9C9C9"));
        mIVFavoritesDot.setVisibility(View.INVISIBLE);

        switch (position) {
            case 0:
                mTVNext.setTextColor(Color.parseColor("#000000"));
                mIVNextDot.setVisibility(View.VISIBLE);
                break;
            case 1:
                mTVPopularStories.setTextColor(Color.parseColor("#000000"));
                mIVPopularStoriesDot.setVisibility(View.VISIBLE);
                break;
            case 2:
                mTVCategories.setTextColor(Color.parseColor("#000000"));
                mIVCategoriesDot.setVisibility(View.VISIBLE);
                break;
            default:
                mTVFavorites.setTextColor(Color.parseColor("#000000"));
                mIVFavoritesDot.setVisibility(View.VISIBLE);
                break;
        }
    }

    @OnClick(R.id.iv_language)
    void OnClickLanguage() {
        mCustomChangeLanguageDialog = new CustomChangeLanguageDialog(mActivity, this);
        mCustomChangeLanguageDialog.show();
        mIVLanguage.setVisibility(View.GONE);
    }

    public void setVisibleIVLanguage() {
        mIVLanguage.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.iv_search)
    void onClickSearch() {
        mActivity.onClickSearch();
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

        if (mActivity != null) {
            mActivity = null;
        }

        if (mRealm != null) {
            mRealm = null;
        }

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
    public void onClickClose() {
        mCustomChangeLanguageDialog.dismiss();
        setVisibleIVLanguage();
    }

    @Override
    public void onClickItem(int position) {
        mActivity.showProgressBar();
        mCall = StoriApplication.apiInterface().updateLangToLearn(
                "Bearer " + StoriApplication.getInstance().getToken(),
                position + 1
        );

        mCall.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                mActivity.hideProgressBar();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        StoriApplication.getInstance().setUserCredentials(response.body());
                    } else {
                        Toasty.error(mActivity, "Response is null", Toasty.LENGTH_LONG).show();
                    }
                } else {
                    Toasty.error(mActivity, "Response isn't successful", Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                mActivity.hideProgressBar();
                Log.e(TAG, t.getMessage(), t);
                Toasty.error(mActivity, "Request error", Toasty.LENGTH_LONG).show();
            }
        });

        StoriApplication.getInstance().setLangToLearn(position + 1);
        mCustomChangeLanguageDialog.dismiss();
        updateFlag();
        setVisibleIVLanguage();
    }

    public void updateFlag() {
        mIVLanguage.setImageResource(Languages.values()[StoriApplication.getInstance().getLangToLearn()].getRes());
    }

    @Override
    public void onClickStori(int position) {
        mActivity.onClickStori(mStoris.get(position).getId());
    }

    @Override
    public void onResume() {
        super.onResume();
        onClickCategories(mView.findViewById(R.id.ll_next_cat));
    }
}
