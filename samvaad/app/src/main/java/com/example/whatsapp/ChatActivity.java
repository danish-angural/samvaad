package com.example.whatsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private RelativeLayout mToolbar;
    private Button backButton;
    private ImageButton sendMessage;
    private TextView message_input, username_display, last_seen;
    private FirebaseAuth mAuth;
    private DatabaseReference his_referrence, my_referrence, my_message_referrence, his_message_referrence;
    private int message_count;
    private RecyclerView chatRecyclerList;
    private String myname, Myname, his_name, his_Name, his_dp;
    private CircleImageView user_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initialize();
        mAuth=FirebaseAuth.getInstance();
        my_referrence= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        final String his_ID=getIntent().getExtras().get("visit_user_id").toString();
        his_referrence=FirebaseDatabase.getInstance().getReference().child("Users").child(his_ID);
        my_message_referrence=my_referrence.child("chats").child(his_ID.toString());
        his_message_referrence=his_referrence.child("chats").child(mAuth.getCurrentUser().getUid().toString());




        my_referrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    myname=dataSnapshot.child("name").getValue().toString();
                }
                else{System.out.println("doesnt exixt");}
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        his_referrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                his_name=dataSnapshot.child("name").getValue().toString();
                if(dataSnapshot.child("status").getValue().toString().equals("online")){
                last_seen.setText("online");}
                else if (dataSnapshot.hasChild("last_seen")){
                    last_seen.setText(dataSnapshot.child("last_seen").getValue().toString());}
                username_display.setText(his_name);
                if (dataSnapshot.hasChild("image")){
                    his_dp=dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(his_dp).placeholder(R.drawable.avatar).into(user_image);
                }
                mToolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent=new Intent(ChatActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("visit_user_id", his_ID);
                        startActivity(profileIntent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(message_input.getText())){
                    Toast.makeText(ChatActivity.this, "enter message first", Toast.LENGTH_SHORT).show();
                }
                else{
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");

                    String time = format.format( new Date()   );
                    String Message= message_input.getText().toString();
                    HashMap<String, Object> messageMap=new HashMap<>();
                    int i = (int) new Date().getTime();
                    messageMap.put("datetime", time);
                    messageMap.put("message", Message);
                    messageMap.put("sender",myname);
                    String s=String.valueOf(i);
                    my_message_referrence.child("messages").child(s).updateChildren(messageMap);
                    his_message_referrence.child("messages").child(s).updateChildren(messageMap);
                    my_message_referrence.child("last_message").setValue(i);
                    chatRecyclerList.smoothScrollToPosition(chatRecyclerList.getAdapter().getItemCount());
                    message_input.setText("");
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent= new Intent(ChatActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
        FirebaseRecyclerOptions<Messages> options =
                new FirebaseRecyclerOptions.Builder<Messages>()
                        .setQuery(my_message_referrence.child("messages"), Messages.class)
                        .build();

        final FirebaseRecyclerAdapter<Messages, chatViewHolder> adapter =
                new FirebaseRecyclerAdapter<Messages, chatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatActivity.chatViewHolder holder, final int position, @NonNull Messages model) {
                        holder.sender.setText(model.getSender());
                        holder.message.setText(model.getMessage());
                        holder.datetime.setText(model.getDatetime());
                        if(!holder.sender.getText().toString().equals(myname)){
                           holder.l.setGravity(Gravity.LEFT);
                        }
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this, R.style.AlertDialog);
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
                    public ChatActivity.chatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_layout, viewGroup, false);
                        ChatActivity.chatViewHolder viewHolder = new ChatActivity.chatViewHolder(view);
                        return viewHolder;
                    }
                };
        chatRecyclerList.setAdapter(adapter);
        Objects.requireNonNull(chatRecyclerList.getAdapter()).notifyDataSetChanged();

        adapter.startListening();




        my_message_referrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                message_count=(int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chatRecyclerList.scrollToPosition(chatRecyclerList.getAdapter().getItemCount());


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
        chatRecyclerList=(RecyclerView) findViewById(R.id.chatrecyclerView);
        chatRecyclerList.setLayoutManager(new LinearLayoutManager(this));
        username_display=(TextView) findViewById(R.id.username);
        user_image=(CircleImageView) findViewById(R.id.profileImage);
        last_seen=(TextView) findViewById(R.id.custom_user_last_seen);


    }

    public static class chatViewHolder extends RecyclerView.ViewHolder {

       TextView sender, message, datetime;
       RelativeLayout l;

        public chatViewHolder(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.sender);
            message=itemView.findViewById(R.id.message);
            datetime=itemView.findViewById(R.id.time);
            l=itemView.findViewById(R.id.layout);
        }
    }
}
