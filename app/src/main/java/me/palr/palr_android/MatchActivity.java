package me.palr.palr_android;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import me.palr.palr_android.api.APIService;
import me.palr.palr_android.models.MatchPayload;
import me.palr.palr_android.models.Message;
import me.palr.palr_android.models.Token;
import me.palr.palr_android.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maazali on 2016-10-16.
 */
public class MatchActivity extends AppCompatActivity {
    LinearLayout matchBtns;
    TextView matchText;
    boolean inProcess;

    Socket mSocket;
    boolean isConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // Check if user is already in match process
        PalrApplication app = (PalrApplication) getApplication();
        inProcess = app.getCurrentUser().isInMatchProcess();
        matchBtns = (LinearLayout) findViewById(R.id.match_btns);
        matchText = (TextView) findViewById(R.id.match_text);
        assert (matchBtns != null);
        assert (matchText != null);
        setButtonVisibility();
        setupButtons();

        // To get message data pushed!
        connectSocket();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("temporary_match", onTemporaryMatch);
    }

    private void setupButtons() {
        AppCompatButton learn = (AppCompatButton) findViewById(R.id.match_learn_btn);
        AppCompatButton listen = (AppCompatButton) findViewById(R.id.match_listen_btn);
        AppCompatButton talk = (AppCompatButton) findViewById(R.id.match_talk_btn);

        assert(learn != null);
        assert(listen != null);
        assert(talk != null);

        learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchActivity.this.makeMatchRequest("LEARN");
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchActivity.this.makeMatchRequest("LISTEN");
            }
        });

        talk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchActivity.this.makeMatchRequest("TALK");
            }
        });
    }

    private void makeMatchRequest(String type) {

        final PalrApplication app = (PalrApplication) getApplication();
        APIService service = app.getAPIService();

        Call<Object> createMatchReq = service.requestMatch(new MatchPayload(type));

        createMatchReq.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.raw().code() != 200) {
                    Toast.makeText(getApplicationContext(), "Unable to request match", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "We are in the process of matching you up!", Toast.LENGTH_LONG).show();
                    inProcess = true;
                    MatchActivity.this.setButtonVisibility();
                    PalrApplication app = (PalrApplication) getApplication();
                    User curUser = app.getCurrentUser();
                    if (curUser.isPermanentlyMatched()) {
                        Intent intent = new Intent(MatchActivity.this, ConversationListActivity.class);
                        MatchActivity.this.startActivity(intent);
                    }

                }

            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setButtonVisibility() {
        if (inProcess) {
            matchBtns.setVisibility(View.INVISIBLE);
            matchText.setVisibility(View.VISIBLE);
        } else {
            matchBtns.setVisibility(View.VISIBLE);
            matchText.setVisibility(View.INVISIBLE);
        }
    }


    private void connectSocket() {
        try {
            mSocket = IO.socket(getResources().getString(R.string.websocket_url));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("temporary_match", onTemporaryMatch);
        mSocket.connect();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            MatchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        isConnected = true;
                    }
                    Log.d("MATCHACTIVITY", "We've connected to the websocket");
                    Token curToken = ((PalrApplication) getApplication()).getCurrentToken();

                    mSocket.emit("add_client", curToken.getAccessToken());
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            MatchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    Log.d("MATCHACTIVITY", "We've disconnected from websocket");
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            MatchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("MATCHACTIVITY", "Websocket errored out");
                }
            });
        }
    };

    private Emitter.Listener onTemporaryMatch = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MatchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("MATCHACTIVITY", "WE've been matched");
                    Intent intent = new Intent(MatchActivity.this, ConversationListActivity.class);
                    MatchActivity.this.startActivity(intent);
                }
            });
        }
    };
}
