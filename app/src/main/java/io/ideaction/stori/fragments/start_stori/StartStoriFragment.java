package io.ideaction.stori.fragments.start_stori;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.ideaction.stori.R;
import io.ideaction.stori.activities.StartStoriActivity;
import io.ideaction.stori.db.Stori;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class StartStoriFragment extends Fragment {

    private static final String TAG = "StartStoriFragment";
    private static final String STORI_ID = "io.ideaction.stori.fragments.start_stori.storiId";

    @BindView(R.id.stori_name)
    TextView mTVStoriName;

    private StartStoriActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;
    private Realm mRealm;

    private int mStoriId;
    private Stori mStori;

    public static StartStoriFragment newInstance(int storiId) {
        StartStoriFragment startStoriFragment = new StartStoriFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(STORI_ID, storiId);
        startStoriFragment.setArguments(bundle);

        return startStoriFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStoriId = getArguments().getInt(STORI_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_start_stori, container, false);

        initFragment();
        initViews();

        return mView;
    }

    private void initFragment() {
        mActivity = (StartStoriActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, mView);

        RealmConfiguration configuration = new RealmConfiguration.Builder().name("stori.realm").build();
        mRealm = Realm.getInstance(configuration);

        mStori = mRealm.where(Stori.class).equalTo("id", mStoriId).findFirst();
    }

    private void initViews() {
        if (mStori != null) {
            mTVStoriName.setText(mStori.getName());
        }
    }

    @OnClick(R.id.listen_btn)
    void onClickListenBTN(){
        mActivity.onClickListenStoriFragment();
    }

    @OnClick(R.id.read_btn)
    void onClickReadBTN(){
        mActivity.onClickReadStoriFragment();
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
    }
}

