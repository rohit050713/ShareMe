package com.www.shareme.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.www.shareme.OtpReceivedInterface;
import com.www.shareme.R;
import com.www.shareme.SmsBroadcastReceiver;
import com.www.shareme.databinding.ActivityOtpBinding;

public class Otp extends AppCompatActivity implements OtpReceivedInterface, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SmsBroadcastReceiver mSmsBroadcastReceiver;
    ActivityOtpBinding binding;
    GoogleApiClient mGoogleApiClient;

    private int RESOLVE_HINT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_otp);

        // getting the otp from the other activity
         Intent intent=getIntent();
       final int random_num=intent.getIntExtra("otp",1234);

       //for the retrieving the message from the inbox
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

//        mSmsBroadcastReceiver.setOnOtpListeners(this);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
//        getApplicationContext().registerReceiver(mSmsBroadcastReceiver, intentFilter);

        //for start retriveing the code
        startSMSListener();


//        getHintPhoneNumber();


        binding.otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pin=binding.otp.getText().toString().trim();
                if(pin.length()==4){
                    binding.tvOtpError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // for checking whether the otp is right or not
        binding.otpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp= binding.otp.getText().toString().trim();
                if(otp.length()<4){
                    binding.tvOtpError.setVisibility(View.VISIBLE);
                }
                else if(random_num != Integer.valueOf(otp)){
                    binding.tvOtpError.setVisibility(View.VISIBLE);

                }


                else if(random_num == Integer.valueOf(otp)){
                    binding.tvOtpError.setVisibility(View.GONE);
                    Intent intent=new Intent(Otp.this,Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result if we want hint number
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                // credential.getId();  <-- will need to process phone number string
            }
        }
    }

// for retrieving the code
    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(Otp.this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
//                layoutInput.setVisibility(View.GONE);
//                layoutVerify.setVisibility(View.VISIBLE);

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
                getApplicationContext().registerReceiver(mSmsBroadcastReceiver, intentFilter);

                Toast.makeText(Otp.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                Toast.makeText(Otp.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onOtpReceived(String otp) {
        Toast.makeText(this, "Otp Received " + otp, Toast.LENGTH_LONG).show();
       binding.otp.setText(otp);
    }

    @Override
    public void onOtpTimeout() {
        Toast.makeText(this, "Time out, please resend", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}