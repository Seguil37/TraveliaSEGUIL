package com.proyecto.travelia;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyecto.travelia.data.UserRepository;
import com.proyecto.travelia.data.local.UserEntity;
import com.proyecto.travelia.data.session.UserSessionManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etEmail;
    private EditText etTelefono;
    private Spinner spNacionalidad;
    private EditText etPassword;
    private EditText etConfirmPassword;

    private UserRepository userRepository;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
            return insets;
        });

        userRepository = new UserRepository(this);
        sessionManager = new UserSessionManager(this);

        initViews();
        setupSpinners();
        setupListeners();
    }

    private void initViews() {
        etNombre = findViewById(R.id.et_nombre);
        etEmail = findViewById(R.id.et_email);
        etTelefono = findViewById(R.id.et_telefono);
        spNacionalidad = findViewById(R.id.sp_nacionalidad);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
    }

    private void setupSpinners() {
        String[] paises = {"Selecciona país", "Perú", "Chile", "Argentina", "Colombia", "México", "España"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, paises);
        spNacionalidad.setAdapter(adapter);
    }

    private void setupListeners() {
        Button btnRegistrar = findViewById(R.id.btn_registrar);
        btnRegistrar.setOnClickListener(v -> intentarRegistro());

        TextView tvIrLogin = findViewById(R.id.tv_ir_login);
        tvIrLogin.setOnClickListener(v -> finish());
    }

    private void intentarRegistro() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirm = etConfirmPassword.getText().toString();
        String nacionalidad = spNacionalidad.getSelectedItem().toString();

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingresa tu nombre");
            etNombre.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Correo no válido");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(telefono)) {
            etTelefono.setError("Ingresa tu teléfono");
            etTelefono.requestFocus();
            return;
        }
        if (spNacionalidad.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecciona tu nacionalidad", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Mínimo 6 caracteres");
            etPassword.requestFocus();
            return;
        }
        if (!password.equals(confirm)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus();
            return;
        }

        findViewById(R.id.btn_registrar).setEnabled(false);
        userRepository.register(nombre, email, password, telefono, nacionalidad, this::onAuthResult);
    }

    private void onAuthResult(boolean success, String message, UserEntity user) {
        findViewById(R.id.btn_registrar).setEnabled(true);
        if (!success) {
            Toast.makeText(this, message != null ? message : "Error al registrar", Toast.LENGTH_SHORT).show();
            return;
        }
        sessionManager.login(user.id);
        Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
        startActivity(new android.content.Intent(this, InicioActivity.class));
        finishAffinity();
    }
}
