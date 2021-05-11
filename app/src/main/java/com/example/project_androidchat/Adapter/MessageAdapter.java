package com.example.project_androidchat.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project_androidchat.Model.Messages;
import com.example.project_androidchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<Messages> allMessages;
    private String imageUrl;

    FirebaseUser fUser;

    // Глобальные переменные
    public static int MESSAGE_LEFT = 0;
    public static int MESSAGE_RIGHT = 1;

    public MessageAdapter(Context context, List<Messages> allMessages, String imageUrl) {
        this.context = context;
        this.allMessages = allMessages;
        this.imageUrl = imageUrl;
    }

    @Override
    // Создание одинаковых элементов на основе одного
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MESSAGE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.message_item_right, parent, false);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.message_item_left, parent, false);
        }
        return new ViewHolder(view);
    }

    // Устанавливаем значения в активити на основе класса-модели
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Messages messages = allMessages.get(position);
        holder.msg.setText(messages.getMessage());
        holder.Date.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", messages.getDate()));

        if(imageUrl.equals("default")) {
            holder.user_icon.setImageResource(R.drawable.avatar);
        } else {
            Glide.with(context).load(imageUrl).into(holder.user_icon);
        }

    }

    @Override
    public int getItemCount() {
        return allMessages.size();
    }

    // Класс, получающий значения полей
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Атрибуты каждого пользователя в приложении (Аватар, Имя)
        public TextView msg;
        public ImageView user_icon;
        public TextView Date;

        public ViewHolder(View itemView) {
            super(itemView);
            // Получение полей
            msg = itemView.findViewById(R.id.message);
            user_icon = itemView.findViewById(R.id.user_avatar);
            Date = itemView.findViewById(R.id.date);
        }
    }

    // Метод для определения положения сообщения для пользователя и оппонента
    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(allMessages.get(position).getSender().equals(fUser.getUid())) {
            return MESSAGE_RIGHT;
        }
        else {
            return MESSAGE_LEFT;
        }
    }
}
