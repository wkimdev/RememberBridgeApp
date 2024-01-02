package com.example.remeberbridge;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class TestActivity extends AppCompatActivity {

    private TextInputLayout testTextInputCustom;
    private TextInputEditText testTextInputNumberEdit;
    private TextInputLayout testTextInputPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        testTextInputCustom = findViewById(R.id.test_textInput_custom);
        testTextInputNumberEdit = findViewById(R.id.test_textInput_numberEdit);
        testTextInputPhoneNum = findViewById(R.id.test_textInput_phoneNum);

        // 아이콘을 커스텀으로 만든 뒤, 클릭해서 다른 이벤트를 줄 수도 있음.
        /*testTextInputCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TestActivity", "onClick: !!!!");
                Toast.makeText(getApplicationContext(),  "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });*/

        // 액티비티 코드에서
        testTextInputCustom.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TestActivity", "onClick: !!!!");
                //todo) 토스트 안뜸
                Toast.makeText(TestActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });


        testTextInputNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 텍스트가 변경되기 전에 호출
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 텍스트가 변경되는 동안 호출
                if (s.length() > 10) {
                    testTextInputPhoneNum.setError("No More!!!");
                } else if (s.length() <= 10) {
                    testTextInputPhoneNum.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 텍스트가 변경된 후 호출
            }
        });

    }
}