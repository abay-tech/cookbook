package com.example.firebasedemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class RecipeData extends AppCompatActivity {


   ImageView image;
    TextView recipeName,recipeIngredients,recipeSteps,recipeby;
    Map<String,Object> docMapData,userMapData;
    ToggleButton fav;
    FirebaseFirestore db2;
    String name;
    String docName;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_data);


        recipeName=findViewById(R.id.recipeName);
        recipeIngredients=findViewById(R.id.recipeIngredients);
        recipeSteps=findViewById(R.id.recipeSteps);
        image=findViewById(R.id.recipeImage);
        recipeby=findViewById(R.id.recipeby);
        fav=findViewById(R.id.favoriteToggler);
        findViewById(R.id.verifyLayout).setVisibility(View.GONE);

        name = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Intent intent = getIntent();
        docName=intent.getStringExtra("docId");      //getting the documentID

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference recipe= db.collection("recipes").document(docName);

        recipe.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot snapDocument=task.getResult();
                    if(snapDocument.exists())
                    {
                        docMapData = snapDocument.getData();
                        recipeName.setText(docMapData.get("Name").toString());

                        if(!snapDocument.getBoolean("Verified"))
                        {
                            fav.setVisibility(View.GONE);
                            giveMeVerificationBar();
                        }
                        if(snapDocument.get("Owner").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())&&snapDocument.getBoolean("Verified"))
                        {
                            findViewById(R.id.verifyLayout).setVisibility(View.GONE);

                        }

                        List<String> ingredients=(List<String>)docMapData.get("Ingredients");
                        int i;
                        for (i=0; i<ingredients.size()-1; i++){
                        recipeIngredients.append("-"+ingredients.get(i)+"\n");
                        }
                        recipeIngredients.append("-"+ingredients.get(i));

                        List<String> steps=(List<String>)docMapData.get("Steps");
                        for (i=0; i<steps.size(); i++){
                            recipeSteps.append("STEP"+(i+1)+": "+steps.get(i)+"\n\n");

                        }
                        recipeby.append("DATABASE");
                    }
                }
            }
        });

        setImage(docName,image);
        fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    favoriteOn(fav);
                }
                else
                {
                    favoriteOff(fav);
                }

            }
        });

//for star button if already favorite
        db2 = FirebaseFirestore.getInstance();

        db2.collection("users").document(name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot snapDocument=task.getResult();
                    if(snapDocument.exists())
                    {
                        docMapData = snapDocument.getData();
                        s1=(ArrayList<String>)docMapData.get("favorites");
                        for(int i=0;i<s1.size();i++)
                        {
                            if(docName.equals(s1.get(i)))fav.setBackgroundResource(R.drawable.staron__1_);
                        }

                    }
                }
            }

        });
    }


    void setImage(String docuname,ImageView imageView){
        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageRef=storage.getReference().child("Recipe/"+docuname+"/thumbnail.jpg");

        long MAX_BYTES=1024*1024;
        storageRef.getBytes(MAX_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

            }



    ArrayList<String> s1=new ArrayList<>();
    Boolean alreadyFavorite;

    void favoriteOn(ToggleButton tb)
        {
            tb.setBackgroundResource(R.drawable.staron__1_);
            db2 = FirebaseFirestore.getInstance();

            alreadyFavorite=false;
            Map<String,Object> data=new HashMap();

            db2.collection("users").document(name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        DocumentSnapshot snapDocument=task.getResult();
                        if(snapDocument.exists())
                        {
                            docMapData = snapDocument.getData();
                            s1=(ArrayList<String>)docMapData.get("favorites");

                            for(int i=0;i<s1.size();i++)
                            {
                                if(docName.equals(s1.get(i)))alreadyFavorite=true;
                            }

                        if(!alreadyFavorite) {//check if the favorite is already present
                            s1.add(docName);
                            data.put("favorites", s1);
                            db2.collection("users").document(name).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(RecipeData.this, "Added To Favorites", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RecipeData.this, "Error please try again", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        }
                    }
                }
            });






        }

    void favoriteOff(ToggleButton tb)
        {
            tb.setBackgroundResource(R.drawable.staroff__1_);
            Toast.makeText(RecipeData.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();

            db2 = FirebaseFirestore.getInstance();


            Map<String,Object> data=new HashMap();

            db2.collection("users").document(name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        DocumentSnapshot snapDocument=task.getResult();
                        if(snapDocument.exists())
                        {
                            docMapData = snapDocument.getData();
                            s1=(ArrayList<String>)docMapData.get("favorites");

                            for(int i=0;i<s1.size();i++)
                            {
                                if(docName.equals(s1.get(i))){

                                        s1.remove(i);
                                        data.put("favorites", s1);
                                        db2.collection("users").document(name).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(RecipeData.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RecipeData.this, "Error please try again", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                }
                            }


                        }
                    }
                }
            });






        }
    void giveMeVerificationBar()
        {
            findViewById(R.id.verifyLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.adminVerify).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RecipeData.this, "verified", Toast.LENGTH_SHORT).show();

                    FirebaseFirestore putdata=FirebaseFirestore.getInstance();
                    Map<String,Object> data=new HashMap();

                    data.put("Verified",true);

                    putdata.collection("recipes").document(docName).set(data,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(RecipeData.this, "Verified", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RecipeData.this,"Error in verification",Toast.LENGTH_SHORT).show();
                        }
                    });





                }
            });



        }
    void hideVerificationBar()
    {
        findViewById(R.id.verify);



    }


}
