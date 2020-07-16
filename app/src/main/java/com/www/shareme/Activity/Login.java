package com.www.shareme.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.www.shareme.Database.MydbAdapter;
import com.www.shareme.R;
import com.www.shareme.databinding.ActivityLoginBinding;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private static final Pattern Password= Pattern.compile("^[a-zA-Z0-9~!@#$%^&*()_+=-]{6,}$");
    SharedPreferences sp;
    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_login);

       sp= getSharedPreferences("login",MODE_PRIVATE);
       if(sp.getBoolean("logged",false)){
           Intent intent=new Intent(Login.this,Home.class);
           startActivity(intent);
           finish();
       }

       binding.tvCreateaccount.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(Login.this,CreateAccount.class);
               startActivity(intent);
               finish();
           }
       });

       binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(Login.this,ForgotPassword.class);
               startActivity(intent);
               finish();
           }
       });


        binding.etLoginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email= binding.etLoginEmail.getText().toString().trim();
                if(email.isEmpty()){
                    binding.tvLoginEmailError.setVisibility(View.VISIBLE);

                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.tvLoginEmailError.setVisibility(View.VISIBLE);

                }
                else{
                    binding.tvLoginEmailError.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.etLoginPswrd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password= binding.etLoginPswrd.getText().toString().trim();
                if(password.isEmpty()){
                    binding.tvLoginPswrdError.setVisibility(View.VISIBLE);

                }
                else if(!Password.matcher(password).matches()){
                    binding.tvLoginPswrdError.setVisibility(View.VISIBLE);


                }
                else{
                    binding.tvLoginPswrdError.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    public void signin(View view){

        //for searching the data
        MydbAdapter mydbAdapter=new MydbAdapter(this);

        String email= binding.etLoginEmail.getText().toString().trim();
        String password= binding.etLoginPswrd.getText().toString().trim();
        StringBuffer add= new StringBuffer();
        add.append(email+" "+password);
       String check= mydbAdapter.search(this,email,password);

        if(!validate_email() | !validate_password()){
            return;
        }
        else if(add.toString().equals(check)  ) {

//         String check1= mydbAdapter.search(this,email,password);

            Toast.makeText(this, "Log in successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
            //for one time login only..
            sp.edit().putBoolean("logged", true).apply();
            finish();
        }
         else if(add.toString() != check){
             Toast.makeText(this, "Credentials is invalid.", Toast.LENGTH_SHORT).show();
             return;
         }


        }




    public boolean validate_email(){

        String email= binding.etLoginEmail.getText().toString().trim();
        if(email.isEmpty()){
            binding.tvLoginEmailError.setVisibility(View.VISIBLE);
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.tvLoginEmailError.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            binding.tvLoginEmailError.setVisibility(View.GONE);
            return true;
        }

    }

    public boolean validate_password(){
        String password= binding.etLoginPswrd.getText().toString().trim();
        if(password.isEmpty()){
            binding.tvLoginPswrdError.setVisibility(View.VISIBLE);
            return false;
        }
        else if(!Password.matcher(password).matches()){
            binding.tvLoginPswrdError.setVisibility(View.VISIBLE);
            return false;

        }
        else{
            binding.tvLoginPswrdError.setVisibility(View.GONE);
            return true;
        }

    }


}