package me.palr.palr_android;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import me.palr.palr_android.api.APIService;
import me.palr.palr_android.models.Conversation;
import me.palr.palr_android.models.Token;
import me.palr.palr_android.models.User;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by maazali on 2016-10-15.
 */
public class PalrApplication extends Application {
    private APIService apiService;
    User currentUser;

    HashMap<String, Conversation> conversations;

    public void onCreate() {
        super.onCreate();
        Iconify.with(new MaterialModule());

        Stetho.initializeWithDefaults(this);

        FacebookSdk.sdkInitialize(getApplicationContext());

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(APIService.class);
    }

    public void logUserIn(Token token, MainActivity activity) {
        final String jwtToken = token.getAccessToken();
        Interceptor authorizationInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("Authorization", jwtToken).build();
                return chain.proceed(newRequest);
            }
        };


        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(authorizationInterceptor)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(APIService.class);

        makeGetUserReq(token, activity);
    }

    public APIService getAPIService() {
        return apiService;
    }

    public HashMap<String, Conversation> getConversations() {
        return conversations;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setConversations(HashMap<String, Conversation> conversations) {
        this.conversations = conversations;
    }

    private void makeGetUserReq(Token token, MainActivity a) {
        final MainActivity activity = a;
        Call<User> userReq = getAPIService().getUser(token.getUserId());
        userReq.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() == null) {
                    Toast.makeText(getApplicationContext(), "Response body was null", Toast.LENGTH_LONG).show();
                } else {
                    currentUser = response.body();
                    Intent intent = new Intent(activity, MatchActivity.class);
                    activity.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}
