package com.proyecto.travelia.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SessionManager {

    private static final String PREFS_NAME = "travelia_session";
    private static final String KEY_USER_ID = "active_user_id";
    private static SessionManager instance;

    private final SharedPreferences prefs;
    private final MutableLiveData<Long> activeUserIdLiveData = new MutableLiveData<>();

    private SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long stored = prefs.getLong(KEY_USER_ID, -1L);
        activeUserIdLiveData.setValue(stored > 0 ? stored : null);
    }

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager(context);
                }
            }
        }
        return instance;
    }

    public LiveData<Long> getActiveUserId() {
        return activeUserIdLiveData;
    }

    @Nullable
    public Long getActiveUserIdNow() {
        Long value = activeUserIdLiveData.getValue();
        if (value != null && value > 0) {
            return value;
        }
        long stored = prefs.getLong(KEY_USER_ID, -1L);
        return stored > 0 ? stored : null;
    }

    @Nullable
    public String getActiveUserKey() {
        Long id = getActiveUserIdNow();
        return id == null ? null : String.valueOf(id);
    }

    public void setActiveUserId(@Nullable Long userId) {
        if (userId == null || userId <= 0) {
            prefs.edit().remove(KEY_USER_ID).apply();
            activeUserIdLiveData.postValue(null);
        } else {
            prefs.edit().putLong(KEY_USER_ID, userId).apply();
            activeUserIdLiveData.postValue(userId);
        }
    }

    public void clearSession() {
        setActiveUserId(null);
    }
}
