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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity
{
    private Button RegisterButton, LoginButton;
    private EditText UserEmail, Password, MobileNumber, ConfirmPassword;
    private FirebaseAuth Mauth;
    private DatabaseReference RootRefference;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Mauth=FirebaseAuth.getInstance();
        RootRefference= FirebaseDatabase.getInstance().getReference();

        InitializeFeilds();

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            SendUserToLoginActivity();
            }
        });


        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {

        String email = UserEmail.getText().toString();
        String password = Password.getText().toString();
        String Confirmpassword = ConfirmPassword.getText().toString();
        String number = MobileNumber.getText().toString();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "please enter your email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "please enter your password...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(Confirmpassword)) {
            Toast.makeText(this, "please confirm your password...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "please enter your number...", Toast.LENGTH_SHORT).show();
        }

        if (!password.equals(Confirmpassword)) {
            Toast.makeText(this, "passwords don't match", Toast.LENGTH_SHORT).show();
        }

        else{
            loadingBar.setTitle("creating new account");
            loadingBar.setMessage("please wait while your account is being created");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();


            Mauth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                String currentUserID=Mauth.getCurrentUser().getUid();
                                RootRefference.child("Users").child(currentUserID).setValue("");
                                SendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();

                                loadingBar.dismiss();
                            }

                            else{
                                String message= task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "error:"+ message, Toast.LENGTH_SHORT).show();

                                loadingBar.dismiss();
                            }
                        }
                    });
        }

    }


        private void InitializeFeilds() {

        LoginButton = (Button) findViewById(R.id.login);
        MobileNumber = (EditText) findViewById(R.id.register_mobile_number);
        UserEmail = (EditText) findViewById(R.id.login_email);
        Password = (EditText) findViewById(R.id.register_password);
        RegisterButton = (Button) findViewById(R.id.register_button);
        ConfirmPassword = (EditText) findViewById(R.id.register_confirm_password);
        loadingBar=new ProgressDialog(this);
    }

    private void SendUserToLoginActivity () {

        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);

    }

    private void SendUserToMainActivity () {

        Intent MainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();

    }
}
