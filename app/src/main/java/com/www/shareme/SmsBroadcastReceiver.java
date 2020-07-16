package com.www.shareme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsBroadcastReceiver";
    OtpReceivedInterface otpReceiveInterface=null;
    public void setOnOtpListeners(OtpReceivedInterface otpReceiveInterface) {
        Log.d(TAG, "onReceive: InterFace ");
        this.otpReceiveInterface = otpReceiveInterface;
    }
    @Override public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status mStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            switch (mStatus.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents'
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    Log.d(TAG, "onReceive: failure "+message);
                    if (otpReceiveInterface != null) {
                        String otp = message.replace("<#> Your otp code is : ", "");
                        otpReceiveInterface.onOtpReceived(otp);
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    Log.d(TAG, "onReceive: failure");
                    if (otpReceiveInterface != null) {
                        otpReceiveInterface.onOtpTimeout();
                    }
                    break;
            }
        }


//        Log.d(TAG, "onReceive: ");
//        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
//            Bundle extras = intent.getExtras();
//            Status mStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
//            Log.d(TAG, "onReceive: status code " + mStatus.getStatusCode());
//            switch (mStatus.getStatusCode()) {
//                case CommonStatusCodes.SUCCESS:
//                    // Get SMS message contents'
//                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
//                    if (otpReceiveInterface != null) {
//                        Log.d(TAG, "onReceive: Success "+message);
//                        // Here we are using 6 digit Otp code.
//                        Pattern pattern = Pattern.compile("(|^)\\d{4}");
//                        Matcher matcher = pattern.matcher(message);
//                        if (matcher.find()) {
//                            Toast.makeText(context, "Full Message Received " + message, Toast.LENGTH_LONG).show();
//                            otpReceiveInterface.onOtpReceived(matcher.group(0));
//                        }
//
//                    }
//                    break;
//                case CommonStatusCodes.TIMEOUT:
//                    // Waiting for SMS timed out (5 minutes)
//                    Log.d(TAG, "onReceive: failure");
//                    if (otpReceiveInterface != null) {
//                        otpReceiveInterface.onOtpTimeout();
//                    }
//                    break;
//            }
//        }


    }
}
