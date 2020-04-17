package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    private Button UpdateUsername, UpdatePassword, UpdateStatus, returnButton, submitButton, nextButton, sendOTP, confirmOTP, UpdateAbout;
    private CircleImageView UpdatePhoto;
    private EditText updateUsername, updatePassword, updateStatus,EnterPassword, OTP, phoneNumber, confirmEmail, confirmPassword, About;
    private TextView Username_text, Password_text, Status_text, Phone_text, display_username, about_text, phone;
    private String currentUserID;
    private DatabaseReference rootReff;
    private FirebaseUser CurrentUser;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private static final int GalleryPick=1;
    private StorageReference UserProfileImagesRefference;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        CurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        currentUserID= mAuth.getCurrentUser().getUid();
        rootReff= FirebaseDatabase.getInstance().getReference();

        UserProfileImagesRefference= FirebaseStorage.getInstance().getReference().child("Profile Images");
        initializefeilds();

        accessUserInfo();

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                CurrentUser.updatePhoneNumber(phoneAuthCredential);
                Toast.makeText(SettingsActivity.this, "phone number updated successfuly", Toast.LENGTH_SHORT).show();
                phone.setText(phoneNumber.getText().toString());
                OTP.setVisibility(View.GONE);
                submitButton.setVisibility(View.GONE);

            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                Toast.makeText(SettingsActivity.this, "Invalid Phone Number, Please enter correct phone number with your country code...", Toast.LENGTH_LONG).show();

                phoneNumber.setVisibility(View.VISIBLE);
                sendOTP.setVisibility(View.VISIBLE);

                OTP.setVisibility(View.GONE);
                submitButton.setVisibility(View.GONE);
            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token)
            {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;


                Toast.makeText(SettingsActivity.this, "Code has been sent, please check and verify...", Toast.LENGTH_SHORT).show();

                phoneNumber.setVisibility(View.GONE);
                sendOTP.setVisibility(View.GONE);

                OTP.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.VISIBLE);


            }
        };


        Username_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UpdateUsername.getVisibility()==View.GONE) {
                    UpdateUsername.setVisibility(View.VISIBLE);
                    updateUsername.setVisibility(View.VISIBLE);
                }
                else{
                    UpdateUsername.setVisibility(View.GONE);
                    updateUsername.setVisibility(View.GONE);
                }
            }
        });

        Password_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmEmail.getVisibility()==View.GONE) {
                    confirmEmail.setVisibility(View.VISIBLE);
                    confirmPassword.setVisibility(View.VISIBLE);
                    submitButton.setVisibility(View.VISIBLE);
                }
                else{
                    confirmEmail.setVisibility(View.GONE);
                    confirmPassword.setVisibility(View.GONE);
                    submitButton.setVisibility(View.GONE);
                }
            }
        });

        Status_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UpdateStatus.getVisibility()==View.GONE) {
                    UpdateStatus.setVisibility(View.VISIBLE);
                    updateStatus.setVisibility(View.VISIBLE);
                }
                else{
                    UpdateStatus.setVisibility(View.GONE);
                    updateStatus.setVisibility(View.GONE);
                }
            }
        });

        Phone_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EnterPassword.getVisibility()==View.GONE) {
                    phone.setVisibility(View.VISIBLE);
                    EnterPassword.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);
                }
                else{
                    phone.setVisibility(View.GONE);
                    EnterPassword.setVisibility(View.GONE);
                    nextButton.setVisibility(View.GONE);
                }
            }
        });

        about_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UpdateAbout.getVisibility()==View.GONE) {
                    UpdateAbout.setVisibility(View.VISIBLE);
                    About.setVisibility(View.VISIBLE);
                }
                else{
                    UpdateAbout.setVisibility(View.GONE);
                    About.setVisibility(View.GONE);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyuserforpassword();
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToMainActivity();
            }
        });



        UpdateUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUsername();
            }
        });
        UpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_update_password();
            }
        });
        UpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateStatus();
            }
        });
        UpdateAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAbout();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyuserforpassword2();
            }
        });
        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(phoneNumber.getText())){
                    Toast.makeText(SettingsActivity.this, "enter valid phone number", Toast.LENGTH_SHORT).show();
                }
                else{
                    OTP.setVisibility(View.VISIBLE);
                    confirmOTP.setVisibility(View.VISIBLE);
                    phoneNumber.setVisibility(View.GONE);
                    sendOTP.setVisibility(View.GONE);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber.getText().toString(), 60, TimeUnit.SECONDS, SettingsActivity.this, callbacks);
                }
            }
        });

        UpdatePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent galleryIntent= new Intent();
               galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
               galleryIntent.setType("image/*");
               startActivityForResult(galleryIntent, GalleryPick);
            }

        });





    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            CropImage.activity(ImageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("setting profile image");
                loadingBar.setMessage("please wait");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                final Uri resultUri = result.getUri();

                final StorageReference filePath = UserProfileImagesRefference.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override

                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                            @Override

                            public void onSuccess(Uri uri) {


                                final String downloadUrl = uri.toString();

                                rootReff.child("Users").child(currentUserID).child("image").setValue(downloadUrl)

                                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                                            @Override

                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    Toast.makeText(SettingsActivity.this, "Profile image stored to firebase database successfully.", Toast.LENGTH_SHORT).show();

                                                    loadingBar.dismiss();

                                                } else {

                                                    String message = task.getException().getMessage();

                                                    Toast.makeText(SettingsActivity.this, "Error Occurred..." + message, Toast.LENGTH_SHORT).show();

                                                    loadingBar.dismiss();

                                                }

                                            }

                                        });

                            }

                        });

                    }
                });
            }
        }
    }




    private void verifyuserforpassword2() {
       String email=CurrentUser.getEmail().toString();
       String password=EnterPassword.getText().toString();
       AuthCredential credential= EmailAuthProvider.getCredential(email, password);
       CurrentUser.reauthenticate(credential)
               .addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){
                           EnterPassword.setVisibility(View.GONE);
                           nextButton.setVisibility(View.GONE);
                           sendOTP.setVisibility(View.VISIBLE);
                           phoneNumber.setVisibility(View.VISIBLE);

                       }
                       else{
                           Toast.makeText(SettingsActivity.this, "could not authenticate", Toast.LENGTH_SHORT).show();
                       }
                   }
               });

    }

    private void verifyuserforpassword() {
        String email=confirmEmail.getText().toString();
        String oldpassword= confirmPassword.getText().toString();
        AuthCredential credential=EmailAuthProvider.getCredential(email, oldpassword);
        CurrentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            confirmEmail.setVisibility(View.GONE);
                            confirmPassword.setVisibility(View.GONE);
                            submitButton.setVisibility(View.GONE);
                            UpdatePassword.setVisibility(View.VISIBLE);
                            updatePassword.setVisibility(View.VISIBLE);
                        }
                        else {
                            Toast.makeText(SettingsActivity.this, "authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void confirm_update_password() {
        String newpassword=updatePassword.getText().toString();
        HashMap<String, Object> profileMap=new HashMap<>();
        profileMap.put("uid", currentUserID);
        profileMap.put("password",newpassword);
        if (TextUtils.isEmpty(newpassword)){
            Toast.makeText(this, "please enter your password", Toast.LENGTH_SHORT).show();
        }
        else{
            rootReff.child("Users").child(currentUserID).updateChildren(profileMap);
            CurrentUser.updatePassword(newpassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SettingsActivity.this, "password changed successfully", Toast.LENGTH_SHORT).show();
                                SendUserToMainActivity();
                            }
                            else {
                                Toast.makeText(SettingsActivity.this, "something went wring, please try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void accessUserInfo() {
        rootReff.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            if (dataSnapshot.hasChild("image")){
                                Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(UpdatePhoto);
                            }

                            if (dataSnapshot.hasChild("name")){
                                String accessUsername= dataSnapshot.child("name").getValue().toString();
                                updateUsername.setText(accessUsername);
                                display_username.setText("@"+accessUsername);

                            }
                            if (dataSnapshot.hasChild("passowrd")){
                                String accessPassword= dataSnapshot.child("password").getValue().toString();
                                updatePassword.setText(accessPassword);
                            }
                            if (dataSnapshot.hasChild("photo")){
                                String accessPhoto= dataSnapshot.child("photo").getValue().toString();

                            }
                            if (dataSnapshot.hasChild("status")){
                                String accessStatus= dataSnapshot.child("status").getValue().toString();
                                updateStatus.setText(accessStatus);
                            }
                            if(dataSnapshot.hasChild("phone")){
                                String accessPhone= dataSnapshot.child("phone").getValue().toString();
                                phoneNumber.setText(accessPhone);
                            }
                            if(dataSnapshot.hasChild("about")){
                                String accessAbout= dataSnapshot.child("about").getValue().toString();
                                About.setText(accessAbout);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    private void UpdateUsername() {
        String uname = updateUsername.getText().toString();

        if (TextUtils.isEmpty(uname)){
            Toast.makeText(this, "please enter your username", Toast.LENGTH_SHORT).show();
        }

        else{
            HashMap<String, Object> profileMap=new HashMap<>();
            profileMap.put("name", uname);
           DatabaseReference reff=rootReff.child("Users").child(currentUserID);
           reff.updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Toast.makeText(SettingsActivity.this, "profile updated successfully", Toast.LENGTH_SHORT).show();
                            }

                            else{
                                String message=task.getException().toString();
                                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }


    private void UpdateStatus() {
        String status = updateStatus.getText().toString();

        if (TextUtils.isEmpty(status)){
            Toast.makeText(this, "please enter your Status", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String, Object> profileMap=new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("status", status);
            rootReff.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Toast.makeText(SettingsActivity.this, "profile updated successfully", Toast.LENGTH_SHORT).show();
                            }

                            else{
                                String message=task.getException().toString();
                                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }
    private void UpdateAbout() {
        String about = About.getText().toString();

        if (TextUtils.isEmpty(about)){
            Toast.makeText(this, "please enter your Description", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String, Object> profileMap=new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("about", about);
            rootReff.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Toast.makeText(SettingsActivity.this, "profile updated successfully", Toast.LENGTH_SHORT).show();
                            }

                            else{
                                String message=task.getException().toString();
                                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }






    private void initializefeilds() {

        updateUsername=(EditText) findViewById(R.id.set_username);
        UpdateUsername=(Button) findViewById(R.id.change_username_button);
        UpdateAbout=(Button) findViewById(R.id.change_about_button);
        updatePassword=(EditText) findViewById(R.id.set_password);
        UpdatePassword=(Button) findViewById(R.id.change_password_button);
        updateStatus=(EditText) findViewById(R.id.set_status);
        UpdateStatus=(Button) findViewById(R.id.change_status_button);
        UpdatePhoto=(CircleImageView) findViewById((R.id.set_profile_image));
        returnButton=(Button) findViewById(R.id.return_button);
        Username_text=(TextView) findViewById(R.id.username_text);
        Password_text=(TextView) findViewById(R.id.password_text);
        Status_text=(TextView) findViewById(R.id.status_text);
        display_username=(TextView) findViewById(R.id.display_username);
        Phone_text=(TextView) findViewById(R.id.phone_text);
        EnterPassword=(EditText) findViewById(R.id.confirm_password2);
        nextButton=(Button) findViewById(R.id.confirm_password_button);
        phoneNumber=(EditText) findViewById(R.id.phone_number);
        sendOTP=(Button) findViewById(R.id.send_OTP);
        OTP=(EditText) findViewById(R.id.OTP);
        confirmOTP=(Button) findViewById(R.id.submit);
        confirmEmail=(EditText) findViewById(R.id.confirm_email);
        confirmPassword=(EditText) findViewById(R.id.confirm_password);
        submitButton=(Button) findViewById(R.id.submit_confirmation);
        About=(EditText) findViewById(R.id.set_about);
        about_text=(TextView) findViewById(R.id.about_text);
        loadingBar= new ProgressDialog(this);
        phone=(TextView)findViewById(R.id.phone);
    }

    private void SendUserToMainActivity () {

        Intent MainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();

    }
}
