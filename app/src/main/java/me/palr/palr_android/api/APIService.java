package me.palr.palr_android.api;

import java.util.List;

import me.palr.palr_android.models.Conversation;
import me.palr.palr_android.models.LoginPayload;
import me.palr.palr_android.models.MatchPayload;
import me.palr.palr_android.models.Token;
import me.palr.palr_android.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by maazali on 2016-10-15.
 */
public interface APIService {
    @POST("login")
    Call<Token> login(@Body LoginPayload loginPayload);

    @POST("register")
    Call<Token> register(@Body User user);

    @POST("match")
    Call<Object> requestMatch(@Body MatchPayload payload);

    @GET("users/{id}")
    Call<User> getUser(@Path("id") String userId);

    @GET("conversations")
    Call<List<Conversation>> getConversations();
}
