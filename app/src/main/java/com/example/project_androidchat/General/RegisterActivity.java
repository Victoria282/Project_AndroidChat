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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    // Поле для ввода имени, почты, пароля
    private EditText userName, Email, Password, ConfirmPassword;

    // Кнопка регистрации
    public Button RegisterButton;

    // Подключение к Firebase - аутентификация и база данных, текущий пользователь
    public FirebaseAuth auth;
    public DatabaseReference myRef;
    public FirebaseUser firebaseUser;

    // Окно ожидания
    public ProgressDialog progressDialog;

    // Ссылка на активити с авторизацией
    public TextView LinkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Получаем текущую дату и linearLayout, для изменения фона
        Calendar background = Calendar.getInstance();
        int timeOfDay = background.get(Calendar.HOUR_OF_DAY);
        LinearLayout ThisLayout = findViewById(R.id.RegisterActivity);
        new BackgroundChanges(timeOfDay, ThisLayout);

        // Проверка подключен ли пользователь к интернету
        if(!isOnline()) {
            Toast.makeText(this, "Нет соединения с интернетом..", Toast.LENGTH_LONG).show();
        }

        // Инициализация виджетов по id
        userName = findViewById(R.id.name);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        ConfirmPassword = findViewById(R.id.confirm_password);

        LinkTextView = findViewById(R.id.LinkSignIn);
        RegisterButton = findViewById(R.id.register);
        progressDialog = new ProgressDialog(this);

        // Подключение к Firebase для регистрации пользователя
        auth = FirebaseAuth.getInstance();

        // При нажатии на ссылку "Войти", переходим на активити с аутентификацией пользователя
        LinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity. this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Действия при нажатии на кнопку регистрации
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получение значений полей, преобразование к строковому типу
                String username_source = userName.getText().toString();
                String email_source = Email.getText().toString();
                String password_source = Password.getText().toString();
                String password_confirm_source = ConfirmPassword.getText().toString();

                // Проверка на непустые поля и валидация email
                if(TextUtils.isEmpty(email_source)) {
                    Email.setError("Введите email!");
                }
                else if(TextUtils.isEmpty(password_source)) {
                    Password.setError("Введите пароль!");
                }
                else if(password_source.length() < 6) {
                    Password.setError("Пароль не меньше 6 символов!");
                }
                else if(TextUtils.isEmpty(username_source)) {
                    userName.setError("Введите имя пользователя!");
                }
                else if(!isValidEmail(email_source)) {
                    Email.setError("Неправильный email!");
                }
                else if(!password_source.equals(password_confirm_source)) {
                    ConfirmPassword.setError("Разные пароли!");
                }
                else {
                    // В случае корректности данных переходим в функцию регистрации
                    Register(username_source, email_source, password_source);
                }
            }
        });
    }

    // Проверка email на корректность
    private Boolean isValidEmail(CharSequence target) {
        return(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    // Функция регистрации пользователей
    public void Register(final String username_source, String email_source, String password_source) {
        // Окно ожидания
        progressDialog.setMessage("Пожалуйста, подождите...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        // Регистрируем пользователя почтой и паролем
        auth.createUserWithEmailAndPassword(email_source, password_source)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            // Добавляем колонку с id пользователем
                            myRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            // Структура для хранения id и имени юзера
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("userId", userId);
                            hashMap.put("userName", username_source);
                            hashMap.put("imageUrl", "default");
                            hashMap.put("status", "offline");

                            myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(RegisterActivity.this, "Регистрация прошла успешно", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                        // В случае ошибки соответствующее сообщение
                        else {
                            Toast.makeText(RegisterActivity.this, "Ошибка регистрации!", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
    // Проверка на подключение к интернету
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