package com.example.whatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.mbms.StreamingServiceInfo;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String userIDrecieved, myUserId;
    private CircleImageView hisImage;
    private Button backButton, sendRequestButton;
    private TextView hisStatus, hisAbout, hisUsername, hisPhone, hisEmail;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth=FirebaseAuth.getInstance();
        userRef=FirebaseDatabase.getInstance().getReference();
        currentUser=mAuth.getCurrentUser();
        myUserId=currentUser.getUid();
        userIDrecieved=getIntent().getExtras().get("visit_user_id").toString();



        hisImage=(CircleImageView) findViewById(R.id.his_profile_image);
        backButton=(Button) findViewById(R.id.return_visit_button);
        sendRequestButton=(Button) findViewById(R.id.send_messsage_request);
        hisEmail=(TextView)findViewById(R.id.his_email);
        hisPhone=(TextView)findViewById(R.id.his_number);
        hisAbout=(TextView)findViewById(R.id.his_Description);
        hisStatus=(TextView)findViewById(R.id.his_status);
        hisUsername=(TextView)findViewById(R.id.his_username);


        RetreiveUserInfo();

    backButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mainIntent= new Intent(ProfileActivity.this, FindFriendsActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();

        }
    });
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestButton.setEnabled(false);

                sendChatRequest();

            }
        });
    }

    private void RetreiveUserInfo()
    {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userIDrecieved).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("image")){
                        String userImage= dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(userImage).placeholder((R.drawable.avatar)).into(hisImage);
                    }
                    if(dataSnapshot.hasChild("about")){
                        hisAbout.setText(dataSnapshot.child("about").getValue().toString());
                    }
                    if(dataSnapshot.hasChild("status")){
                        hisStatus.setText("status:"+dataSnapshot.child("status").getValue().toString());
                    }
                    if(dataSnapshot.hasChild("name")){
                        hisUsername.setText("@"+dataSnapshot.child("name").getValue().toString());
                    }
                    if(dataSnapshot.hasChild("phone")){
                        hisPhone.setText(dataSnapshot.child("phone").getValue().toString());
                    }
                    if(dataSnapshot.hasChild("email")){
                        hisEmail.setText(dataSnapshot.child("email").getValue().toString());
                    }
                    if (dataSnapshot.child("incoming_requests").hasChild(myUserId)||(myUserId.equals(userIDrecieved))||dataSnapshot.child("contacts").hasChild(myUserId)){
                        sendRequestButton.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendChatRequest() {
       copyFirebaseData();
        Toast.makeText(this, "request sent!", Toast.LENGTH_SHORT).show();

    }
    private void copyFirebaseData() {

        final DatabaseReference myuser = FirebaseDatabase.getInstance().getReference().child("Users").child(myUserId);

        myuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email="", name="", image="", status="", about="", uid="";
                email=dataSnapshot.child("email").getValue().toString();
                name=dataSnapshot.child("name").getValue().toString();
                if (dataSnapshot.hasChild("image")){
                image=dataSnapshot.child("image").getValue().toString();}
                if (dataSnapshot.hasChild("status")){
                    status=dataSnapshot.child("status").getValue().toString();}
                if (dataSnapshot.hasChild("about")){
                    about=dataSnapshot.child("about").getValue().toString();}
                uid=dataSnapshot.child("uid").getValue().toString();
                DatabaseReference target= FirebaseDatabase.getInstance().getReference().child("Users").child(userIDrecieved).child("incoming_requests").child(myUserId);

               target.child("email").setValue(email);
               target.child("name").setValue(name);
                target.setValue(status);
                target.child("image").setValue(image);
                target.child("about").setValue(about);
               target.child("uid").setValue(uid);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
