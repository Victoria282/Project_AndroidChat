package com.example.project_androidchat.General;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.project_androidchat.Adapter.ViewPagerAdapter;
import com.example.project_androidchat.Fragments.ChatsFragment;
import com.example.project_androidchat.Fragments.ProfileFragment;
import com.example.project_androidchat.Fragments.UsersFragment;
import com.example.project_androidchat.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // Tab для переключения между вкладками 'пользователи' и 'сообщения'
    public TabLayout tabLayout;

    // Страница отображающая фрагменты -'пользователи' и 'сообщения'
    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Проверка, есть ли подключение к интернету
        if(!isOnline()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            MainActivity.this.finish();
        }

        // Получение атрибутов по id
        tabLayout = findViewById(R.id.Tab_layout);
        viewPager = findViewById(R.id.View_pager);

        // Создаем объект класса ViewPagerAdapter
        // Содержит логику подключения для фрагментов - сообщения и пользователи
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Добавляем две страницы ссообщениями и пользователями
        viewPagerAdapter.AddFragment(new ChatsFragment(), "Сообщения");
        viewPagerAdapter.AddFragment(new UsersFragment(), "Пользователи");
        viewPagerAdapter.AddFragment(new ProfileFragment(), "Профиль");

        // Устанавливаем адаптер
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    // Функция получения меню-выхода
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Осуществление выхода ==> переход в активити Login
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
        }
        return false;
    }
    // Метод для проверки - подключен ли пользователь к интернету
    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return true;
        }
    }
}
