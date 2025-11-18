package com.proyecto.travelia.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.proyecto.travelia.data.local.AppDatabase;
import com.proyecto.travelia.data.local.UserDao;
import com.proyecto.travelia.data.local.UserEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    public interface UserCallback {
        void onResult(boolean success, @Nullable UserEntity user, String message);
    }

    private final UserDao userDao;
    private final SessionManager sessionManager;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UserRepository(Context context) {
        userDao = AppDatabase.get(context).userDao();
        sessionManager = SessionManager.getInstance(context);
    }

    public void register(String name,
                         String email,
                         String password,
                         String phone,
                         UserCallback callback) {
        ioExecutor.execute(() -> {
            UserEntity existing = userDao.findByEmailSync(email);
            if (existing != null) {
                post(callback, false, null, "Ya existe un usuario con ese correo");
                return;
            }
            long now = System.currentTimeMillis();
            UserEntity entity = new UserEntity(name, email, password, phone, now, now);
            long id = userDao.insert(entity);
            entity.id = id;
            sessionManager.setActiveUserId(id);
            post(callback, true, entity, "Registro exitoso");
        });
    }

    public void login(String email, String password, UserCallback callback) {
        ioExecutor.execute(() -> {
            UserEntity user = userDao.findByEmailSync(email);
            if (user == null) {
                post(callback, false, null, "Usuario no encontrado");
                return;
            }
            if (!user.password.equals(password)) {
                post(callback, false, null, "Contraseña incorrecta");
                return;
            }
            sessionManager.setActiveUserId(user.id);
            post(callback, true, user, "Inicio de sesión exitoso");
        });
    }

    public void updateProfile(String name, String phone, UserCallback callback) {
        ioExecutor.execute(() -> {
            Long activeId = sessionManager.getActiveUserIdNow();
            if (activeId == null) {
                post(callback, false, null, "Inicia sesión para actualizar tu perfil");
                return;
            }
            UserEntity user = userDao.findByIdSync(activeId);
            if (user == null) {
                sessionManager.clearSession();
                post(callback, false, null, "La sesión ya no es válida");
                return;
            }
            user.name = name;
            user.phone = phone;
            user.updatedAt = System.currentTimeMillis();
            userDao.update(user);
            post(callback, true, user, "Perfil actualizado");
        });
    }

    public void logout() {
        sessionManager.clearSession();
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public LiveData<UserEntity> observeActiveUser() {
        MediatorLiveData<UserEntity> mediator = new MediatorLiveData<>();
        LiveData<Long> sessionLive = sessionManager.getActiveUserId();
        final LiveData<UserEntity>[] currentSource = new LiveData[]{null};

        mediator.addSource(sessionLive, userId -> {
            if (currentSource[0] != null) {
                mediator.removeSource(currentSource[0]);
                currentSource[0] = null;
            }
            if (userId == null || userId <= 0) {
                mediator.setValue(null);
            } else {
                LiveData<UserEntity> source = userDao.observeById(userId);
                currentSource[0] = source;
                mediator.addSource(source, mediator::setValue);
            }
        });
        return mediator;
    }

    public LiveData<UserEntity> observeUser(long userId) {
        return userDao.observeById(userId);
    }

    public void syncWithRemote(@Nullable Runnable callback) {
        ioExecutor.execute(() -> {
            // Aquí podrías integrar tu API real / Firebase.
            // Por ahora es un placeholder para mantener la interfaz pedida.
            if (callback != null) {
                mainHandler.post(callback);
            }
        });
    }

    private void post(UserCallback callback, boolean success, @Nullable UserEntity user, String message) {
        if (callback == null) return;
        mainHandler.post(() -> callback.onResult(success, user, message));
    }
}
