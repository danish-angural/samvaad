package com.example.whatsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootReff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        rootReff= FirebaseDatabase.getInstance().getReference();
        mToolbar= (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("samvaad");


        myViewPager= (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter= new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter((myTabsAccessorAdapter));

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(currentUser==null){
            sendUserToLoginActivity();
        }

        else {
            VerifyUserExistence();
            updateStatus("online");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    updateStatus("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateStatus("offline");
    }

    private void VerifyUserExistence() {
        String currentUserId=mAuth.getCurrentUser().getUid();
        String email=currentUser.getEmail();
        rootReff.child("Users").child(currentUser.getUid()).child("email").setValue(email);
        rootReff.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if ((!dataSnapshot.child("name").exists())&&(!dataSnapshot.child("phone").exists())){
                   sendUserToSettingsActivity();
                   Toast.makeText(MainActivity.this, "username and phone number can't be empty", Toast.LENGTH_SHORT).show();
                }


           }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId()==R.id.main_logout_option)
        {
            mAuth.signOut();
            sendUserToLoginActivity();
        }
        if (item.getItemId()==R.id.main_settings_option)
        {
           sendUserToSettingsActivity();
        }
        if (item.getItemId()==R.id.main_find_friends_option)
        {
            sendUserToFindFriendsactivity();

        }
        if (item.getItemId()==R.id.main_createGroup_option)
        {
            requestNewGroup();
        }
        if(item.getItemId()==R.id.main_requests_option)
        {
            sendUserToRequestsActivity();
        }

        return true;
    }

    private void sendUserToRequestsActivity()
    {
        Intent requestIntent=new Intent(MainActivity.this, RequestsActivity.class);
        requestIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(requestIntent);
        finish();
    }

    private void sendUserToFindFriendsactivity() {
        Intent FindFriendsIntent= new Intent(MainActivity.this,FindFriendsActivity.class);
        FindFriendsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(FindFriendsIntent);
        finish();
    }

    private void requestNewGroup() {

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name");

        final EditText groupNameField= new EditText(MainActivity.this);
        groupNameField.setHint("eg. punter log");
        builder.setView(groupNameField);
        builder.setPositiveButton("create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String GroupName= groupNameField.getText().toString();

                if(TextUtils.isEmpty(GroupName)){
                    Toast.makeText(MainActivity.this, "please enter the group name", Toast.LENGTH_SHORT).show();
                }

                else {
                    CreateNewGroup(GroupName);
                }
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }

        });
        builder.show();

    }

    private void CreateNewGroup(String GroupName) {
        rootReff.child("Groups").child(GroupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if ( task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Group created successfuly", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void sendUserToSettingsActivity()
    {
        Intent settingsIntent= new Intent(MainActivity.this,SettingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();

    }
    private void sendUserToLoginActivity()
    {
        Intent loginIntent= new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }


    private void updateStatus(String state){
        String last_seen;
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat format= new SimpleDateFormat("hh:mm");
        last_seen=format.format(calendar.getTime());
        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("last_seen").setValue(last_seen);
        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("state").setValue(state);

    }

}
