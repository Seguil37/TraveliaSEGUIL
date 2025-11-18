package com.proyecto.travelia;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.proyecto.travelia.data.UserRepository;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etPhone;
    private Button btnRegistrar;
    private TextView tvIrLogin;

    private UserRepository userRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        userRepository = new UserRepository(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        etNombre = findViewById(R.id.et_nombre);
        etEmail = findViewById(R.id.et_email_registro);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etPhone = findViewById(R.id.et_phone);
        btnRegistrar = findViewById(R.id.btn_registrar);
        tvIrLogin = findViewById(R.id.tv_ir_login);
    }

    private void setupListeners() {
        btnRegistrar.setOnClickListener(v -> intentarRegistro());
        tvIrLogin.setOnClickListener(v -> finish());
    }

    private void intentarRegistro() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirm = etConfirmPassword.getText().toString();
        String phone = etPhone.getText().toString().trim();

        if (!validar(nombre, email, password, confirm, phone)) {
            return;
        }

        setLoading(true);
        userRepository.register(nombre, email, password, phone, (success, user, message) -> {
            setLoading(false);
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            if (success) {
                Intent intent = new Intent(RegisterActivity.this, InicioActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    private boolean validar(String nombre, String email, String password, String confirm, String phone) {
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingresa tu nombre completo");
            etNombre.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Ingresa un correo válido");
            etEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirm)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Ingresa un teléfono de contacto");
            etPhone.requestFocus();
            return false;
        }
        return true;
    }

    private void setLoading(boolean loading) {
        btnRegistrar.setEnabled(!loading);
        btnRegistrar.setAlpha(loading ? 0.5f : 1f);
    }
}
