package com.proyecto.travelia;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.proyecto.travelia.data.SessionManager;
import com.proyecto.travelia.data.UserRepository;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario;
    private EditText etContrasena;
    private TextView tvOlvido;
    private TextView tvRegistrar;
    private Button btnIngresar;
    private ImageButton btnGoogle;
    private ImageButton btnFacebook;
    private ImageButton btnOtra;

    private UserRepository userRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        userRepository = new UserRepository(this);
        sessionManager = userRepository.getSessionManager();

        inicializarVistas();
        configurarEventos();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sessionManager.getActiveUserIdNow() != null) {
            irAInicio();
        }
    }

    private void intentarLogin() {
        String usuario = etUsuario.getText().toString().trim();
        String contrasena = etContrasena.getText().toString();

        if (!validarUsuario(usuario) || !validarContrasena(contrasena)) {
            return;
        }

        setLoading(true);
        userRepository.login(usuario, contrasena, (success, user, message) -> {
            setLoading(false);
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            if (success) {
                irAInicio();
            }
        });
    }

    private boolean validarUsuario(String usuario) {
        if (TextUtils.isEmpty(usuario)) {
            etUsuario.setError("Ingrese correo electrónico");
            etUsuario.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(usuario).matches()) {
            etUsuario.setError("Ingrese un email válido");
            etUsuario.requestFocus();
            return false;
        }
        etUsuario.setError(null);
        return true;
    }

    private boolean validarContrasena(String contrasena) {
        if (TextUtils.isEmpty(contrasena)) {
            etContrasena.setError("Ingrese contraseña");
            etContrasena.requestFocus();
            return false;
        }
        if (contrasena.length() < 4) {
            etContrasena.setError("La contraseña debe tener al menos 4 caracteres");
            etContrasena.requestFocus();
            return false;
        }
        etContrasena.setError(null);
        return true;
    }

    private void configurarEventos() {
        btnIngresar.setOnClickListener(v -> intentarLogin());

        tvRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvOlvido.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "Funcionalidad 'Olvidé mi contraseña' pendiente", Toast.LENGTH_SHORT).show());

        View.OnClickListener socialListener = v ->
                Toast.makeText(LoginActivity.this, "Integración pendiente", Toast.LENGTH_SHORT).show();
        btnGoogle.setOnClickListener(socialListener);
        btnFacebook.setOnClickListener(socialListener);
        btnOtra.setOnClickListener(socialListener);
    }

    private void inicializarVistas() {
        etUsuario = findViewById(R.id.et_usuario);
        etContrasena = findViewById(R.id.et_contrasena);
        tvOlvido = findViewById(R.id.tv_olvido);
        tvRegistrar = findViewById(R.id.tv_registrar);
        btnIngresar = findViewById(R.id.btn_ingresar);
        btnGoogle = findViewById(R.id.btn_google);
        btnFacebook = findViewById(R.id.button3);
        btnOtra = findViewById(R.id.button4);
    }

    private void setLoading(boolean loading) {
        btnIngresar.setEnabled(!loading);
        btnIngresar.setAlpha(loading ? 0.5f : 1f);
    }

    private void irAInicio() {
        Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
