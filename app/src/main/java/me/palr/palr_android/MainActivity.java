package me.palr.palr_android;

import android.animation.Animator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    LinearLayout registerView;
    LinearLayout signinView;
    LinearLayout mainBtns;
    String curView;


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
    }

    @Override
    public void onBackPressed() {
        if (this.curView.equals("SIGNIN") || this.curView.equals("REGISTER")) {
            signinView.setVisibility(View.INVISIBLE);
            registerView.setVisibility(View.INVISIBLE);
            mainBtns.setVisibility(View.VISIBLE);
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
}
