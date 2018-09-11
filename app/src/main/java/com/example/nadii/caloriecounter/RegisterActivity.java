package com.example.nadii.caloriecounter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //create firebase instase.
    private FirebaseAuth mAuth;

    private DatabaseReference mDataBase;

    private Button mVerifyBtn;
    private EditText mName;


    EditText   search_edit_text;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<String> fullNameList;
    SearchAdapter searchAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initialize the FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();

        //Toolbar set
        getSupportActionBar().setTitle("Email Verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //design fields.
        mVerifyBtn = (Button) findViewById(R.id.reg_verify_btn);
        mName = (EditText) findViewById(R.id.reg_name_edittxt);

                mVerifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        send_verify_email();
                    }

                });


            }








    private void send_verify_email() {

        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    set_profile();

                    Toast.makeText(RegisterActivity.this , "Profile Set ,Verification Email Sent.", Toast.LENGTH_LONG).show();

                    //after the mail is sent , logout the user and finish the activity
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(RegisterActivity.this , StartActivity.class));
                    finish();
                }
                else{

                    //email not sent. display message , and restart the activity.
                    Toast.makeText(RegisterActivity.this , "ERROR in sending verification email.", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }

    private void set_profile() {

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = current_user.getUid();

        if(current_user != null) {

            //String current_uid = current_user.getUid();
        }

        //reference to the realtime database. pointing to our root.
        mDataBase = FirebaseDatabase.getInstance().getReference();

        mDataBase = mDataBase.child("users").child(current_uid);

        HashMap<String , String> userMap = new HashMap<>();

        userMap.put("name", mName.getText().toString());
        userMap.put("weight" , "0");
        userMap.put("height" , "0");
        userMap.put("gender" , "unknown");
        userMap.put("birth date" , "dd/mm/yyyy");

        mDataBase.setValue(userMap);

        mDataBase = mDataBase.child("food");

        HashMap<String , String> userBreakfast = new HashMap<>();
        userBreakfast.put("egg" , "63");
        mDataBase.child("breakfast").setValue(userBreakfast);

        HashMap<String , String> userLunch = new HashMap<>();
        userLunch.put("burger" , "540");
        mDataBase.child("lunch").setValue(userLunch);

        HashMap<String , String> userDinner = new HashMap<>();
        userDinner.put("spaghetti" , "340");
        mDataBase.child("dinner").setValue(userDinner);

        HashMap<String , String> userSnacks = new HashMap<>();
        userSnacks.put("apple" , "40");
        mDataBase.child("snacks").setValue(userSnacks);
    }
}
