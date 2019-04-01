package com.example.nadii.caloriecounter;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FoodActivity extends AppCompatActivity implements View.OnClickListener, SearchAdapter.SearchListener
{
    FloatingActionButton fabMain, fabOne, fabTwo, fabThree;
    Float translationY = 100f;

    OvershootInterpolator interpolator = new OvershootInterpolator();

    private static final String TAG = "FoodActivity";

    Boolean isMenuOpen = false;
    private FirebaseAuth mAuth;
    ArrayList<String> fullNameList;
    private DatabaseReference mDataBase;
    RecyclerView  recyclerView;
    SearchAdapter searchAdapter;
    FirebaseUser  firebaseUser;

    DatabaseReference databaseReference;

    private EditText search_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_activity);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        initFabMenu();

    }


    private void initFabMenu()
    {
        fabMain = findViewById(R.id.fabMain);
        fabOne = findViewById(R.id.fabOne);
        fabTwo = findViewById(R.id.fabTwo);
        fabThree = findViewById(R.id.fabThree);

        fabOne.setAlpha(0f);
        fabTwo.setAlpha(0f);
        fabThree.setAlpha(0f);

        fabOne.setTranslationY(translationY);
        fabTwo.setTranslationY(translationY);
        fabThree.setTranslationY(translationY);

        fabMain.setOnClickListener(this);
        fabOne.setOnClickListener(this);
        fabTwo.setOnClickListener(this);
        fabThree.setOnClickListener(this);
    }

    private void openMenu()
    {
        isMenuOpen = !isMenuOpen;

        fabMain.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();

        fabOne.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabTwo.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabThree.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();


    }

    private void closeMenu()
    {
        DatabaseReference databaseReference;
        isMenuOpen = !isMenuOpen;

        fabMain.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();

        fabOne.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabTwo.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabThree.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();

    }

    private void handleFabOne()
    {

        Log.i(TAG, "handleFabOne: ");

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(FoodActivity.this);


        View mView = getLayoutInflater().inflate(R.layout.search_food, null);

        search_edit_text = (EditText) mView.findViewById(R.id.search_edit_text);
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

        /*
         * Create an array list for each node you want to use
         * */
        fullNameList = new ArrayList<>();

        search_edit_text.addTextChangedListener(watcher);



    }

    private TextWatcher watcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            fullNameList.clear();
            recyclerView.removeAllViews();
        }

        @Override
        public void afterTextChanged(Editable s)
        {

            if (!s.toString().isEmpty())
            {
                setAdapter(s.toString());

            }
            else
            {
                /*
                 * Clear the list when editText is empty
                 * */
                fullNameList.clear();
                recyclerView.removeAllViews();
            }
        }
    };


    public void onClick(View view)
    {


        switch (view.getId())
        {
            case R.id.fabMain:
                Log.i(TAG, "onClick: fab main");
                if (isMenuOpen)
                {
                    closeMenu();
                }
                else
                {
                    openMenu();
                }
                break;
            case R.id.fabOne:
                Log.i(TAG, "onClick: fab one");
                handleFabOne();
                if (isMenuOpen)
                {
                    closeMenu();
                }
                else
                {
                    openMenu();
                }
                break;
            case R.id.fabTwo:
                Log.i(TAG, "onClick: fab two");
                break;
            case R.id.fabThree:
                Log.i(TAG, "onClick: fab three");
                break;
        }

    }

    private void setAdapter(final String searchedString)
    {
        recyclerView.setVisibility(View.VISIBLE);

        databaseReference.child("food").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                /*
                 * Clear the list for every new search
                 * */

                recyclerView.removeAllViews();

                int counter = 0;

                /*
                 * Search all users for matching searched string
                 * */
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {

                    String food = snapshot.getKey(); // Food string
                    String Calories = snapshot.child("Calories").getValue(String.class); // calories amount (string)


                    if (food.toLowerCase().contains(searchedString.toLowerCase()))
                    {
                        fullNameList.add(food);
                        counter++;

                    }

                    /*
                     * Get maximum of 3 searched results only
                     * */
                    if (counter == 3)
                        break;
                }

                searchAdapter = new SearchAdapter(FoodActivity.this, fullNameList, FoodActivity.this);
                recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    @Override
    public void onItemSelect(String text)
    {
        if (search_edit_text != null)
        {
            search_edit_text.removeTextChangedListener(watcher);
            search_edit_text.setText(text);
            search_edit_text.addTextChangedListener(watcher);

            recyclerView.removeAllViews();
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }
}