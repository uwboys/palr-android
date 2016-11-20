package me.palr.palr_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import me.palr.palr_android.api.APIService;
import me.palr.palr_android.models.Conversation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maazali on 2016-10-15.
 */
public class ConversationListActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_conversation_list);
        makeConversationRequest();
        setupAppbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conversation_list_actions, menu);
        MenuItem myProfile = menu.findItem(R.id.action_my_profile);
        myProfile.setIcon(
                new IconDrawable(this, MaterialIcons.md_account_circle)
                        .colorRes(R.color.grey50)
                        .actionBarSize()
        );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_my_profile:
                Intent intent = new Intent(this, ProfileEditActivity.class);
                this.startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void setupAppbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.conversation_list_appbar);
        setSupportActionBar(toolbar);
    }


    private void makeConversationRequest() {
        final PalrApplication app = (PalrApplication) getApplication();
        APIService service = app.getAPIService();

        Call<List<Conversation>> conversationsReq = service.getConversations();


        conversationsReq.enqueue(new Callback<List<Conversation>>() {
            @Override
            public void onResponse(Call<List<Conversation>> call, Response<List<Conversation>> response) {

                if (response.body() == null) {
                    Toast err = Toast.makeText(getApplicationContext(), response.errorBody().toString(), Toast.LENGTH_LONG);
                    err.show();

                    if (response.raw().code() == 401) {
                        Intent loginIntent = new Intent(ConversationListActivity.this, MainActivity.class);
                        ConversationListActivity.this.startActivity(loginIntent);
                        ConversationListActivity.this.finish();
                    }
                } else {
                    View recyclerView = findViewById(R.id.conversation_list);
                    assert recyclerView != null;
                    ((RecyclerView)recyclerView).addItemDecoration(new HorizontalDividerItemDecoration.Builder(ConversationListActivity.this).build());
                    app.setConversations(response.body());
                    setupRecyclerView((RecyclerView)recyclerView, app.getConversations());
                }
            }

            @Override
            public void onFailure(Call<List<Conversation>> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Conversation> conversations) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(conversations));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Conversation> conversations;

        public SimpleItemRecyclerViewAdapter(List<Conversation> conversations) {
            this.conversations = conversations;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.conversation_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Context context = ConversationListActivity.this;
            final int index = position;
            holder.mItem = this.conversations.get(position);
            holder.palName.setText(holder.mItem.getPal().getName());
            holder.lastMessageDate.setText(holder.mItem.getLastMessageDate());

            Picasso.with(ConversationListActivity.this)
                    .load(R.drawable.default_profile_picture)
                    .into(holder.palImage);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ConversationViewActivity.class);
                intent.putExtra("item_index", index);
                context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return conversations.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final RelativeLayout cardView;
            public final CircleImageView palImage;
            public final TextView palName;
            public final TextView lastMessageDate;
            public Conversation mItem;

            public ViewHolder(View view) {
                super(view);
                cardView = (RelativeLayout) view.findViewById(R.id.conversation_card);
                palImage = (CircleImageView) view.findViewById(R.id.conversation_card_image);
                palName = (TextView) view.findViewById(R.id.conversation_card_name);
                lastMessageDate = (TextView) view.findViewById(R.id.conversation_card_date);
            }
        }
    }
}

