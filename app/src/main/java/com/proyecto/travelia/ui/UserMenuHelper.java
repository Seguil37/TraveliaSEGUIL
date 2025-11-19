package com.proyecto.travelia.ui;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.proyecto.travelia.LoginActivity;
import com.proyecto.travelia.PerfilActivity;
import com.proyecto.travelia.R;
import com.proyecto.travelia.data.UserRepository;
import com.proyecto.travelia.data.local.UserEntity;
import com.proyecto.travelia.data.session.UserSessionManager;

/**
 * Encapsula la lógica del icono del usuario ubicado en la barra superior
 * para evitar duplicar listeners en cada Activity. Se encarga de:
 *  - Navegar al Login si no hay sesión activa.
 *  - Navegar al Perfil cuando el usuario ya inició sesión.
 *  - Mostrar un saludo con el nombre del usuario autenticado.
 */
public final class UserMenuHelper {

    private UserMenuHelper() {
        // Utility class
    }

    public static void bind(AppCompatActivity activity,
                             UserSessionManager sessionManager) {
        if (activity == null || sessionManager == null) {
            return;
        }

        View icon = activity.findViewById(R.id.iv_user_avatar);
        TextView label = activity.findViewById(R.id.tv_user_status);
        if (icon == null && label == null) {
            return; // no hay barra superior en esta pantalla
        }

        View.OnClickListener navigation = v -> {
            Class<?> target = sessionManager.isLoggedIn() ? PerfilActivity.class : LoginActivity.class;
            Intent intent = new Intent(activity, target);
            activity.startActivity(intent);
        };

        if (icon != null) {
            icon.setOnClickListener(navigation);
        }
        if (label != null) {
            label.setOnClickListener(navigation);
            updateLabel(activity, label, sessionManager);
        }
    }

    private static void updateLabel(AppCompatActivity activity,
                                    TextView label,
                                    UserSessionManager sessionManager) {
        if (!sessionManager.isLoggedIn()) {
            label.setText(activity.getString(R.string.topbar_guest_label));
            return;
        }
        String userId = sessionManager.getActiveUserId();
        if (userId == null) {
            label.setText(activity.getString(R.string.topbar_profile_label));
            return;
        }
        UserRepository repository = new UserRepository(activity);
        repository.observeUser(userId).observe(activity, user ->
                label.setText(formatGreeting(activity, user)));
    }

    private static String formatGreeting(AppCompatActivity activity,
                                         @Nullable UserEntity user) {
        if (user == null || user.name == null || user.name.trim().isEmpty()) {
            return activity.getString(R.string.topbar_profile_label);
        }
        String[] tokens = user.name.trim().split("\\s+");
        String firstName = tokens.length > 0 ? tokens[0] : user.name;
        return activity.getString(R.string.topbar_greeting_format, firstName);
    }
}
