package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton, PhoneLoginButton, RegisterButton;
    private EditText UserEmail, Password;
    private TextView ForgotPassword;
    private FirebaseAuth Mauth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Mauth=FirebaseAuth.getInstance();

        InitializeFeilds();

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();

            }


        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();

            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPhoneLoginActivity();
            }
        });
    }

    private void sendUserToPhoneLoginActivity() {
        Intent PhoneIntent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
        PhoneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(PhoneIntent);
        finish();


    }

    private void AllowUserToLogin() {

        String email = UserEmail.getText().toString();
        String password = Password.getText().toString();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "please enter your email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "please enter your password...", Toast.LENGTH_SHORT).show();
        }


        else{

            loadingBar.setTitle("verifying credentials");
            loadingBar.setMessage("please wait as we process your request");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            Mauth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(LoginActivity.this,"log in successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                            else {
                                String message= task.getException().toString();
                                Toast.makeText(LoginActivity.this, "error:"+ message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }
    }

    private void InitializeFeilds() {

        LoginButton = (Button) findViewById(R.id.login_button);
        PhoneLoginButton = (Button) findViewById(R.id.phone_button);
        UserEmail = (EditText) findViewById(R.id.login_email);
        Password = (EditText) findViewById(R.id.login_password);
        RegisterButton = (Button) findViewById(R.id.register);
        ForgotPassword = (TextView) findViewById(R.id.login_forgot_password);
        loadingBar=new ProgressDialog(this);
    }



    private void SendUserToMainActivity () {

        Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();

    }
    private void SendUserToRegisterActivity () {

        Intent RegisterIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(RegisterIntent);

    }
}

