package com.www.shareme.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.FacebookDialog;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.www.shareme.R;
import com.www.shareme.databinding.ActivityLoginoptionBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static com.google.android.gms.common.SignInButton.SIZE_STANDARD;

public class LoginOption extends AppCompatActivity {
ActivityLoginoptionBinding binding;
SharedPreferences sp;
GoogleSignInClient mGoogleSignInClient;

int RC_SIGN_IN=0;
int FC_SIGN_IN=1;

CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_loginoption);


        //for user logged in one time only
        sp=getSharedPreferences("login",MODE_PRIVATE);

        if(sp.getBoolean("logged",false)){
            Intent i=new Intent(LoginOption.this,Home.class);
            startActivity(i);
//            finish();
        }


        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginOption.this,Login.class);
                startActivity(i);
                finish();
            }
        });

        binding.btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginOption.this,CreateAccount.class);
                startActivity(i);
                finish();
            }
        });


        //facebook login
//       FacebookSdk.sdkInitialize(this.getApplicationContext());
        //for holding the value when app is deleted from the phone memory


        final boolean loggedOut= AccessToken.getCurrentAccessToken() == null;
        if(!loggedOut){
            Picasso.with(this).load(Profile.getCurrentProfile().getProfilePictureUri(200,200)).into(binding.logo);

            Log.d("TAG","Username is: "+Profile.getCurrentProfile().getName());


            loadprofile(AccessToken.getCurrentAccessToken());


        }


//        binding.llFacebook.setReadPermissions(Arrays.asList("email","public_profile"));

      //
      callbackManager= CallbackManager.Factory.create();
      //change
      LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
          @Override
          public void onSuccess(LoginResult loginResult) {

              boolean loggedIn= AccessToken.getCurrentAccessToken()==null;
              Log.d("API123",loggedIn+" ??");

              sp.edit().putBoolean("logged",true).apply();
              loadprofile(AccessToken.getCurrentAccessToken());
              Toast.makeText(LoginOption.this, "You are logged in.", Toast.LENGTH_SHORT).show();


          }

          @Override
          public void onCancel() {

          }

          @Override
          public void onError(FacebookException error) {

          }
      });
//
      binding.llFacebook.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        AccessToken current =AccessToken.getCurrentAccessToken();
        if(current !=null){

            final AlertDialog.Builder alertdialog=new AlertDialog.Builder(LoginOption.this);
            alertdialog.setTitle("Are you sure you want to logout?");
            alertdialog.setCancelable(false);
            alertdialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    LoginManager.getInstance().logOut();
                    binding.logo.setImageResource(R.drawable.logo);
                    binding.name.setText(R.string.Share_files);
                    binding.email.setText(R.string.content);
                    binding.tvFacebook.setText("Facebook");
                    sp.edit().putBoolean("logged out",true).apply();
                    dialog.dismiss();
                    Toast.makeText(LoginOption.this, "Logged out", Toast.LENGTH_SHORT).show();

                }
            });

            alertdialog.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Intent i=new Intent(LoginOption.this,Home.class);
//                    startActivity(i);
//                    finish();
                    dialog.dismiss();
                }
            });

            alertdialog.show();
        }
        else
         if(current == null) {
            LoginManager.getInstance().logOut();

             LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);

            LoginManager.getInstance().logInWithReadPermissions(LoginOption.this, Arrays.asList("email", "public_profile"));




        }


    }
});
        //

      // for the google sign in

        // Set the dimensions of the sign-in button.
        LinearLayout signInButton = findViewById(R.id.btn_google_signin);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
// Configure sign-in to request the user's ID, email address, and basic
 // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
// Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_google_signin:
                        signIn();
                        break;

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            callbackManager.onActivityResult(requestCode, resultCode, data);



      if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }



     private void loadprofile(AccessToken accessToken){
 //graph api for read and write operation on facebook

         GraphRequest graphRequest= GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
             @Override
             public void onCompleted(JSONObject object, GraphResponse response) {

                 try {
                     String first_name= object.getString("first_name");
                     String last_name=object.getString("last_name");
                     String email=object.getString("email");
                     String id=object.getString("id");

                     String image_url= "https://graph.facebook.com/"+ id +"/picture?type=large";

                     binding.name.setText(first_name+" "+last_name);
                     binding.email.setText(email);


                     Picasso.with(LoginOption.this).load(image_url).into(binding.logo);
                     binding.tvFacebook.setText("Logout");

                 } catch (JSONException e) {
                     e.printStackTrace();
                 }


             }
         });
         Bundle parameters=new Bundle();
         parameters.putString("fields","first_name,last_name,email,id");
         graphRequest.setParameters(parameters);

         graphRequest.executeAsync();

     }

     //for google sign in
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Intent intent=new Intent(LoginOption.this,GoogleSecond.class);
            startActivity(intent);

            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    //delete request permissions
//    public void deleterequest(){
//        GraphRequest delpermRequest= new GraphRequest(AccessToken.getCurrentAccessToken(), "/{user-id}/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
//            @Override
//            public void onCompleted(GraphResponse response) {
//
//                if(response !=null){
//                    FacebookRequestError error= response.getError();
//
//                    if(error !=null){
//                        Log.e("TAG",error.toString());
//                    }
//                    else {
//                        finish();
//                    }
//                }
//
//            }
//        });
//
//        Log.d("TAG","Executing revoke permissions with graph path"+ delpermRequest.getGraphPath());
//        delpermRequest.executeAsync();
//    }

}