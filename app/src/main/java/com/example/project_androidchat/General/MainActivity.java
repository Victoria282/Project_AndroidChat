package com.example.project_androidchat.General;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.project_androidchat.Adapter.ViewPagerAdapter;
import com.example.project_androidchat.Fragments.ProfileFragment;
import com.example.project_androidchat.Fragments.UsersFragment;
import com.example.project_androidchat.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
public class MainActivity extends AppCompatActivity {

    // Tab для переключения между вкладками 'пользователи', 'профиль' и 'сообщения'
    public TabLayout tabLayout;

    // Страница отображающая фрагменты'
    public ViewPager viewPager;

    // Подключение к firebase
    DatabaseReference reference;
    FirebaseUser user;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Проверка, есть ли подключение к интернету
        if(!isOnline()) {
            Toast.makeText(this, "Нет соединения с интернетом!", Toast.LENGTH_LONG).show();
            MainActivity.this.finish();
        }

        // Получение атрибутов по id
        tabLayout = findViewById(R.id.Tab_layout);
        viewPager = findViewById(R.id.View_pager);

        // Создаем объект класса ViewPagerAdapter
        // Содержит логику подключения для фрагментов - сообщения и пользователи
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Добавляем две страницы ссообщениями и пользователями
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
        if (item.getItemId() == R.id.logout) {
            CheckStatusUser("offline");
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

    public void CheckStatusUser(String status) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            // Обновить статус
            reference.updateChildren(hashMap);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        CheckStatusUser("online");
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
    protected void onDestroy() {
        super.onDestroy();
        CheckStatusUser("offline");
    }
}


