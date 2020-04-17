package com.example.whatsapp;

import android.content.Intent;
import android.support.annotation.DrawableRes;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference UsersRef;
    private String currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Button send_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        send_back=(Button) findViewById(R.id.return_ff_button);
        send_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainIntent=new Intent(FindFriendsActivity.this, MainActivity.class);
                startActivity(MainIntent);
            }
        });
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FindFriendsRecyclerList = findViewById(R.id.recyclerView);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Find Friends");
    }

    @Override
    protected void onStart() {
        super.onStart();



        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(UsersRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FindFriendViewHolder holder, final int position, @NonNull Contacts model) {
                        holder.userName.setText(model.getName());
                        holder.userStatus.setText(model.getStatus());
                        UsersRef.child(getRef(holder.getAdapterPosition()).getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(currentUserId.equals(getRef(holder.getAdapterPosition()).getKey())){
                                    holder.itemView.setVisibility(View.GONE);
                                }
                                if ((!dataSnapshot.child("incoming_requests").hasChild(currentUserId))&&( ! dataSnapshot.child("contacts").hasChild(currentUserId))){
                                    holder.request_button.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.avatar).into(holder.profileImage);
                        holder.request_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference target, myid;
                                target=UsersRef.child(getRef(holder.getAdapterPosition()).getKey()).child("incoming_requests").child(currentUserId);
                                myid=UsersRef.child(currentUserId);
                                copyFirebaseData(target, myid);
                                Toast.makeText(FindFriendsActivity.this, "request sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id = getRef(holder.getAdapterPosition()).getKey();

                                Intent profileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileIntent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.usersdisplaylayout, viewGroup, false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                        return viewHolder;
                    }
                };
        FindFriendsRecyclerList.setAdapter(adapter);

        adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView profileImage;
        Button request_button;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            request_button=itemView.findViewById(R.id.request_accept_btn);
            request_button.setText("send request");
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