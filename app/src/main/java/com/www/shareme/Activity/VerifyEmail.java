package com.www.shareme.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.www.shareme.R;
import com.www.shareme.databinding.ActivityVerifyEmailBinding;

public class VerifyEmail extends AppCompatActivity {

    ActivityVerifyEmailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_verify_email);

        binding.verifyOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pin=binding.verifyOtp.getText().toString().trim();
                if(pin.length()==4){
                    binding.tvVerifyError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.verifyConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin= binding.verifyOtp.getText().toString().trim();

                if(pin.length()<4){
                    binding.tvVerifyError.setVisibility(View.VISIBLE);
                }
                else{
                    binding.tvVerifyError.setVisibility(View.GONE);
                    Intent intent=new Intent(VerifyEmail.this,Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}