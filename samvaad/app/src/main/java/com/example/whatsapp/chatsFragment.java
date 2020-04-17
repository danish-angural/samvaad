package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class chatsFragment extends Fragment {
    private View ContactsView;
    private RecyclerView myContactList;

    private DatabaseReference ContactsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public chatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ContactsView = inflater.inflate(R.layout.fragment_contact, container, false);

        myContactList = ContactsView.findViewById(R.id.chatrecyclerView);
        myContactList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();


        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ContactsRef =UsersRef.child(mAuth.getCurrentUser().getUid()).child("contacts");
        ContactsRef.orderByChild("last_message");


        return ContactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ContactsRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, contactFragment.ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, contactFragment.ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final contactFragment.ContactsViewHolder holder, final int position, @NonNull Contacts model) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();
                        Intent chatIntent= new Intent(getContext(), ChatActivity.class);
                        chatIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(chatIntent);

                    }
                });
                String usersIDs = getRef(position).getKey();

                UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image")) {
                            String userImage = dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(userImage).placeholder(R.drawable.avatar).into(holder.profileImage);
                        }
                        String profileName = dataSnapshot.child("name").getValue().toString();
                        String profileStatus = dataSnapshot.child("status").getValue().toString();
                        holder.userName.setText(profileName);
                        holder.userStatus.setText(profileStatus);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public contactFragment.ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.usersdisplaylayout, viewGroup, false);
                contactFragment.ContactsViewHolder viewholder = new contactFragment.ContactsViewHolder(view);
                return viewholder;
            }
        };

        myContactList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView profileImage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }

}
