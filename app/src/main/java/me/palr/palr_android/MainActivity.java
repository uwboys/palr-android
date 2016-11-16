package me.palr.palr_android;

import android.animation.Animator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import me.palr.palr_android.api.APIService;
import me.palr.palr_android.models.LoginPayload;
import me.palr.palr_android.models.Token;
import me.palr.palr_android.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    LinearLayout registerView;
    LinearLayout signinView;
    LinearLayout mainBtns;
    String curView;

    EditText signInEmail;
    EditText signInPass;

    EditText registerName;
    EditText registerEmail;
    EditText registerPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.curView = "MAIN";
        registerView = (LinearLayout) findViewById(R.id.register_view);
        signinView = (LinearLayout) findViewById(R.id.signin_view);
        mainBtns = (LinearLayout) findViewById(R.id.main_btns);

        assert (registerView != null);
        assert (signinView != null);
        assert (mainBtns != null);

        setupButtons();
        setupSigninButton();
        setupRegisterButton();
    }

    @Override
    public void onBackPressed() {
        if (this.curView.equals("SIGNIN") || this.curView.equals("REGISTER")) {
            signinView.setVisibility(View.INVISIBLE);
            registerView.setVisibility(View.INVISIBLE);
            mainBtns.setVisibility(View.VISIBLE);

            // Zero out fields
            if (this.curView.equals("SIGNIN")) {
                signInEmail.setText("");
                signInPass.setText("");
            }

            this.curView = "MAIN";
        } else {
            super.onBackPressed();
        }
    }

    private void setupButtons() {
        AppCompatButton signin = (AppCompatButton) findViewById(R.id.main_signin_btn);
        AppCompatButton register = (AppCompatButton) findViewById(R.id.main_register_btn);

        assert(signin != null);
        assert(register != null);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cx = signinView.getWidth() / 2;
                int cy = signinView.getHeight() / 2;

                float finalRadius = (float) Math.hypot(cx, cy);

                Animator anim = ViewAnimationUtils.createCircularReveal(signinView, cx, cy, 0, finalRadius);
                signinView.setVisibility(View.VISIBLE);
                mainBtns.setVisibility(View.INVISIBLE);
                anim.start();

                signInEmail = (EditText)findViewById(R.id.signin_input_email);
                signInPass = (EditText)findViewById(R.id.signin_input_password);
                assert (signInEmail != null);
                assert (signInPass != null);

                MainActivity.this.curView = "SIGNIN";
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cx = registerView.getWidth() / 2;
                int cy = registerView.getHeight() / 2;

                float finalRadius = (float) Math.hypot(cx, cy);

                Animator anim = ViewAnimationUtils.createCircularReveal(registerView, cx, cy, 0, finalRadius);
                registerView.setVisibility(View.VISIBLE);
                mainBtns.setVisibility(View.INVISIBLE);
                anim.start();
                MainActivity.this.curView = "REGISTER";
            }
        });
    }


    private void setupSigninButton() {
        AppCompatButton signin = (AppCompatButton) findViewById(R.id.signin_btn);
        assert(signin != null);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signInEmail.getText().toString();
                String password = signInPass.getText().toString();
                if (!email.equals("") && !password.equals("")) {
                    LoginPayload payload = new LoginPayload(email, password);
                    makeLoginRequest(payload);
                }
            }
        });
    }

    private void setupRegisterButton() {
        AppCompatButton register = (AppCompatButton) findViewById(R.id.register_btn);
        assert(register != null);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameInput = (EditText)findViewById(R.id.register_input_name);
                EditText emailInput = (EditText)findViewById(R.id.register_input_email);
                EditText passwordInput = (EditText)findViewById(R.id.register_input_password);
                assert (nameInput != null);
                assert (emailInput  != null);
                assert (passwordInput != null);
                String name = nameInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (!email.equals("") && !password.equals("") && !name.equals("")) {
                    User user = new User(name, email, password);
                    makeRegisterRequest(user);
                } else {
                    Toast.makeText(getApplicationContext(), "All fields must be filled out", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void makeRegisterRequest(User user) {
        final PalrApplication app = (PalrApplication) getApplication();
        APIService service = app.getAPIService();

        Call<Token> createRegisterReq = service.register(user);

        createRegisterReq.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.raw().code() != 200) {
                    Toast.makeText(getApplicationContext(), "Registration failed! Check all required fields are filled out", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_LONG).show();
                    app.logUserIn(response.body(), MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong, unable to register", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void makeLoginRequest(LoginPayload payload) {
        final PalrApplication app = (PalrApplication) getApplication();
        APIService service = app.getAPIService();

        Call<Token> createLoginReq = service.login(payload);

        createLoginReq.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {

                if (response.body() == null) {
                    if (response.raw().code() == 401) {
                        Toast.makeText(getApplicationContext(), "Username or password does not match!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Successfully logged in!", Toast.LENGTH_SHORT).show();
                    app.logUserIn(response.body(), MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
            }
        });
    }

}
