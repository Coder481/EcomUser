package com.example.ecomuser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ecomuser.databinding.ActivitySignInBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private ActivitySignInBinding b;
    private MyApp app;

    // Shared Preferences
    private SharedPreferences mSharedPref;
    public static final String MY_ID="myId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b= ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());


        setupGoogleSignIn();
    }


    private void setupGoogleSignIn() {

        if (getId().length() == 0){
            b.signInWithGoogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.GoogleBuilder().build()
                    );

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN
                    );
                }
            });
        }else{
            Toast.makeText(this, "Welcome: "+getId(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignInActivity.this,MainActivity.class)
                    .putExtra("MyId",getId())
                    .putExtra("Username",getUsernamePref()));
        }
    }

    private String getId() {
        mSharedPref = getSharedPreferences("signInId",MODE_PRIVATE);
        return mSharedPref.getString(MY_ID,"");
    }

    private String getUsernamePref(){
        mSharedPref = getSharedPreferences("signInId",MODE_PRIVATE);
        return mSharedPref.getString("username","");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK){
                // Successfully Signed In
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                saveIdLocally(user.getEmail());
                Toast.makeText(this, ""+user.getEmail(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignInActivity.this,MainActivity.class)
                        .putExtra("MyId",""+user.getEmail())
                        .putExtra("Username",getUsername()));
            }else{
                Toast.makeText(this, "Please sign in to continue!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveIdLocally(String email) {
        mSharedPref = getSharedPreferences("signInId",MODE_PRIVATE);
        mSharedPref.edit().putString(MY_ID,email)
                .putString("username",getUsername())
                .apply();
    }

    private String getUsername() {
        return b.usernameEditText.getText().toString();
    }

}