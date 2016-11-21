package me.palr.palr_android;

import android.app.Activity;
import android.os.Bundle;
import de.hdodenhof.circleimageview.CircleImageView;
import me.palr.palr_android.models.Conversation;

/**
 * Created by maazali on 2016-11-20.
 */
public class ProfileActivity extends Activity {
    Conversation conversation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        int position = getIntent().getIntExtra("item_index", 0);
        PalrApplication app = (PalrApplication) getApplication();
        conversation = app.getConversations().get(position);
    }
}
