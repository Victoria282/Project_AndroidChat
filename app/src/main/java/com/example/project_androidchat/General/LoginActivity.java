package com.example.project_androidchat.General;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_androidchat.Other.BackgroundChanges;
import com.example.project_androidchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {
    // Поля email и пароль для авторизации
    EditText Email, Password;

    // Кнопка входа в учетную запись
    Button AuthorizationButton;

    // Подключение к Firebase - аутентификация
    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    // Ссылка на страницу регистрации
    TextView LinkTextView;

    // Окно ожидания
    private ProgressDialog progressDialog;

    // Проверка авторизован пользователь или нет
    protected void onStart() {
        super.onStart();
        // Получаем текущего пользователя
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Если пользователь авторизован, переводим на страницу с сообщениями
        if(firebaseUser != null) {
            Intent intent = new Intent(LoginActivity. this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        // Проверка подключен ли пользователь к интернету
        if(!isOnline()) {
            Toast.makeText(this, "Нет соединения с интернетом..", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Смена фона в зависимости от времени суток
        Calendar background = Calendar.getInstance();
        int timeOfDay = background.get(Calendar.HOUR_OF_DAY);
        LinearLayout ThisLayout = findViewById(R.id.LoginActivity);
        new BackgroundChanges(timeOfDay, ThisLayout);

        // Получение полей по id
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        AuthorizationButton = findViewById(R.id.log_in);
        progressDialog = new ProgressDialog(this);
        LinkTextView = findViewById(R.id.LinkSignUp);

        // Аутентификация пользователя
        auth = FirebaseAuth.getInstance();

        // При нажатии на ссылку регистрации, переходим на активити с регистрацией пользователя
        LinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity. this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Действия при нажатии на кнопку аутентификации
        AuthorizationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получение значений почты и пароля и преобразования к типу String
                String email_source = Email.getText().toString();
                String password_source = Password.getText().toString();

                // Проверка на непустые поля и валидация почты
                if(TextUtils.isEmpty(email_source)) {
                    Email.setError("Введите почту!");
                }
                else if(TextUtils.isEmpty(password_source)) {
                    Password.setError("Введите пароль!");
                }
                else if(!isValidEmail(email_source)) {
                    Email.setError("Неправильный email!");
                }
                // Если данные корректны, логиним пользователя
                else {
                    Login(email_source, password_source);
                }
            }
        });
    }
    // Функция для авторизации пользователя
    public void Login(String email_source, String password_source) {
        // Вход через email и пароль
        auth.signInWithEmailAndPassword(email_source, password_source)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            // Переход на вкладку с чатами
                            Intent intent = new Intent(LoginActivity. this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Неверный email или пароль!", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
    // Проверка email на корректность
    private Boolean isValidEmail(CharSequence target) {
        return(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    // Проверка подключения к интернету
    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        }
        else {
            return true;
        }
    }
}