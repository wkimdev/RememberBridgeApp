package com.example.remeberbridge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.remeberbridge.mypage.AddDogActivity;

public class MainActivity3 extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    private AutoCompleteTextView autoCompleteTextView;
    private TextView text_show_view;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        //
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView_1);
        text_show_view = findViewById(R.id.text_show_view);

        Resources resources = getResources();
        String[] regionArray = resources.getStringArray(R.array.dog_variety);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity3.this, R.layout.dropdown_item, regionArray);
        autoCompleteTextView.setAdapter(arrayAdapter);


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                text_show_view.setText((String)adapterView.getItemAtPosition(i));
            }
        });
    }
}