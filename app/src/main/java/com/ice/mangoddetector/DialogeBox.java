package com.ice.mangoddetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class DialogeBox extends AppCompatActivity {
    String[] items = {"Almaas",
            "Alphonso",
            "Anmol",
            "Anwar Rataul",
            "BaganPali",
            "Chaunsa",
            "Chok Anan",
            "Collector",
            "Dusehri",
            "Desi Ada Pamato",
            "Desi Badam",
            "Desi Gola",
            "Desi Badshah",
            "Dilkash",
            "Fajri",
            " Gulab Janhu",
            "Gulab Khas",
            "Lahoti",
            "Lal Badshah",
            "Langra",
            "Malda",
            "Muhammad Wole",
            "Nawab Puri",
            "Neelum",
            "Rani Phool",
            "Sindhri",
            "Saroli",
            "Sawarnarika",
            "Saleh Bhai",
            "Saib",
            "Shan-e-Khuda",
            "Taimuria",
            "Toofan",
            "Wanghi",
            "Zafran"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItem;
    Button done;
    String itemG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialoge_box);
        autoCompleteTextView  = findViewById(R.id.text);
        done = findViewById(R.id.done_button);

        adapterItem = new ArrayAdapter<String>(this,R.layout.list_item,items);

        autoCompleteTextView.setAdapter(adapterItem);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start(itemG);
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String item = adapterView.getItemAtPosition(position).toString();
                itemG = item;
                Toast.makeText(getApplicationContext(),"Item: "+item,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void start(String item) {
        if (item==null){
            Toast.makeText(this,"Please Select Specie First",Toast.LENGTH_LONG).show();
        }
        else {
            Intent intent = new Intent(DialogeBox.this,Upload.class);
            intent.putExtra("specie",item);
            startActivity(intent);}

    }
}