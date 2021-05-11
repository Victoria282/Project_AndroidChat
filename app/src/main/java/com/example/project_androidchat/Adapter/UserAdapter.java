package com.example.project_androidchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project_androidchat.General.MessageActivity;
import com.example.project_androidchat.Model.Users;
import com.example.project_androidchat.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    // Список на основе класса-модели Users (userId, userName, imageUrl)
    private List<Users> allUsers;

    public UserAdapter(Context context, List<Users> allUsers) {
        this.context = context;
        this.allUsers = allUsers;
    }
    @NonNull
    @Override
    // onCreateViewHolder() создает новый объект ViewHolder всякий раз, когда RecyclerView нуждается в этом.
    // Это тот момент, когда создаётся layout строки списка, передается объекту ViewHolder, и
    // каждый дочерний view-компонент может быть найден и сохранен.
    // * Создание одинаковых элементов на основе одного *
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // генерируем элементы на основе user.xml (содержит имя и аватарку)
        View view = LayoutInflater.from(context).inflate(R.layout.user, parent, false);
        return new UserAdapter.ViewHolder(view);
    }
    @Override
    // onBindViewHolder() принимает объект ViewHolder и устанавливает необходимые данные для соответствующей
    // строки во view-компоненте.
    //  * Устанавливаем значения в активити на основе класса-модели *
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = allUsers.get(position);
        holder.userName.setText(users.getUsername());
        holder.userId.setText(users.getUserId());
        // Если id изображение дефолт -> устанавливаем иконку из drawable (стандартную)
        if(users.getImageUrl().equals("default")) {
            holder.imageView.setImageResource(R.drawable.avatar);
        }
        // Иначе, с помощью библиотеки Glide загружаем новую иконку
        else {
            Glide.with(context).load(users.getImageUrl()).into(holder.imageView);
        }

        // Обработчик событий нажатия на чат с определенным пользователем
        // Создание активности для общения с пользователем
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход в активити с сообщениями
                Intent intent = new Intent(context, MessageActivity.class);
                // Передача данных пользователя в другую активити
                intent.putExtra("userId", users.getUserId());
                intent.putExtra("userName", users.getUsername());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return allUsers.size();
    }

    // Класс, получающий значения полей
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Атрибуты каждого пользователя в приложении (Аватар, Имя)
        public TextView userName;
        public ImageView imageView;
        public TextView userId;

        public ViewHolder(View itemView) {
            super(itemView);
            // Получение полей из user.xml
            userName = itemView.findViewById(R.id.textViewName);
            imageView = itemView.findViewById(R.id.imageViewUser);
            userId = itemView.findViewById(R.id.textViewId);
        }
    }
}
