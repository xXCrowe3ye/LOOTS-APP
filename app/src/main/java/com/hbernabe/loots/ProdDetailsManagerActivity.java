package com.hbernabe.loots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbernabe.loots.Model.Products;
import com.hbernabe.loots.PaperLoc.paperDb;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProdDetailsManagerActivity extends AppCompatActivity
{
    private Button addCartBtn;
    private ImageView prodImg;
    private ElegantNumberButton quantityBtn;
    private TextView prodPrice, prodDesc, prodName;
    private String prodId = "", state = "Normal";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_details_manager);

        prodId = getIntent().getStringExtra("pid");

        addCartBtn = (Button) findViewById(R.id.prodAddToCart);
        quantityBtn = (ElegantNumberButton) findViewById(R.id.numCounterBtn);
        prodImg = (ImageView) findViewById(R.id.prodImageDetails);
        prodName = (TextView) findViewById(R.id.prodNameDetails);
        prodDesc = (TextView) findViewById(R.id.prodDescriptionDetails);
        prodPrice = (TextView) findViewById(R.id.productPriceDetails);


        getProductDetails(prodId);


        addCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                addingToCartList();
            }
        });
    }




    private void addingToCartList()
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("EEE, d MMM yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm:ss");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", prodId);
        cartMap.put("pname", prodName.getText().toString());
        cartMap.put("price", prodPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", quantityBtn.getNumber());


        cartListRef.child("User View").child(paperDb.currentUser.getUsername())
                .child("Products").child(prodId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(ProdDetailsManagerActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ProdDetailsManagerActivity.this, HomeNavigationActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }


    private void getProductDetails(String productID)
    {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    Products products = dataSnapshot.getValue(Products.class);

                    prodName.setText(products.getPname());
                    prodPrice.setText(products.getPrice());
                    prodDesc.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(prodImg); //loads the image to the CardView
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}