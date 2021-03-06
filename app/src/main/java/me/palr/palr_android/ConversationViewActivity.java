package me.palr.palr_android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import me.palr.palr_android.api.APIService;
import me.palr.palr_android.models.Conversation;
import me.palr.palr_android.models.Message;
import me.palr.palr_android.models.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maazali on 2016-10-16.
 */
public class ConversationViewActivity extends AppCompatActivity {
    private Conversation conversation;
    private int conversationPosition;

    private AppCompatButton messageSendBtn;
    private RecyclerView recyclerView;
    private ScrollView scrollView;
    private EditText messageContentInput;
    private SimpleItemRecyclerViewAdapter adapter;

    private List<Message> messageList;

    private Socket mSocket;
    private Boolean isConnected = true;

    private Menu curMenu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_view);

        conversationPosition = getIntent().getIntExtra("item_index", 0);
        PalrApplication app = (PalrApplication) getApplication();
        conversation = app.getConversations().get(conversationPosition);

        messageList = new ArrayList<Message>();
        adapter = new SimpleItemRecyclerViewAdapter(messageList);

        messageSendBtn = (AppCompatButton) findViewById(R.id.message_send_btn);
        messageContentInput = (EditText) findViewById(R.id.message_content_input);
        recyclerView = (RecyclerView) findViewById(R.id.message_list);
        scrollView = (ScrollView) findViewById(R.id.message_list_wrapper);
        assert (messageSendBtn != null);
        assert (messageContentInput != null);
        assert (recyclerView != null);
        assert (scrollView != null);


        setupAppbar();

        setupMessageBtn();
        setupRecyclerView();

        // To get message data pushed!
        connectSocket();
    }

    @Override
    protected void onStart() {
        super.onStart();
        makeMessageRequest();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

        Log.d("DEBUG", "WE ARE CALLING DISCONNECT (Activity is being destroyed!)");

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("message", onNewMessage);
        mSocket.off("permanent_match", onPermanentMatch);
        mSocket.off("delete_conversation", onDeleteConversation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        curMenu = menu;
        getMenuInflater().inflate(R.menu.conversation_view_actions, menu);
        MenuItem myProfile = menu.findItem(R.id.action_permanent_match);
        MaterialIcons icon = MaterialIcons.md_favorite_border;
        if (conversation.getIsPermanent()) {
            icon = MaterialIcons.md_favorite;
        }
        myProfile.setIcon(
                new IconDrawable(this, icon)
                        .colorRes(R.color.grey50)
                        .actionBarSize()
        );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_permanent_match:
                if (!conversation.getIsPermanent()) {
                    makePermanentMatchRequest();
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void setupAppbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.conversation_view_appbar);
        setSupportActionBar(toolbar);
        setTitle(conversation.getPal().getName());
    }


    private void setupMessageBtn() {
        AppCompatButton messageSendBtn = (AppCompatButton) findViewById(R.id.message_send_btn);

        assert (messageSendBtn != null);

        messageSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText contentInput = (EditText)findViewById(R.id.message_content_input);
                assert (contentInput != null);
                String content = contentInput.getText().toString();
                if (content.equals("")) {
                    return;
                }
                Message msg = new Message(content, conversation.getConversationDataId());
                makeMessageCreateRequest(msg);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        scrollToBottom();
    }

    private void makePermanentMatchRequest() {
        final PalrApplication app = (PalrApplication) getApplication();
        APIService service = app.getAPIService();

        Map<String, String> payload = new HashMap<>();
        payload.put("conversationId", conversation.getId());

        Call<Object> createPermReq = service.requestPermanentMatch(payload);

        createPermReq.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                if (response.raw().code() != 200) {
                    Toast.makeText(getApplicationContext(), "Something went wrong, make permanent request unsuccessful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Make permanent request sent!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong, make permanent request unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeMessageCreateRequest(Message message) {
        final PalrApplication app = (PalrApplication) getApplication();
        APIService service = app.getAPIService();

        Call<Message> createRegisterReq = service.createMessage(message);

        createRegisterReq.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                if (response.raw().code() != 200) {
                    Toast.makeText(getApplicationContext(), "Something went wrong, message not sent!", Toast.LENGTH_LONG).show();
                } else {
                    EditText contentInput = (EditText)findViewById(R.id.message_content_input);
                    assert (contentInput != null);
                    contentInput.setText("");
                    addMessage(response.body());
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong, message not sent!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void makeMessageRequest() {
        final PalrApplication app = (PalrApplication) getApplication();
        APIService service = app.getAPIService();

        Call<List<Message>> messagesReq = service.getMessages(conversation.getConversationDataId());


        messagesReq.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {

                if (response.body() == null) {
                    handleNullBody(response);
                } else {
                    messageList.clear();
                    messageList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    scrollToBottom();
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }

    private void handleNullBody(Response response) {
        Toast err = Toast.makeText(getApplicationContext(), "Response body was null", Toast.LENGTH_LONG);
        err.show();
        if (response.raw().code() == 401) {
            Intent loginIntent = new Intent(ConversationViewActivity.this, MainActivity.class);
            this.startActivity(loginIntent);
            this.finish();
        }
    }


    private void scrollToBottom() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
                int bottom = lastChild.getBottom() + scrollView.getPaddingBottom();
                int sy = scrollView.getScrollY();
                int sh = scrollView.getHeight();
                int delta = bottom - (sy + sh);

                scrollView.smoothScrollBy(0, delta);
            }
        });
    }


    private void setupRecyclerView() {
        LinearLayoutManager layout = new LinearLayoutManager(ConversationViewActivity.this);
        layout.setReverseLayout(true);
        layout.setStackFromEnd(true);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        scrollToBottom();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Message> messages;

        public SimpleItemRecyclerViewAdapter(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = this.messages.get(position);
            String createdByName = holder.mItem.getCreatedBy().getName();
            String memo = holder.mItem.getContent();
            holder.createdByName.setText(createdByName);
            holder.content.setText(memo);
            // get current user
            PalrApplication app = (PalrApplication) getApplication();

            Picasso.with(ConversationViewActivity.this)
                    .load(holder.mItem.getCreatedBy().getImageUrl())
                    .fit()
                    .placeholder(R.drawable.default_profile_picture)
                    .error(R.drawable.default_profile_picture)
                    .into(holder.createdByImage);

            if (holder.mItem.getCreatedBy() != null && app.getCurrentUser().getId().equals(holder.mItem.getCreatedBy().getId())) {
                holder.createdByName.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            } else { // Current user is not the creator of message
                holder.createdByName.setTextColor(getResources().getColor(R.color.grey700));
                setupImageClick(holder);
            }
        }

        private void setupImageClick(ViewHolder holder) {
            if (!conversation.getIsPermanent()) {
                return;
            }

            holder.createdByImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("item_index", conversationPosition);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final CardView cardView;
            public final CircleImageView createdByImage;
            public final TextView createdByName;
            public final TextView content;
            public Message mItem;

            public ViewHolder(View view) {
                super(view);
                createdByImage = (CircleImageView) view.findViewById(R.id.message_card_image);
                cardView = (CardView) view.findViewById(R.id.message_card);
                createdByName  = (TextView) view.findViewById(R.id.message_created_by_name);
                content = (TextView) view.findViewById(R.id.message_content);

                assert (createdByImage != null);
            }
        }
    }

    private void addMessage(Message newMsg) {
        if (messageList != null && adapter != null) {
            messageList.add(0, newMsg);
            Log.d("DEBUG", "Inserting Message");
            Log.d("DEBUG", "Size of messageList: " + messageList.size());
            recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(messageList));
            scrollToBottom();
        }
    }

    private void connectSocket() {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = false;

            mSocket = IO.socket(getResources().getString(R.string.websocket_url), opts);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("message", onNewMessage);
        mSocket.on("permanent_match", onPermanentMatch);
        mSocket.on("delete_conversation", onDeleteConversation);
        mSocket.connect();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ConversationViewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        isConnected = true;
                    }
                    Log.d("DEBUG", "We've connected to the websocket");
                    Token curToken = ((PalrApplication) getApplication()).getCurrentToken();

                    mSocket.emit("add_client", curToken.getAccessToken());
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ConversationViewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    Log.d("DEBUG", "We've disconnected from websocket");
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ConversationViewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("DEBUG", "Websocket errored out");
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ConversationViewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    JsonParser jsonParser = new JsonParser();
                    JsonObject gsonMsg = (JsonObject)jsonParser.parse(args[0].toString());
                    Message newMsg = gson.fromJson(gsonMsg, Message.class);
                    addMessage(newMsg);
                }
            });
        }
    };

    private Emitter.Listener onPermanentMatch = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ConversationViewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("DEBUG", "Permanently matched!");
                    MenuItem myProfile = curMenu.findItem(R.id.action_permanent_match);
                    MaterialIcons icon = MaterialIcons.md_favorite;
                    myProfile.setIcon(
                            new IconDrawable(ConversationViewActivity.this, MaterialIcons.md_favorite)
                                    .colorRes(R.color.grey50)
                                    .actionBarSize()
                    );

                    Toast.makeText(ConversationViewActivity.this, "You've been permanently matched!", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onDeleteConversation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ConversationViewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject)jsonParser.parse(args[0].toString());
                    Log.d("DEBUG", "Conversation Deleted!" + jsonObject.toString());
                }
            });
        }
    };
}
