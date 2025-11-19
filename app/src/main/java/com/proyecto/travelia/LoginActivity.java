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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyecto.travelia.data.UserRepository;
import com.proyecto.travelia.data.local.UserEntity;
import com.proyecto.travelia.data.session.UserSessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario;
    private EditText etContrasena;
    private Button btnIngresar;
    private TextView tvIrRegistro;

    private UserRepository userRepository;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
            return insets;
        });

        userRepository = new UserRepository(this);
        sessionManager = new UserSessionManager(this);

        if (sessionManager.isLoggedIn()) {
            irAInicio();
            return;
        }

        inicializarVistas();
        configurarEventos();
    }

    private void inicializarVistas() {
        etUsuario = findViewById(R.id.et_usuario);
        etContrasena = findViewById(R.id.et_contrasena);
        btnIngresar = findViewById(R.id.btn_ingresar);
        tvIrRegistro = findViewById(R.id.tv_ir_registro);
    }

    private void configurarEventos() {
        btnIngresar.setOnClickListener(v -> intentarLogin());

        TextView tvOlvido = findViewById(R.id.tv_olvido);
        tvOlvido.setOnClickListener(v -> Toast.makeText(this, "Función no disponible", Toast.LENGTH_SHORT).show());

        ImageButton btnGoogle = findViewById(R.id.btn_google);
        ImageButton btnFacebook = findViewById(R.id.button3);
        ImageButton btnOtra = findViewById(R.id.button4);
        View.OnClickListener social = v -> Toast.makeText(this, "Disponible próximamente", Toast.LENGTH_SHORT).show();
        btnGoogle.setOnClickListener(social);
        btnFacebook.setOnClickListener(social);
        btnOtra.setOnClickListener(social);

        tvIrRegistro.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void intentarLogin() {
        String usuario = etUsuario.getText().toString().trim();
        String contrasena = etContrasena.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(usuario).matches()) {
            etUsuario.setError("Ingresa un correo válido");
            etUsuario.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(contrasena)) {
            etContrasena.setError("Ingresa tu contraseña");
            etContrasena.requestFocus();
            return;
        }

        btnIngresar.setEnabled(false);
        userRepository.login(usuario, contrasena, this::onAuthResult);
    }

    private void onAuthResult(boolean success, String message, UserEntity user) {
        btnIngresar.setEnabled(true);
        if (!success || user == null) {
            Toast.makeText(this, message != null ? message : "Credenciales inválidas", Toast.LENGTH_SHORT).show();
            return;
        }
        sessionManager.login(user.id);
        irAInicio();
    }

    private void irAInicio() {
        startActivity(new Intent(this, InicioActivity.class));
        finishAffinity();
    }
}
