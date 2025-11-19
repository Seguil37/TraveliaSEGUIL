package com.proyecto.travelia.data.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.proyecto.travelia.LoginActivity;
import com.proyecto.travelia.R;

public class UserSessionManager {

    private static final String PREFS = "travelia_session";
    private static final String KEY_USER_ID = "session_user_id";
    public static final String ANONYMOUS_USER_ID = "guest";

    private final SharedPreferences prefs;

    public UserSessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return prefs.contains(KEY_USER_ID);
    }

    @Nullable
    public String getActiveUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getActiveOrGuestId() {
        String id = getActiveUserId();
        return id == null ? ANONYMOUS_USER_ID : id;
    }

    public void login(String userId) {
        prefs.edit().putString(KEY_USER_ID, userId).apply();
    }

    public void logout() {
        prefs.edit().remove(KEY_USER_ID).apply();
    }

    public boolean ensureLoggedIn(Activity activity) {
        if (isLoggedIn()) {
            return true;
        }
        Toast.makeText(activity, activity.getString(R.string.login_required_message), Toast.LENGTH_SHORT).show();
        activity.startActivity(new Intent(activity, LoginActivity.class));
        return false;
    }
}
