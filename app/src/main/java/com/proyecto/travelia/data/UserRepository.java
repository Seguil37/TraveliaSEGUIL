package com.proyecto.travelia.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.proyecto.travelia.data.local.AppDatabase;
import com.proyecto.travelia.data.local.UserDao;
import com.proyecto.travelia.data.local.UserEntity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    public interface AuthCallback {
        void onResult(boolean success, @Nullable String message, @Nullable UserEntity user);
    }

    private final UserDao userDao;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UserRepository(Context context) {
        userDao = AppDatabase.get(context).userDao();
    }

    public LiveData<UserEntity> observeUser(String userId) {
        return userDao.observeById(userId);
    }

    public void login(String email, String password, AuthCallback callback) {
        ioExecutor.execute(() -> {
            UserEntity entity = userDao.findByEmail(email.toLowerCase(Locale.getDefault()));
            if (entity == null) {
                post(callback, false, "El usuario no existe", null);
                return;
            }
            String hashed = hash(password);
            if (!entity.passwordHash.equals(hashed)) {
                post(callback, false, "Contraseña incorrecta", null);
                return;
            }
            post(callback, true, null, entity);
        });
    }

    public void register(String name,
                         String email,
                         String password,
                         String phone,
                         String nationality,
                         AuthCallback callback) {
        ioExecutor.execute(() -> {
            String normalizedEmail = email.toLowerCase(Locale.getDefault());
            UserEntity existing = userDao.findByEmail(normalizedEmail);
            if (existing != null) {
                post(callback, false, "El correo ya está registrado", null);
                return;
            }
            long now = System.currentTimeMillis();
            UserEntity entity = new UserEntity(
                    UUID.randomUUID().toString(),
                    name,
                    normalizedEmail,
                    phone,
                    nationality,
                    hash(password),
                    true,
                    now,
                    now
            );
            userDao.insert(entity);
            post(callback, true, null, entity);
        });
    }

    public void update(UserEntity entity, AuthCallback callback) {
        ioExecutor.execute(() -> {
            entity.updatedAt = System.currentTimeMillis();
            userDao.update(entity);
            post(callback, true, null, entity);
        });
    }

    private void post(AuthCallback callback, boolean success, @Nullable String message, @Nullable UserEntity entity) {
        if (callback == null) return;
        mainHandler.post(() -> callback.onResult(success, message, entity));
    }

    private String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format(Locale.US, "%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(input.hashCode());
        }
    }
}
