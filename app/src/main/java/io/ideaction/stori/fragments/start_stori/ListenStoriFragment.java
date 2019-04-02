package io.ideaction.stori.fragments.start_stori;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import io.ideaction.stori.R;
import io.ideaction.stori.activities.StartStoriActivity;
import io.ideaction.stori.db.Stori;
import io.ideaction.stori.utils.FragmentEntranceSide;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ListenStoriFragment extends Fragment {

    private static final String TAG = "ListenStoriFragment";
    private static final String STORI_ID = "io.ideaction.stori.fragments.start_stori.storiId";

    @BindView(R.id.stori_name)
    TextView mTVStoriName;
    @BindView(R.id.seek_bar)
    SeekBar mSeekBar;
    @BindView(R.id.play_pause_btn)
    Button mBTNPlayPause;
    @BindView(R.id.current_time)
    TextView mTVCurrentSoundTrackTime;
    @BindView(R.id.track_length)
    TextView mTVSoundTrackLength;
    @BindView(R.id.plus_5_seconds)
    TextView mTVAddSubtract5Seconds;
    @BindView(R.id.progress_circular)
    ProgressBar mProgressBar;

    private StartStoriActivity mActivity;
    private View mView;
    private Unbinder mUnbinder;
    private Realm mRealm;

    private Handler myHandler = new Handler();
    private MediaPlayer mMediaPlayer;
    private String mSelectedStoriURL;
    private double startTime = 0;
    private double finalTime = 0;
    private boolean isSeekBarTouched = false;
    private boolean isPlaying = false;
    private int mStoriId;
    private Stori mStori;

    private Runnable mUpdateSongTime = new Runnable() {
        public void run() {
            if (!isSeekBarTouched()) {
                startTime = mMediaPlayer.getCurrentPosition();
                mTVCurrentSoundTrackTime.setText(String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                );
                mSeekBar.setProgress((int) startTime);
            }
            myHandler.postDelayed(this, 100);
        }
    };

    public static ListenStoriFragment newInstance(int storiId) {
        ListenStoriFragment listenStoriFragment = new ListenStoriFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(STORI_ID, storiId);
        listenStoriFragment.setArguments(bundle);

        return listenStoriFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStoriId = getArguments().getInt(STORI_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_listen_stori, container, false);

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
        mTVStoriName.setText(mStori.getTranslations().getName().getNameInNativeLanguages());
        mSelectedStoriURL = mStori.getAudios().getAudioInLanguagesToLearn();
        mTVAddSubtract5Seconds.setAlpha(0);
        mBTNPlayPause.setEnabled(false);

        initSeekBar();
        initMediaPlayer();
    }

    private void initSeekBar() {
        mSeekBar.setClickable(false);
        mSeekBar.setProgressBackgroundTintList(ColorStateList.valueOf(0xFFD8D8D8));
        mSeekBar.setProgressTintList(ColorStateList.valueOf(0xFFFFC86B));
        mSeekBar.getThumb().setColorFilter(0xFFFFC86B, PorterDuff.Mode.SRC);

        LayerDrawable progressBarDrawable = (LayerDrawable) mSeekBar.getProgressDrawable();
        Drawable backgroundDrawable = progressBarDrawable.getDrawable(0);
        backgroundDrawable.setColorFilter(ContextCompat.getColor(mActivity, R.color.F1F1F2_grey), PorterDuff.Mode.SRC_IN);
    }

    private void initMediaPlayer() {
        if (mSelectedStoriURL != null && mSelectedStoriURL.length() > 0) {
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(mSelectedStoriURL);
            } catch (IOException e) {
                Toasty.error(mActivity, "Oops, something went wrong...", Toast.LENGTH_SHORT).show();
            }
            mMediaPlayer.setAudioStreamType(AudioAttributes.CONTENT_TYPE_MUSIC);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(mp -> {
                hideProgressBar();

                if (mBTNPlayPause != null) {
                    mBTNPlayPause.setEnabled(true);
                    mBTNPlayPause.callOnClick();
                }
            });

            mMediaPlayer.setOnCompletionListener(mp -> {
                mBTNPlayPause.setText(R.string.play);
                isPlaying = false;
            });

            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int mProgress;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mTVCurrentSoundTrackTime.setText(String.format(Locale.getDefault(), "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes((long) progress),
                            TimeUnit.MILLISECONDS.toSeconds((long) progress)));
                    mProgress = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    setSeekBarTouched(true);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    setSeekBarTouched(false);
                    mMediaPlayer.seekTo(mProgress);
                }
            });
        }
    }

    @OnClick(R.id.play_pause_btn)
    void onClickPlayPause() {
        if (!isPlaying) {
            mMediaPlayer.start();
            finalTime = mMediaPlayer.getDuration();
            startTime = mMediaPlayer.getCurrentPosition();
            mSeekBar.setMax((int) finalTime);

            mTVSoundTrackLength.setText(String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
            );

            mTVCurrentSoundTrackTime.setText(String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
            );

            mSeekBar.setProgress((int) startTime);
            myHandler.postDelayed(mUpdateSongTime, 100);
            mBTNPlayPause.setText(getString(R.string.pause));
            isPlaying = true;
        } else {
            mMediaPlayer.pause();
            mBTNPlayPause.setText(getString(R.string.resume));
            isPlaying = false;
        }
    }

    @OnClick(R.id.finish_btn)
    void onClickFinishBTN() {
        mActivity.setFragment(StartStoriFragment.newInstance(mStoriId), FragmentEntranceSide.BOTTOM);
    }

    @OnClick(R.id.btn_forward)
    void onClickPlus5Seconds() {
        int temp = (int) startTime;
        int FORWARD_TIME = 5000;

        if ((temp + FORWARD_TIME) <= finalTime) {
            startTime = startTime + FORWARD_TIME;
            mMediaPlayer.seekTo((int) startTime);
            mTVAddSubtract5Seconds.setText(getString(R.string.plus_5_sec));
            mTVAddSubtract5Seconds.animate().alpha(1).setDuration(700);
            new CountDownTimer(1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    mTVAddSubtract5Seconds.animate().alpha(0).setDuration(700);
                }
            }.start();
        } else {
            Toasty.error(mActivity, "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_backward)
    void onClickSubtract5Seconds() {
        int temp = (int) startTime;
        int BACKWARD_TIME = 5000;

        if ((temp - BACKWARD_TIME) > 0) {
            startTime = startTime - BACKWARD_TIME;
            mMediaPlayer.seekTo((int) startTime);
            mTVAddSubtract5Seconds.setText(getString(R.string.minus_5_sec));
            mTVAddSubtract5Seconds.animate().alpha(1).setDuration(700);
            new CountDownTimer(1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if (mTVAddSubtract5Seconds != null)
                        mTVAddSubtract5Seconds.animate().alpha(0).setDuration(700);
                }

            }.start();

        } else {
            Toasty.error(mActivity, "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
        }
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

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        myHandler.removeCallbacks(mUpdateSongTime);
    }

    public boolean isSeekBarTouched() {
        return isSeekBarTouched;
    }

    public void setSeekBarTouched(boolean seekBarTouched) {
        isSeekBarTouched = seekBarTouched;
    }

    public void hideProgressBar() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
