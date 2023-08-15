package com.example.firebase_e_commerce.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firebase_e_commerce.Product_Data_Model;
import com.example.firebase_e_commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class Add_Product_Fragment extends Fragment {

    AppCompatEditText pname,pprice,pdes;
    AppCompatImageView pimg;
    AppCompatButton addButton,updateButton;
    Uri resultUri;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseStorage storage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add__product, container, false);

        pname = view.findViewById(R.id.pname);
        pprice = view.findViewById(R.id.pprice);
        pdes = view.findViewById(R.id.pdes);
        pimg = view.findViewById(R.id.pimg);
        addButton = view.findViewById(R.id.addButton);
        updateButton = view.findViewById(R.id.updateButton);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Product").push();
        String id = myRef.getKey();

        Log.d("AAA", "onCreateView: Key= "+id);
        storage = FirebaseStorage.getInstance();
        String ImgName = "IMG_" + new Random().nextInt(10000) + ".jpg";
        StorageReference mainBucket = storage.getReference().child("ProductImages/" + ImgName);

        if (getArguments() == null)
        {
            addButton.setVisibility(View.VISIBLE);
        }

        pimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .start(getContext(),Add_Product_Fragment.this);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pimg.setDrawingCacheEnabled(true);
                pimg.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) pimg.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mainBucket.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        Log.d("YYY", "onSuccess: Image Uploaded");

                        UploadTask uploadTask = mainBucket.putBytes(data);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                        new OnCompleteListener<Uri>() {

                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                String fileLink = task.getResult().toString();
                                                Log.d("AAA", "onComplete: url=" + fileLink);
                                                //next work with URL

                                                Product_Data_Model product_data = new Product_Data_Model(id, pname.getText().toString(), pdes.getText().toString(), pprice.getText().toString(), fileLink);
                                                myRef.setValue(product_data);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("AAA", "onFailure: error  "+e.getLocalizedMessage());
                                    }
                                });
                            }
                        });
                    }

                });
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                pimg.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}