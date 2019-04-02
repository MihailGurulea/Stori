package io.ideaction.stori.network;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.ideaction.stori.StoriApplication;
import io.ideaction.stori.db.Stori;
import io.ideaction.stori.db.Vocabulary;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetStorisAsync extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "GetStorisAsync";

    private Realm mRealm;
    private GetStorisAsyncListener mGetStorisAsyncListener;
    private Call<List<Stori>> mCall;
    private Call<Vocabulary> mCallGetWords;
    private RealmConfiguration mRealmConfiguration;

    public GetStorisAsync(GetStorisAsyncListener getStorisAsyncListener) {
        mGetStorisAsyncListener = getStorisAsyncListener;
        mRealmConfiguration = new RealmConfiguration.Builder().name("stori.realm").build();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        mCall = StoriApplication.apiInterface().getStories("Bearer " + StoriApplication.getInstance().getToken());

        mCall.enqueue(new Callback<List<Stori>>() {
            @Override
            public void onResponse(Call<List<Stori>> call, Response<List<Stori>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        for (Stori stori : response.body()) {
                            if (stori.getImage() != null && stori.getImage().length() > 0) {
                                Picasso.get().load(stori.getImage()).resize(1280, 720).fetch();
                            }
                        }
                        mRealm = getRealmInstance();
                        mRealm.executeTransaction(r -> {
                            r.deleteAll();
                            r.insert(response.body());
                        });

                        mCallGetWords = StoriApplication.apiInterface().getWords("Bearer " + StoriApplication.getInstance().getToken());
                        mCallGetWords.enqueue(new Callback<Vocabulary>() {
                            @Override
                            public void onResponse(Call<Vocabulary> call, Response<Vocabulary> response) {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        mRealm = getRealmInstance();
                                        mRealm.executeTransaction(r -> r.insert(response.body()));
                                        mGetStorisAsyncListener.onSuccess();
                                    } else {
                                        onError(null);
                                    }
                                } else {
                                    onError(null);
                                }
                            }

                            @Override
                            public void onFailure(Call<Vocabulary> call, Throwable t) {
                                onError(t);
                            }
                        });
                    } else {
                        onError(null);
                    }
                } else {
                    onError(null);
                }
            }

            @Override
            public void onFailure(Call<List<Stori>> call, Throwable t) {
                onError(t);
            }
        });
        return null;
    }

    private Realm getRealmInstance() {
        try {
            Log.i(TAG, "get Instance");
            mRealm = Realm.getInstance(mRealmConfiguration);
        } catch (RealmMigrationNeededException r) {
            Log.i(TAG, "delete and get Instance");
            Realm.deleteRealm(mRealmConfiguration);
            mRealm = Realm.getInstance(mRealmConfiguration);
        }

        return mRealm;
    }

    private void onError(Throwable t) {
        StoriApplication.getInstance().setToken("");
        mGetStorisAsyncListener.onError(t);
    }

    public interface GetStorisAsyncListener {
        void onError(Throwable e);

        void onSuccess();
    }
}
