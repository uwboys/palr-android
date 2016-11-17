package me.palr.palr_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import me.palr.palr_android.models.User;

/**
 * Created by maazali on 2016-11-16.
 */
public class ProfileActivity extends AppCompatActivity {

    EditText nameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText ageInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        nameInput = (EditText) findViewById(R.id.profile_input_name);
        emailInput = (EditText) findViewById(R.id.profile_input_email);
        passwordInput = (EditText) findViewById(R.id.profile_input_password);
        ageInput = (EditText) findViewById(R.id.profile_input_age);


        assert (nameInput != null);
        assert (emailInput != null);
        assert (passwordInput != null);
        assert (ageInput != null);

        addCurrentDataToField();
    }

    private void addCurrentDataToField() {
        User curUser = ((PalrApplication) getApplication()).getCurrentUser();

        nameInput.setText(curUser.getName());
        emailInput.setText(curUser.getEmail());
        ageInput.setText(curUser.getAge());
    }
}
