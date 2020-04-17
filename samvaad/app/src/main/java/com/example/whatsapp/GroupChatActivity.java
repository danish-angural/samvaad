package com.example.whatsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.*;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {

    private RelativeLayout mToolbar;
    private Button backButton;
    private ImageButton sendMessage;
    private TextView message_input, username_display;
    private FirebaseAuth mAuth;
    private DatabaseReference Group_referrence, my_referrence;
    private int message_count;
    private RecyclerView GroupchatRecyclerList;
    private String myname, Group_name, his_dp;
    private CircleImageView user_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        initialize();
        mAuth=FirebaseAuth.getInstance();
        my_referrence= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        final String Group_name=getIntent().getExtras().get("groupName").toString();
        Group_referrence =FirebaseDatabase.getInstance().getReference().child("Groups").child(Group_name);
        username_display.setText(Group_name);
        my_referrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    myname=dataSnapshot.child("name").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(message_input.getText())){
                    Toast.makeText(GroupChatActivity.this, "enter message first", Toast.LENGTH_SHORT).show();
                }
                else{
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");

                    String time = format.format( new Date()   );
                    String Message= message_input.getText().toString();
                    HashMap<String, Object> messageMap=new HashMap<>();
                    messageMap.put("datetime", time);
                    messageMap.put("message", Message);
                    messageMap.put("sender",myname);
                    int i = (int) new Date().getTime();
                    String s=String.valueOf(i);
                    Group_referrence.child("last_message").setValue(i);
                    Group_referrence.child(s).child("messages").updateChildren(messageMap);
                    GroupchatRecyclerList.smoothScrollToPosition(GroupchatRecyclerList.getAdapter().getItemCount());
                    message_input.setText("");
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent= new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
        FirebaseRecyclerOptions<Messages> options =
                new FirebaseRecyclerOptions.Builder<Messages>()
                        .setQuery(Group_referrence.child("messages"), Messages.class)
                        .build();

        final FirebaseRecyclerAdapter<Messages, GroupchatViewHolder> adapter =
                new FirebaseRecyclerAdapter<Messages, GroupchatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final GroupchatViewHolder holder, final int position, @NonNull Messages model) {
                        holder.sender.setText(model.getSender());
                        holder.message.setText(model.getMessage());
                        holder.datetime.setText(model.getDatetime().toString());
                        if(!holder.sender.getText().toString().equals(myname)){
                            holder.l.setGravity(Gravity.LEFT);
                        }
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder builder=new AlertDialog.Builder(GroupChatActivity.this, R.style.AlertDialog);
                                builder.setTitle("delete message?");
                                builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getRef(holder.getAdapterPosition()).removeValue();

                                    }
                                });

                                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }

                                });
                                builder.show();
                                return true;
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public GroupchatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_layout, viewGroup, false);
                        GroupchatViewHolder viewHolder = new GroupchatViewHolder(view);
                        return viewHolder;
                    }
                };
        GroupchatRecyclerList.setAdapter(adapter);
        Objects.requireNonNull(GroupchatRecyclerList.getAdapter()).notifyDataSetChanged();

        adapter.startListening();




        Group_referrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                message_count=(int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        GroupchatRecyclerList.scrollToPosition(GroupchatRecyclerList.getAdapter().getItemCount());


    }

    private void getUserInfo() {

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void initialize() {
        mToolbar=(RelativeLayout) findViewById(R.id.chat_toolbar);
        backButton=(Button) findViewById(R.id.return_chat_buton);
        message_input=(TextView) findViewById(R.id.input_message);
        sendMessage=(ImageButton) findViewById(R.id.send_message_btn);
        GroupchatRecyclerList=(RecyclerView) findViewById(R.id.chatrecyclerView);
        GroupchatRecyclerList.setLayoutManager(new LinearLayoutManager(this));
        username_display=(TextView) findViewById(R.id.username);
        user_image=(CircleImageView) findViewById(R.id.profileImage);


    }

    public static class GroupchatViewHolder extends RecyclerView.ViewHolder {

        TextView sender, message, datetime;
        RelativeLayout l;

        public GroupchatViewHolder(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.sender);
            message=itemView.findViewById(R.id.message);
            datetime=itemView.findViewById(R.id.time);
            l=itemView.findViewById(R.id.layout);
        }
    }
}
