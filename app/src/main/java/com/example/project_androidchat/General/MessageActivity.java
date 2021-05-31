package com.example.project_androidchat.General;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project_androidchat.Adapter.MessageAdapter;
import com.example.project_androidchat.Model.Messages;
import com.example.project_androidchat.Model.Users;
import com.example.project_androidchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    // Аватар и имя собеседника
    TextView nameOfUser;
    ImageView picOfUser;
    ImageButton imgBtn;

    // Подключение к firebase
    FirebaseUser user;
    DatabaseReference reference;

    // Получение данных с другой активити
    Intent intent;

    // Поле для ввода сообщений и кнопка отправки сообщений
    EditText editText;
    ImageButton sendMessageButton;

    // Прокрутка сообщений
    RecyclerView recyclerView;

    MessageAdapter messageAdapter;
    List<Messages> lMessages;

    public FirebaseAuth auth;
    public FirebaseUser firebaseUser;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getSupportActionBar().hide();

        // Стилизация (аватар, имя, иконка выхода)
        picOfUser = findViewById(R.id.picOfUser);
        nameOfUser = findViewById(R.id.nameOfUser);
        imgBtn = findViewById(R.id.back_btn);

        // Ключевые поля
        recyclerView = findViewById(R.id.RecyclerViewMessages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        LinearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(LinearLayoutManager);

        editText = findViewById(R.id.EditMessage);
        sendMessageButton = findViewById(R.id.send_btn);

        // Кнопка выхода из чата с другим пользователем
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity. this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        picOfUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity. this, AboutUserActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        intent = getIntent();
        // Данные о собеседнике (Из активити UserAdapter)
        String userId = intent.getStringExtra("userId");

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Звук отправленного смс
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound_send);

        // Обработчик кнопки отправки смс
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString().trim();
                long date = new Date().getTime();
                // Если пустое тело - предупреждение
                if(message.equals("")) {
                    Toast.makeText(MessageActivity.this, "Введите сообщение..", Toast.LENGTH_LONG).show();
                }
                else {
                    // В качестве параметров id собеседника, текущего пользователя и тело сообщения
                    SendMessage(user.getUid(), userId, message, date);
                    mp.start();
                }
                // Очистить поле ввода сообщения
                editText.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        // Получить url пользователя
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                String userId = firebaseUser.getUid();
                // ТЕСТ ! вывод id пользователя !
                nameOfUser.setText(user.getUsername());
                if(user.getImageUrl().equals("default")) {
                    picOfUser.setImageResource(R.drawable.avatar);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(picOfUser);
                }
                ShowMessage(user.getUserId(), userId, user.getImageUrl());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void SendMessage(String user_id, String opponent_id, String message, long date) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", user_id);
        hashMap.put("receiver", opponent_id);
        hashMap.put("message", message);
        hashMap.put("date",  date);
        databaseReference.child("Chats").push().setValue(hashMap);
    }
    private void ShowMessage(String userId, String opponentId, String imageUrl) {
        lMessages = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lMessages.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Messages messages = snapshot.getValue(Messages.class);

                    if(messages.getReceiver().equals(opponentId) && messages.getSender().equals(userId) || messages.getReceiver().equals(userId) && messages.getSender().equals(opponentId)) {
                        lMessages.add(messages);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, lMessages, imageUrl);
                    recyclerView.setAdapter(messageAdapter);
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    public void CheckStatusUser(String status) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckStatusUser("online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        CheckStatusUser("offline");
    }
}