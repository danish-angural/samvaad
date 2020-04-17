package com.example.whatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView requestsRecyclerList;
    private DatabaseReference UsersRef;
    public String myuserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        myuserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(myuserId).child("incoming_requests");

        requestsRecyclerList = findViewById(R.id.requests_RecyclerView);
        requestsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = findViewById(R.id.requests_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("message requests");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(UsersRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RequestsViewHolder holder, final int position, @NonNull Contacts model) {
                            holder.userName.setText(model.getName());
                            holder.userStatus.setText(model.getStatus());
                            if(model.getImage()!=null){
                                Picasso.get().load(model.getImage()).placeholder(R.drawable.avatar).into(holder.profileImage);
                            }

                        holder.okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               DatabaseReference target= FirebaseDatabase.getInstance().getReference().child("Users").child(getRef(position).getKey()).child("contacts").child(myuserId);
                               DatabaseReference myuser= FirebaseDatabase.getInstance().getReference().child("Users").child(myuserId);
                               copyFirebaseData(target, myuser);
                               target=FirebaseDatabase.getInstance().getReference().child("Users").child(myuserId).child("contacts").child(getRef(position).getKey());
                               myuser=FirebaseDatabase.getInstance().getReference().child("Users").child(getRef(position).getKey());
                               copyFirebaseData(target, myuser);
                               UsersRef.child(getRef(position).getKey()).removeValue();
                                Toast.makeText(RequestsActivity.this, "youre now friends!!! look up your contacts to start chatting", Toast.LENGTH_SHORT).show();
                            }
                        });
                        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UsersRef.child(getRef(position).getKey()).removeValue();
                                Toast.makeText(RequestsActivity.this, "request has been declined successfully", Toast.LENGTH_SHORT).show();

                            }
                        });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id = getRef(position).getKey();

                                Intent profileIntent = new Intent(RequestsActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileIntent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.usersdisplaylayout, viewGroup, false);
                        RequestsViewHolder viewHolder = new RequestsViewHolder(view);
                        return viewHolder;
                    }
                };
        requestsRecyclerList.setAdapter(adapter);

        adapter.startListening();
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView profileImage;
        Button okButton, rejectButton;

        public RequestsViewHolder(@NonNull final View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            okButton= itemView.findViewById(R.id.request_accept_btn);
            rejectButton=itemView.findViewById(R.id.request_cancel_btn);
            okButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.VISIBLE);

        }
    }

    private void copyFirebaseData(final DatabaseReference target, DatabaseReference myuser) {

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

                target.child("email").setValue(email);
                target.child("name").setValue(name);
                target.child("status").setValue(status);
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