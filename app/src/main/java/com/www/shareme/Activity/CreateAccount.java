package com.www.shareme.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.www.shareme.Database.MydbAdapter;
import com.www.shareme.R;
import com.www.shareme.databinding.ActivityCreateAccountBinding;

import java.util.regex.Pattern;

public class CreateAccount extends AppCompatActivity {

    private static final Pattern Password= Pattern.compile("^[a-zA-Z0-9~!@#$%^&*()_+=-]{6,}$");

    // for spacing between the numbers is allowed
    public static final Pattern Phone= Pattern.compile("^[+]?(0)?[1-9 ][0-9 ]{6,16}$");



    ActivityCreateAccountBinding binding;
    MydbAdapter mydbAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_create_account);

        mydbAdapter=new MydbAdapter(this);

        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(CreateAccount.this,Login.class);
                startActivity(i);
                finish();
            }
        });

        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email=binding.etEmail.getText().toString().trim();
                if(email.isEmpty()){
                    binding.tvEmailError.setVisibility(View.VISIBLE);

                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.tvEmailError.setVisibility(View.VISIBLE);

                }
                else{
                    binding.tvEmailError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone= binding.etPhone.getText().toString().trim();
                if(phone.isEmpty()){
                    binding.tvPhoneError.setVisibility(View.VISIBLE);
                }
                else if(!Phone.matcher(phone).matches()){
                    binding.tvPhoneError.setVisibility(View.VISIBLE);
                }
                else{
                    binding.tvPhoneError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etPswrd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pswrd= binding.etPswrd.getText().toString().trim();
                if(pswrd.isEmpty()){
                    binding.tvPswrdError.setVisibility(View.VISIBLE);
                }
                else if(!Password.matcher(pswrd).matches()){
                    binding.tvPswrdError.setVisibility(View.VISIBLE);

                }
                else{
                    binding.tvPswrdError.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.etCnfpswrd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cnfpswrd= binding.etCnfpswrd.getText().toString().trim();
                String pswrd= binding.etPswrd.getText().toString().trim();
                if(cnfpswrd.isEmpty()){
                    binding.tvCnfpswrdError.setVisibility(View.VISIBLE);
                }
                else if(!cnfpswrd.equals(pswrd)){
                    binding.tvCnfpswrdError.setVisibility(View.VISIBLE);
                }
                else{
                    binding.tvCnfpswrdError.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void create(View view){
        String email=binding.etEmail.getText().toString().trim();
        String phone= binding.etPhone.getText().toString().trim();
        String pswrd= binding.etPswrd.getText().toString().trim();


        if(!validateemail() | !validatephone() | !validatepswrd() | !validatecnfpswrd()){
            return;
        }
        else{
            long id= mydbAdapter.insertData(email,phone,pswrd);
            if(id<=0){
                Toast.makeText(this, "Account can't be created.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Account is created.", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(CreateAccount.this,VerifyEmail.class);
                startActivity(intent);
                finish();
            }

        }
    }

    public boolean validateemail(){
        String email=binding.etEmail.getText().toString().trim();
        if(email.isEmpty()){
            binding.tvEmailError.setVisibility(View.VISIBLE);
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.tvEmailError.setVisibility(View.VISIBLE);
            return false;

        }
        else{
            binding.tvEmailError.setVisibility(View.GONE);
            return true;
        }
    }

    public boolean validatephone(){
        String phone= binding.etPhone.getText().toString().trim();
        if(phone.isEmpty()){
            binding.tvPhoneError.setVisibility(View.VISIBLE);
            return false;
        }
        else if(!Phone.matcher(phone).matches()){
            binding.tvPhoneError.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            binding.tvPhoneError.setVisibility(View.GONE);
            return true;
        }
    }

    public boolean validatepswrd(){

        String pswrd= binding.etPswrd.getText().toString().trim();
        if(pswrd.isEmpty()){
            binding.tvPswrdError.setVisibility(View.VISIBLE);
            return false;
        }
        else if(!Password.matcher(pswrd).matches()){
            binding.tvPswrdError.setVisibility(View.VISIBLE);
            return false;

        }
        else{
            binding.tvPswrdError.setVisibility(View.GONE);
            return true;

        }
    }

    public boolean validatecnfpswrd(){
        String cnfpswrd= binding.etCnfpswrd.getText().toString().trim();
        String pswrd= binding.etPswrd.getText().toString().trim();
        if(cnfpswrd.isEmpty()){
            binding.tvCnfpswrdError.setVisibility(View.VISIBLE);
            return false;
        }
        else if(!cnfpswrd.equals(pswrd)){
            binding.tvCnfpswrdError.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            binding.tvCnfpswrdError.setVisibility(View.GONE);
            return true;

        }
    }

}