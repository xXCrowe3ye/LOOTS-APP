package com.hbernabe.loots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddProdActivity extends AppCompatActivity
{
    private Button selectItem;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prod);

        selectItem = (Button) findViewById(R.id.selectItem);

        selectItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AddProdActivity.this, AddProdDetailsActivity.class);
                intent.putExtra("category", "Product");
                startActivity(intent);
            }
        });


    }
}
