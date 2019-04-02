package io.ideaction.stori.fragments.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.ideaction.stori.R;
import io.ideaction.stori.activities.TutorialActivity;

public class FirstTutorialFragment extends Fragment {

    private TutorialActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;

    public static FirstTutorialFragment newInstance() {
        return new FirstTutorialFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_first_tutorial, container, false);

        initFragment();
        return mView;

    }

    private void initFragment() {
        mActivity = (TutorialActivity) getActivity();
        mUnbinder = ButterKnife.bind(this, mView);
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
}