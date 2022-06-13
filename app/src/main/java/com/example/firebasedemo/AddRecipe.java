package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddRecipe extends AppCompatActivity {

    int i=0,j=0;
    Button addIngredient,removeIngredient,addStep,removeStep;
    View ingredientViews[]=new View[30];
    View StepViews[]=new View[30];
    CheckBox post;
    DocumentReference documentReference;




    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addrecipe);

        FirebaseFirestore putdata=FirebaseFirestore.getInstance();


        LinearLayout ll = findViewById(R.id.addIngredientLayout);
        LinearLayout ll2 = findViewById(R.id.addStepLayout);
        addIngredient=findViewById(R.id.addIngredientButton);
        removeIngredient=findViewById(R.id.removeIngredientButton);
        addStep=findViewById(R.id.addStepButton);
        removeStep=findViewById(R.id.removeStepButton);
        post=findViewById(R.id.postPublic);

        setAddIngredient(ll);
        setAddStep(ll2);

        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t=ingredientViews[i].findViewById(R.id.No);
                if(i<29&&!(t.getText().toString().isEmpty()))
                {
                    setAddIngredient(ll);
                }
                else if((t.getText().toString().isEmpty()))
                {
                    Toast.makeText(AddRecipe.this, "Please fill previous ingredient", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(AddRecipe.this, "Maximum ingredients reached", Toast.LENGTH_SHORT).show();
            }
        });
        removeIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i>1){
                ll.removeView(ingredientViews[i]);
                i--;
                }
                else Toast.makeText(AddRecipe.this, "Minimum 1 ingredient required", Toast.LENGTH_SHORT).show();
            }
        });
        addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t=StepViews[j].findViewById(R.id.No);
                if(j<29&&!(t.getText().toString().isEmpty()))
                {
                    setAddStep(ll2);
                }
                else if((t.getText().toString().isEmpty()))
                {
                    Toast.makeText(AddRecipe.this, "Please fill previous step", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(AddRecipe.this, "Maximum steps reached", Toast.LENGTH_SHORT).show();
            }

        });
        removeStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(j>1){
                    ll2.removeView(StepViews[j]);
                    j--;
                }

                else Toast.makeText(AddRecipe.this, "Minimum 1 step is required", Toast.LENGTH_SHORT).show();
            }
        });

        CollectionReference collectionReference=putdata.collection("recipes");
        documentReference=collectionReference.document();

        findViewById(R.id.addImagebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                openImage();

            }
        });


        findViewById(R.id.submitrecipe).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Map<String,Object> data=new HashMap();
            TextView t1=findViewById(R.id.addRecipeName);
            TextView t2=findViewById(R.id.addRecipeOneline);

            ArrayList<String> s1=new ArrayList<>();
            ArrayList<String> s2=new ArrayList<>();

            Boolean submitForVerification;

            for (int a=1;a<=i;a++){
                TextView t3=ingredientViews[a].findViewById(R.id.No);
                s1.add(t3.getText().toString());
            }
            for (int a=1;a<=j;a++){
                TextView t4=StepViews[a].findViewById(R.id.No);
                s2.add(t4.getText().toString());
            }

            if(post.isChecked())  data.put("WantToPublish",true);
            else data.put("WantToPublish",false);



            data.put("Name",t1.getText().toString());
            data.put("oneline",t2.getText().toString());
            data.put("Ingredients",s1);
            data.put("Steps",s2);
            data.put("Verified",false);
            data.put("Owner", FirebaseAuth.getInstance().getCurrentUser().getUid());


            documentReference.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    Toast.makeText(AddRecipe.this, "Recipe Added", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddRecipe.this,"Error",Toast.LENGTH_SHORT).show();
                }
            });


            Toast.makeText(AddRecipe.this,  documentReference.getId(), Toast.LENGTH_SHORT).show();



        }
    });



    }

    Uri imageUri;


    void setAddIngredient(LinearLayout ll)
    {
        i++;
        LayoutInflater inflater=getLayoutInflater();
        View newLayout=inflater.inflate(R.layout.ingredint_layout,ll,false);
        EditText e=newLayout.findViewById(R.id.No);


        e.setHint("Ingredient:"+i);
        e.requestFocus();
        ingredientViews[i]=newLayout;

        ll.addView(newLayout);
    }
    void setAddStep(LinearLayout ll)
    {
        j++;
        LayoutInflater inflater=getLayoutInflater();
        View newLayout=inflater.inflate(R.layout.ingredint_layout,ll,false);
        EditText e=newLayout.findViewById(R.id.No);


        e.setHint("Step:"+j);
        e.requestFocus();
        StepViews[j]=newLayout;
        ll.addView(newLayout);
    }
    void addimage()
    {
        ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if(imageUri!=null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
           StorageReference storageRef = storage.getReference();
            storageRef.child("Recipe/"+documentReference.getId()).child("thumbnail.jpg").putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    pd.dismiss();

                    Toast.makeText(AddRecipe.this, "Image Added", Toast.LENGTH_SHORT).show();

                }
            });


        }


    }

    private static final int IMAGE_REQUEST=2;
    void openImage()
    {
        Intent intend=new Intent();
        intend.setType("image/");
        intend.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intend,IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK)
    {
        Toast.makeText(AddRecipe.this, "BANANAAAAA", Toast.LENGTH_SHORT).show();

        imageUri=data.getData();
        addimage();
    }



    }
}