package com.example.firebasedemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {


    private LayoutInflater inflater;
    private List<String> headData,descriptionData,docList;
    private List<Integer> imageNum;

    Adapter(Context context,List<String> data1,List<String> data2,List<Integer> imagedata,List<String> docs)
        {
            this.inflater=LayoutInflater.from(context);// to inflate the layout for each item of recycler view.
            headData =data1;//setting heading data here
            descriptionData=data2;//setting description data here
            imageNum =imagedata;
            docList=docs;
        }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.recycler_source,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //get data from list
        String title = headData.get(position);
        String description=descriptionData.get(position);
        Integer image= imageNum.get(position);
        String documentName=docList.get(position);

        //put data into card
        holder.Head.setText(title);
        holder.Description.setText(description);


        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageRef=storage.getReference().child("Recipe/"+documentName+"/thumbnail.jpg");

        long MAX_BYTES=1024*1024;
        storageRef.getBytes(MAX_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                holder.thumbnail.setImageBitmap(bitmap);
                holder.thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                holder.thumbnail.setBackgroundResource(image);
            }
        });

        //holder.thumbnail.setBackgroundResource(image);
        holder.docName=documentName;
    }

    @Override
    public int getItemCount() {
        return headData.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Head,Description;
        String docName;
        ImageView thumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //get items on card
            Head=itemView.findViewById(R.id.headtext);
            Description=itemView.findViewById(R.id.descrtext);
            thumbnail=itemView.findViewById(R.id.thumbnailMain);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecipeData recipe=new RecipeData();

                    //Toast.makeText(itemView.getContext(),docName,Toast.LENGTH_SHORT).show();
                    // itemView.getContext().startActivity(new Intent(itemView.getContext(),RecipeData.class));

                    Intent myIntend=new Intent(itemView.getContext(),RecipeData.class);
                    myIntend.putExtra("docId",docName);

                    itemView.getContext().startActivity(myIntend);
                }
            });
        }
    }
}
