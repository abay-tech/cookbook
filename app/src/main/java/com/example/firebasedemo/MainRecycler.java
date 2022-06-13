package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MainRecycler extends AppCompatActivity {

    private Button  searchButton;
    ArrayList<String> list1, list2, docList;
    ArrayList<String> list1copy, list2copy, docListcopy;
    private long pressedTime;
    private TextView welcome, searchBar,addRecipe,sellfood;
    ArrayList<Integer> imageList;
    ArrayList<Integer> imageListcopy;
    private String  recipeName, recipeOneLine, searchedData;
    Map<String, Object> docMapData, recipeListData;
    private  FloatingActionButton menu,add,sell;
    boolean isMenu,power=false;
    LinearLayout menuLayout;
    public static String userName;
    boolean menuOpen=false;
    boolean gotOneData=false;



    public void onBackPressed() {
        closeMenu(menuLayout);
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(MainRecycler.this, "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //getWindow().getDecorView().setSystemUiVisibility( WindowInsetsController.BEHAVIOR_DEFAULT);
        //to prevent adjust of BG image with android keyboard i have provided android:windowSoftInputMode="adjustNothing"  in manifest

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_recycler_view);//going to recycler view


        searchBar = findViewById(R.id.searchBar);
        searchButton = findViewById(R.id.searchButton);
        menuLayout=findViewById(R.id.menuLayout);


        //recycler objects and copies
        {
            list1 = new ArrayList<>();
            list2 = new ArrayList<>();
            imageList = new ArrayList<>();
            docList = new ArrayList<>();

            list1copy = new ArrayList<>();
            list2copy = new ArrayList<>();
            imageListcopy = new ArrayList<>();
            docListcopy = new ArrayList<>();
        }

        //FOR SEARCHING
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //power{error}
                searchedData = searchBar.getText().toString();



                if((power=true) && searchedData.equals("ExecuteOrder66")) {
                    Toast.makeText(MainRecycler.this, "power unleashed", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainRecycler.this, power.class));
                    searchBar.setText("");
                }
                if(!searchedData.isEmpty()){
                    db.collection("recipes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    recipeListData = document.getData();


                                    recipeName = recipeListData.get("Name").toString();
                                    recipeOneLine = recipeListData.get("oneline").toString();

                                    if (searchedData.equalsIgnoreCase(recipeName)||recipeName.contains(searchedData)) {

                                        if(!gotOneData){               //if we get atleast one data,clear all existing lists
                                            list1.clear();
                                            list2.clear();
                                            imageList.clear();
                                            docList.clear();
                                            gotOneData=true;

                                        }
                                            list1.add(recipeName);
                                            list2.add(recipeOneLine);
                                            imageList.add(R.drawable.cherries);

                                            String docname = document.getId();
                                            docList.add(docname);
                                    }
                                }
                                setRecycler(list1, list2, imageList, docList);
                                gotOneData=false;
                            }
                        }
                    });
            }
                else
                    Toast.makeText(MainRecycler.this, "Please enter recipe name", Toast.LENGTH_SHORT).show();
            }


        });

        //cancel button
        findViewById(R.id.cancelSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchedData = searchBar.getText().toString();
                if(!searchedData.isEmpty())
                {
                    searchBar.setText("");
                    setRecycler(list1copy, list2copy, imageListcopy, docListcopy);
                }
            }
        });


        //VARIABLES FOR ADD BUTTON
        {menu=findViewById(R.id.menu);
        add=findViewById(R.id.addrecipebutton);
        addRecipe=findViewById(R.id.addRecipe);

        sell=findViewById(R.id.floatingAddButton2);
        sellfood=findViewById(R.id.sellfood);


        isMenu=false;
        add.hide();
        sell.hide();
        addRecipe.setVisibility(View.GONE);
        sellfood.setVisibility(View.GONE);}

        //MENU BUTTON
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainRecycler.this, "Pressed add button", Toast.LENGTH_SHORT).show();
                closeMenu(menuLayout);
                if(isMenu==false)
                {
                    isMenu=true;
                    add.show();
                    //sell.show();
                    addRecipe.setVisibility(View.VISIBLE);
                   // sellfood.setVisibility(View.VISIBLE);

                }
                else
                {
                    isMenu=false;
                    add.hide();
                    //sell.hide();
                    addRecipe.setVisibility(View.GONE);
                   // sellfood.setVisibility(View.GONE);
                }
            }
        });

        //ADD BUTTON
        add.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Toast.makeText(MainRecycler.this, "Pressed add button", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainRecycler.this, AddRecipe.class));


        }
   });

        //PRIMARY DATA RETRIEVAL FOR RECYCLER
        db.collection("recipes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Boolean isVerified;
                        recipeListData = document.getData();
                        recipeName = recipeListData.get("Name").toString();
                        recipeOneLine = recipeListData.get("oneline").toString();

                        isVerified=document.getBoolean("Verified");

                        if(!isVerified)continue;

                        list1.add(recipeName);
                        list2.add(recipeOneLine);
                        imageList.add(R.drawable.cherries);

                        String docname = document.getId().toString();
                        docList.add(docname);

                        list1copy.add(recipeName);
                        list2copy.add(recipeOneLine);
                        imageListcopy.add(R.drawable.cherries);


                        docListcopy.add(docname);
                    }

                    setRecycler(list1, list2, imageList, docList);


                }
            }
        });

        //GETTING NAME FROM DATABASE
        //String name = FirebaseAuth.getInstance().getCurrentUser().getEmail();



        DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapDocument = task.getResult();//snapDocument is the document
                    if (snapDocument.exists()) {
                        docMapData = snapDocument.getData();//docmapdata is hashmap
                        userName = docMapData.get("Name").toString();

                        try {
                            if(docMapData.get("poweruser").toString().equals("true"))
                            {
                                //power=true;
                            }else power=false;
                        }
                        catch (Exception e){
                            power=false;
                        }


                    }
                }
            }
        });



        //for menu button and every damn menu controls
        findViewById(R.id.menuButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLayout.setVisibility(View.VISIBLE);
                menuLayout=findViewById(R.id.menuLayout);
                LayoutInflater inflater=getLayoutInflater();
                View newLayout=inflater.inflate(R.layout.sidemenu,menuLayout,false);
                menuLayout.addView(newLayout);
                menuLayout.setClickable(true);
                menuOpen=true;


                Animation anim2=new TranslateAnimation(-400,0,0,0);
                anim2.setDuration(50);
                menuLayout.startAnimation(anim2);


                //for setting texts and listeners of buttons on menu
                welcome = newLayout.findViewById(R.id.welcomeText);
                welcome.setText("Welcome " + userName);
                findViewById(R.id.logoutText).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(MainRecycler.this, "LOGGED OUT!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainRecycler.this, StartActivity.class));
                        finish();
                    }
                });

                findViewById(R.id.Profile).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(MainRecycler.this, profileActivity.class));
                    }
                });

                findViewById(R.id.Favorites).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainRecycler.this, favoritesActivity.class));
                    }
                });

                findViewById(R.id.verifyRecipes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainRecycler.this, submittedRecipeActivity.class));
                    }
                });

                findViewById(R.id.recipeBook).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainRecycler.this, recipeBookActivity.class));
                    }
                });

                findViewById(R.id.contactUs).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainRecycler.this, contactUsActivity.class));
                    }
                });
            }
        });

        //for closing menu shade
        findViewById(R.id.fullLayout).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Toast.makeText(MainRecycler.this, ""+v.getId(), Toast.LENGTH_SHORT).show();
                if(menuOpen=true)closeMenu(menuLayout);
                return false;
            }
        });
    }

    //function to close menu
    void closeMenu(LinearLayout cl){

            Animation anim2=new TranslateAnimation(0,-400,0,0);
            anim2.setDuration(100);

            Animation anim=new AlphaAnimation(1,0);
            anim.setDuration(100);

            if(cl.getAlpha()==1){
                anim2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        cl.removeAllViews();
                        cl.setVisibility(View.GONE);
                        cl.setClickable(false);
                        menuOpen=false;
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                cl.startAnimation(anim2);
            }
        }

    //FUNCTION TO SET RECYCLER CARDVIEW
    public void setRecycler(ArrayList<String> l1, ArrayList<String> l2, ArrayList<Integer> l3, ArrayList<String> l4) {
        RecyclerView recyclerView;
        Adapter adapter;

        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter(this, l1, l2, l3, l4);
        recyclerView.setAdapter(adapter);
    }
}