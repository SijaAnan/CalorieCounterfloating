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


public class FoodActivity extends AppCompatActivity {

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
        setContentView(R.layout.food_activity);


        // Here I want the user to put his consumed food+calories

        search_edit_text = (EditText) findViewById(R.id.search_edit_text);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        /*
         * Create a array list for each node you want to use
         * */
        fullNameList = new ArrayList<>();

        search_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fullNameList.clear();
                recyclerView.removeAllViews();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    setAdapter(s.toString());
                } else {
                    /*
                     * Clear the list when editText is empty
                     * */
                    fullNameList.clear();
                    recyclerView.removeAllViews();
                }
            }
        });
    }

    private void setAdapter(final String searchedString) {
        databaseReference.child("food").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Clear the list for every new search
                 * */
                //fullNameList.clear();

                recyclerView.removeAllViews();

                int counter = 0;

                /*
                 * Search all users for matching searched string
                 * */
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String food = snapshot.getKey(); // Food string
                    String Calories = snapshot.child("Calories").getValue(String.class); // calories amount (string)
                    //String user_name = snapshot.child("user_name").getValue(String.class);
                    // String profile_pic = snapshot.child("profile_pic").getValue(String.class);

                    if (food.toLowerCase().contains(searchedString.toLowerCase())) {
                        fullNameList.add(food);
                        counter++;

                    }

                    /*
                     * Get maximum of 3 searched results only
                     * */
                    if (counter == 3)
                        break;
                }

                searchAdapter = new SearchAdapter(FoodActivity.this, fullNameList);
                recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

}
