package me.palr.palr_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.palr.palr_android.api.APIService;
import me.palr.palr_android.models.Conversation;
import me.palr.palr_android.models.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maazali on 2016-10-16.
 */
public class ConversationViewActivity extends AppCompatActivity {
    private Conversation conversation;
    private AppCompatButton messageSendBtn;
    private RecyclerView recyclerView;
    private EditText messageContentInput;
    private SimpleItemRecyclerViewAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_view);

        int position = getIntent().getIntExtra("item_index", 0);
        PalrApplication app = (PalrApplication) getApplication();

        conversation = app.getConversations().get(position);
        messageSendBtn = (AppCompatButton) findViewById(R.id.message_send_btn);
        messageContentInput = (EditText) findViewById(R.id.message_content_input);
        assert (messageSendBtn != null);
        assert (messageContentInput != null);
        setupMessageBtn();
        setupContentInput();

        // And From your main() method or any other method
        Timer timer = new Timer();
        timer.schedule(new GetMessages(), 0, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        makeMessageRequest();
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

    private void setupContentInput() {
        messageContentInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                Toast.makeText(ConversationViewActivity.this, "FOCUS", Toast.LENGTH_SHORT).show();
//                if (hasFocus && recyclerView != null)
//                    recyclerView.scrollToPosition(0);
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
                    makeMessageRequest();
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
                    recyclerView = (RecyclerView) findViewById(R.id.message_list);
                    assert recyclerView != null;
//
//                    recyclerView.addItemDecoration(
//                            new HorizontalDividerItemDecoration.Builder(ConversationViewActivity.this)
//                                    .build());

                    setupRecyclerView(recyclerView, response.body());
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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Message> messages) {
        LinearLayoutManager layout = new LinearLayoutManager(ConversationViewActivity.this);
        adapter = new SimpleItemRecyclerViewAdapter(messages);
        layout.setReverseLayout(true);
        layout.setStackFromEnd(true);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(messages));
        recyclerView.scrollToPosition(0);
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

            if (holder.mItem.getCreatedBy() != null && app.getCurrentUser().getId().equals(holder.mItem.getCreatedBy().getId())) {
                holder.createdByName.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            } else {
                holder.createdByName.setTextColor(getResources().getColor(R.color.grey700));
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final LinearLayout cardView;
            public final TextView createdByName;
            public final TextView content;
            public Message mItem;

            public ViewHolder(View view) {
                super(view);
                cardView = (LinearLayout) view.findViewById(R.id.message_card);
                createdByName  = (TextView) view.findViewById(R.id.message_created_by_name);
                content = (TextView) view.findViewById(R.id.message_content);
            }
        }
    }

    class GetMessages extends TimerTask {
        public void run() {
            makeMessageRequest();
        }
    }
}
