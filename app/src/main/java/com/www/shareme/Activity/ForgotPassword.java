package com.www.shareme.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.www.shareme.R;
import com.www.shareme.SmsBroadcastReceiver;
import com.www.shareme.databinding.ActivityForgotPasswordBinding;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.regex.Pattern;

public class ForgotPassword extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    // total length 5+1 15+1 zero is not counted
    public static final Pattern Phone= Pattern.compile("^[+]?(0)?[1-9][0-9]{5,15}$");
    ActivityForgotPasswordBinding binding;
    GoogleApiClient mGoogleApiClient;

    private int RESOLVE_HINT = 2;
    SmsBroadcastReceiver mSmsBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //for stricting the thread for otp sending on mobile phone
        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


                binding= DataBindingUtil.setContentView(this,R.layout.activity_forgot_password);


        //set google api client for hint request for phone number selection
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();



        getHintPhoneNumber();




        binding.tvSignin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(ForgotPassword.this,Login.class);
               startActivity(intent);
               finish();
           }
       });

       binding.etForgotEmail.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               String mail= binding.etForgotEmail.getText().toString().trim();
               if(mail.isEmpty()){
                   binding.tvForgotEmailError.setVisibility(View.VISIBLE);
               }
               else if(Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                   binding.tvForgotEmailError.setVisibility(View.GONE);
               }
               else if(Phone.matcher(mail).matches()){
                   binding.tvForgotEmailError.setVisibility(View.GONE);
               }

               else{
                   binding.tvForgotEmailError.setVisibility(View.VISIBLE);
               }
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });


    }
//submit button
    public void submit(View view){
      if(!validate_mail()){
          return;
      }

      else{

//for sending the otp
             sendSms();


      }




    }

    public boolean validate_mail(){
        String mail= binding.etForgotEmail.getText().toString().trim();
        if(mail.isEmpty()){
            binding.tvForgotEmailError.setVisibility(View.VISIBLE);
            return false;
        }
        else if(Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            binding.tvForgotEmailError.setVisibility(View.GONE);
            return true;
        }
        else if(Phone.matcher(mail).matches()){
            binding.tvForgotEmailError.setVisibility(View.GONE);
            return true;
        }
        else{
            binding.tvForgotEmailError.setVisibility(View.VISIBLE);
            return false;
        }
    }


    //for getting the hint of the number for selection
    public void getHintPhoneNumber() {
        HintRequest hintRequest =
                new HintRequest.Builder()
                        .setPhoneNumberIdentifierSupported(true)
                        .build();
        PendingIntent mIntent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(mIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result if we want hint number
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                // credential.getId();  <-- will need to process phone number string
                binding.etForgotEmail.setText(credential.getId());
            }
        }
    }

//    public void startSMSListener() {
//        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
//        Task<Void> mTask = mClient.startSmsRetriever();
//        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override public void onSuccess(Void aVoid) {
////                layoutInput.setVisibility(View.GONE);
////                layoutVerify.setVisibility(View.VISIBLE);
//
//
//
//                Toast.makeText(ForgotPassword.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
//            }
//        });
//        mTask.addOnFailureListener(new OnFailureListener() {
//            @Override public void onFailure(@NonNull Exception e) {
//                Toast.makeText(ForgotPassword.this, "Error", Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // for sending the otp
    public void sendSms() {
        try {
            // Construct data
            String apiKey = "apikey=" + "u2cCoGyS+3I-hbYblyIcyAhMHTFLQ49crYAoY0ydz6";
            Random random= new Random();
         int random_num= random.nextInt(9999);

            String message = "&message=" + "<#> Your otp code is : "+random_num+"\n"+"C58VsZNRBNy";
            String sender = "&sender=" + "TXTLCL";
            String numbers = "&numbers=" + binding.etForgotEmail.getText().toString().trim();

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
            String data = apiKey + numbers + message + sender;

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
//                stringBuffer.append(line);
                Toast.makeText(this, "Otp sent successfully!!" , Toast.LENGTH_SHORT).show();

            }
            rd.close();
            Intent i=new Intent(ForgotPassword.this,Otp.class);
            i.putExtra("otp",random_num);
            startActivity(i);

        } catch (Exception e) {
            System.out.println("Error SMS "+e);
            Log.e("error","Error sms"+e);
        }
    }


//    @Override
//    public void onOtpReceived(String otp) {
//        Toast.makeText(this, "Otp Received " + otp, Toast.LENGTH_LONG).show();
////        binding.otp.setText(otp);
//    }
//
//    @Override
//    public void onOtpTimeout() {
//        Toast.makeText(this, "Time out, please resend", Toast.LENGTH_LONG).show();
//
//    }
}