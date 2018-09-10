package com.example.nadii.caloriecounter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {


    //create firebase instase.
    private FirebaseAuth mAuth;

    //progress dialog
    private ProgressDialog mLoginProgress;
    private ProgressDialog mRegProgress;

    //design fields.
    private Button mSignUpBtn;
    private Button mSignInBtn;
    private EditText mStartEmail;
    private EditText mStartPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //initialize the FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();

        //progress dialog
        mLoginProgress = new ProgressDialog(this);
        mRegProgress = new ProgressDialog(this);

        //design fields.
        mSignUpBtn = (Button) findViewById(R.id.start_signup_button);
        mSignInBtn = (Button) findViewById(R.id.start_signin_button);
        mStartEmail = (EditText) findViewById(R.id.start_email_txt);
        mStartPassword = (EditText) findViewById(R.id.start_password_txt);


        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mStartEmail.getText().toString();
                String password = mStartPassword.getText().toString();

                //check if fields not empty.
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    register_user(email, password);
                }
            }
        });


        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mStartEmail.getText().toString();
                String password = mStartPassword.getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Logging in");
                    mLoginProgress.setMessage("Please wait while we check your details.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    login_user(email,password);
                }
            }
        });

    }

    private void login_user(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss();

                    if(mAuth.getCurrentUser().isEmailVerified()){

                        Intent main_intent = new Intent(StartActivity.this , MainActivity.class);
                        startActivity(main_intent);
                        finish();

                    }
                    else{

                        // Check if user is signed in (non-null) and update UI accordingly.
                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        if(currentUser != null){

                            FirebaseAuth.getInstance().signOut();
                        }

                        //email is not verified , display message for the user , and log him out.
                        Toast.makeText(StartActivity.this , "Please verify your email before logging in again. ", Toast.LENGTH_LONG).show();

                        Intent reg_intent = new Intent(StartActivity.this , RegisterActivity.class);
                        startActivity(reg_intent);
                        finish();

                    }
                }
                else{

                    mLoginProgress.hide();
                    Toast.makeText(StartActivity.this , "Cannot Sign in. ERROR", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void register_user(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mRegProgress.dismiss();

                    Toast.makeText(StartActivity.this , "Please verify your email before logging in.", Toast.LENGTH_LONG).show();

                    Intent reg_intent = new Intent(StartActivity.this , RegisterActivity.class);
                    startActivity(reg_intent);
                    finish();

                }else{

                    mRegProgress.hide();
                    Toast.makeText(StartActivity.this , "Cannot Sign up. ERROR", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

}
