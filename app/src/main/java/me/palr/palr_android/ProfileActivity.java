package me.palr.palr_android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import me.palr.palr_android.models.Conversation;
import me.palr.palr_android.models.User;

/**
 * Created by maazali on 2016-11-20.
 */
public class ProfileActivity extends Activity {
    Conversation conversation;

    CircleImageView imageDisplay;
    TextView nameDisplay;
    TextView genderDisplay;
    TextView ageDisplay;
    TextView countryDisplay;
    TextView ethnicityDisplay;
    TextView hobbiesDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        int position = getIntent().getIntExtra("item_index", 0);
        PalrApplication app = (PalrApplication) getApplication();
        conversation = app.getConversations().get(position);

        imageDisplay = (CircleImageView) findViewById(R.id.profile_display_image);
        nameDisplay = (TextView) findViewById(R.id.profile_display_name);
        genderDisplay = (TextView) findViewById((R.id.profile_display_gender));
        ageDisplay = (TextView) findViewById(R.id.profile_display_age);
        countryDisplay = (TextView) findViewById(R.id.profile_display_country);
        ethnicityDisplay = (TextView) findViewById(R.id.profile_display_ethnicity);
        hobbiesDisplay = (TextView) findViewById(R.id.profile_display_hobbies);

        assert(imageDisplay != null);
        assert(nameDisplay != null);
        assert(genderDisplay != null);
        assert(ageDisplay != null);
        assert(countryDisplay != null);
        assert(ethnicityDisplay != null);
        assert(hobbiesDisplay != null);

        displayUserDetails();
    }

    private void displayUserDetails() {
        User pal = conversation.getPal();
        Picasso.with(this)
                .load(pal.getImageUrl())
                .placeholder(R.drawable.default_profile_picture)
                .error(R.drawable.default_profile_picture)
                .into(imageDisplay);

        nameDisplay.setText(pal.getName());

        if (pal.getGender() != null)
            genderDisplay.setText(pal.getGender());

        if (pal.getAge() != null)
            ageDisplay.setText(String.valueOf(pal.getAge()));

        if (pal.getCountry() != null)
            countryDisplay.setText(pal.getCountry());

        if (pal.getEthnicity() != null)
            ethnicityDisplay.setText(pal.getEthnicity());

        if (pal.getHobbies() != null && pal.getHobbies().length > 0) {
            StringBuilder sb = new StringBuilder();
            String[] arr = pal.getHobbies();
            for (int i = 0; i < arr.length; i++) {
                sb.append(arr[i]);
                if (arr.length - 1 != i) {
                    sb.append(", ");
                }
            }
            hobbiesDisplay.setText(sb.toString());
        }
    }
}
