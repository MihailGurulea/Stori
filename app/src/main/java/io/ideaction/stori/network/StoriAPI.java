package io.ideaction.stori.network;

import java.util.List;

import io.ideaction.stori.db.Stori;
import io.ideaction.stori.db.Vocabulary;
import io.ideaction.stori.models.ResetPasswordModel;
import io.ideaction.stori.models.UserModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface StoriAPI {

    //-------------------------------Authorisation--------------------------------------------------

    /**
     * First step registration
     * @param email - user email
     * @param name - user name
     * @param password - user password
     * @param passwordConfirmation - user repeated password
     * @return Call<RegisterModel>
     */
    @POST("api/auth/register")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<UserModel> register(
            @Field("email") String email,
            @Field("name") String name,
            @Field("password") String password,
            @Field("password_confirmation") String passwordConfirmation
    );

    /**
     * Update Languages
     * @param nativeLang - user native language
     * @param langToLearn - user language to learn
     * @return Call<UserModel>
     */
    @POST("api/V1/updateProfile")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<UserModel> updateLanguage(
            @Header("Authorization") String token,
            @Field("native_lang") int nativeLang,
            @Field("lang_to_learn") int langToLearn
    );

    /**
     * Update StoriTranslationsName
     * @param name - user name
     * @return Call<UserModel>
     */
    @POST("api/V1/updateProfile")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<UserModel> updateName(
            @Header("Authorization") String token,
            @Field("name") String name
    );

    /**
     * Forgot password first Step
     * @param email - user email
     * @return Call<ResetPasswordModel>
     */
    @POST("api/V1/password/create")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<ResetPasswordModel> forgotPasswordFirstStep(@Field("email") String email);

    /**
     * Forgot password second step
     * @param email - user email
     * @param token - secret code who user received on email
     * @param password - user new password
     * @param passConfirmation - user new repeated password
     * @return Call<ResetPasswordModel>
     */
    @POST("api/V1/password/reset")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<UserModel> forgotPasswordSecondStep(
            @Field("email") String email,
            @Field("token") String token,
            @Field("password") String password,
            @Field("password_confirmation") String passConfirmation
    );

    /**
     * Login
     * @param email - user email
     * @param password - user password
     * @return Call<UserModel>
     */
    @POST("api/auth/login")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<UserModel> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("api/social_auth")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<UserModel> socialLogin(
            @Field("email") String email,
            @Field("name") String name,
            @Field("provider") String provider,
            @Field("provider_user_id") String providerUserId,
            @Field("avatar_link") String avatarLink
    );

    @GET("api/V1/getStoris")
    @Headers("Accept: application/json")
    Call<List<Stori>> getStories(@Header("Authorization") String token);

    @POST("api/V1/updateProfile")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<UserModel> updateNativeLang(
            @Header("Authorization") String token,
            @Field("native_lang") int nativeLang
    );

    @POST("api/V1/updateProfile")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    Call<UserModel> updateLangToLearn(
            @Header("Authorization") String token,
            @Field("lang_to_learn") int langToLearn
    );

    @POST("api/V1/likeStori/{id}")
    @Headers("Accept: application/json")
    Call<Void> likeStori(
            @Header("Authorization") String token,
            @Path("id") int storiId
    );

    @POST("api/V1/readStori/{id}")
    @Headers("Accept: application/json")
    Call<Void> readStori(
            @Header("Authorization") String token,
            @Path("id") int storiId
    );

    @POST("api/V1/saveWord/{id}")
    @Headers("Accept: application/json")
    Call<Void> saveWord(
            @Header("Authorization") String token,
            @Path("id") int wordId
    );

    @GET("api/V1/words")
    @Headers("Accept: application/json")
    Call<Vocabulary> getWords(@Header("Authorization") String token);

    @POST("api/V1/deleteWord/{id}")
    @Headers("Accept: application/json")
    Call<Void> removeWord(
            @Header("Authorization") String token,
            @Path("id") int wordId
    );
}