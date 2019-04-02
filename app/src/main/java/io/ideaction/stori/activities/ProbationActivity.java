package io.ideaction.stori.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.ideaction.stori.R;
import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.adapters.ExamAdapter;
import io.ideaction.stori.fragments.probation.ExamFragment;
import io.ideaction.stori.fragments.probation.ExamResultFragment;
import io.ideaction.stori.utils.FragmentEntranceSide;
import io.ideaction.stori.utils.Languages;

public class ProbationActivity extends AppCompatActivity {

    private static final String TAG = "ProbationActivity";

    @BindView(R.id.progress_circular)
    ProgressBar mProgressBar;

    public static Intent startActivity(Context context) {
        return new Intent(context, ProbationActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = new Locale(Languages.values()[StoriApplication.getInstance().getNativeLang()].getCodeLowerCase());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_probation);
        setFragment(ExamFragment.newInstance(), FragmentEntranceSide.RIGHT);
        ButterKnife.bind(this);
    }

    public void setFragment(Fragment fragment, FragmentEntranceSide fragmentEntranceSide) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (fragmentEntranceSide) {
            case RIGHT:
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left);
                break;
            case LEFT:
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right);
                break;
        }
        fragmentTransaction.replace(R.id.probation_container, fragment);
        fragmentTransaction.commit();
    }

    public void onClickFinish(int correctAnswers, int nrOfQuestions){
        setFragment(ExamResultFragment.newInstance(correctAnswers,nrOfQuestions), FragmentEntranceSide.STANDARD);
    }

    public void onClickOnceAgain(){
        setFragment(ExamFragment.newInstance(),FragmentEntranceSide.STANDARD);
    }
}
