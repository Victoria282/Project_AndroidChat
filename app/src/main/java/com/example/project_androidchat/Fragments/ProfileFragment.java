package com.example.project_androidchat.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.project_androidchat.General.MainActivity;
import com.example.project_androidchat.General.MessageActivity;
import com.example.project_androidchat.Model.Users;
import com.example.project_androidchat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    public ProfileFragment() { }
    // Имя и аватар пользователя
    TextView ProfileUserName;
    ImageView ProfilePic;
    TextView ProfileId;
    Button uploadButton;
    Button chooseButton;

    // Путь к изображению на телефоне
    private Uri filePath;

    // Код запроса
    private final int PICK_IMAGE_REQUEST = 1;

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;


    // Для загрузки фото в Firebase

    // storage будет использоваться для создания экземпляра FirebaseStorage
    FirebaseStorage storage;

    // storageReference укажет на загруженный файл
    StorageReference storageReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_user, container, false);

        ProfileUserName = view.findViewById(R.id.ProfileUserName);
        ProfilePic = view.findViewById(R.id.ProfilePic);
        ProfileId = view.findViewById(R.id.ProfileUserId);
        uploadButton = view.findViewById(R.id.uploadButton);
        chooseButton = view.findViewById(R.id.chooseButton);

        // Окно ожидания
        ProgressDialog progressDialog = new ProgressDialog(this.getContext());

        // Ссылки на хранилище
        storageReference = FirebaseStorage.getInstance().getReference("avatars");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                assert user != null;
                ProfileUserName.setText(user.getUsername());
                ProfileId.setText(user.getUserId());
                if (user.getImageUrl().equals("default")) {
                    ProfilePic.setImageResource(R.drawable.avatar);
                } else {
                    Glide.with(Objects.requireNonNull(getContext())).load(user.getImageUrl()).into(ProfilePic);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Вызов метода для выбора фото из галереи
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        // Вызов метода для добавления фото в Firebase Storage
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        return view;
    }

    // startActivityForResult - получениу результата, который является выбранным изображением.
    // Чтобы отобразить это изображение используется метод onActivityResult.
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // onActivityResult получает код запроса, код результата и данные.
    // В этом методе проверяется, будет ли код запроса равен PICK_IMAGE_REQUEST, с результатом, равным RESULT_OK, и
    // доступными данными. Если верно - изображение добавляем в ImageView.

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null ) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), filePath);
                ProfilePic.setImageBitmap(bitmap);
            }  catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("imageUrl", filePath.toString());
                            databaseReference.updateChildren(hashMap);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
}
