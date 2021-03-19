package com.hbernabe.loots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddProdDetailsActivity extends AppCompatActivity
{
    private String categ, desc, price, pName, currentDate, currentTime;
    private Button addProdBtn;
    private ImageView inProdImg;
    private EditText inProdName, inProdDesc, inProdPrice;
    private static final int gallery = 1;
    private Uri imgUri;
    private String prodKey, dImgUrl;
    private StorageReference prodImgRef;
    private DatabaseReference prodRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prod_details);


        categ = getIntent().getExtras().get("category").toString();
        prodImgRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        prodRef = FirebaseDatabase.getInstance().getReference().child("Products");


        addProdBtn = (Button) findViewById(R.id.addNewProd);
        inProdImg = (ImageView) findViewById(R.id.chooseProductImage);
        inProdName = (EditText) findViewById(R.id.prodName);
        inProdDesc = (EditText) findViewById(R.id.prodDescription);
        inProdPrice = (EditText) findViewById(R.id.prodPrice);
        loadingBar = new ProgressDialog(this);


        inProdImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });


        addProdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateProductData();
            }
        });
    }



    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, gallery);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== gallery &&  resultCode==RESULT_OK  &&  data!=null)
        {
            imgUri = data.getData();
            inProdImg.setImageURI(imgUri);
        }
    }


    private void ValidateProductData()
    {
        desc = inProdDesc.getText().toString();
        price = inProdPrice.getText().toString();
        pName = inProdName.getText().toString();


        if (imgUri == null)
        {
            Toast.makeText(this, "Choose product image", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(desc))
        {
            Toast.makeText(this, "Please enter product description", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(price))
        {
            Toast.makeText(this, "Please enter product Price", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pName))
        {
            Toast.makeText(this, "Please enter product name", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }
    }



    private void StoreProductInformation()
    {
        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("EEE, d MMM yyyy");
        this.currentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm:ss");
        this.currentTime = currentTime.format(calendar.getTime());

        prodKey = this.currentDate + this.currentTime; // creates unique id for products in database


        final StorageReference filePath = prodImgRef.child(imgUri.getLastPathSegment() + prodKey + ".jpg");

        final UploadTask upTask = filePath.putFile(imgUri);


        upTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AddProdDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AddProdDetailsActivity.this, "Product Image uploaded", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = upTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        dImgUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            dImgUrl = task.getResult().toString();

                            saveProdToDb();
                        }
                    }
                });
            }
        });
    }



    private void saveProdToDb()
    {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", prodKey);
        productMap.put("date", currentDate);
        productMap.put("time", currentTime);
        productMap.put("description", desc);
        productMap.put("image", dImgUrl);
        productMap.put("category", categ);
        productMap.put("price", price);
        productMap.put("pname", pName);

        prodRef.child(prodKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AddProdDetailsActivity.this, AddProdActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AddProdDetailsActivity.this, "Product is added", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddProdDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}