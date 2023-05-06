package com.example.healthmate.Login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.example.healthmate.R;


public class LoginFragment extends Fragment {

    /* Atributos de la interfaz gráfica */
    private EditText eUsername;
    private EditText ePassword;
    private Button bSignIn;
    private TextView tNoCuenta;
    private Button bSignUp;

    /* Otros atributos */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eUsername = view.findViewById(R.id.eUsername);
        ePassword = view.findViewById(R.id.ePassword);
        tNoCuenta = view.findViewById(R.id.tNoCuenta);
        bSignIn = view.findViewById(R.id.bSignIn);
        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Llamar al método "comprobarLogeo" con los valores ingresados en las vistas "EditText"
                comprobarLogeo(eUsername.getText().toString(), ePassword.getText().toString());
            }
        });
        bSignUp = view.findViewById(R.id.bSignUp);
        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(
                    requireContext(),
                    "REGISTRARSE",
                    Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // Método privado para comprobar el inicio de sesión del usuario
    private void comprobarLogeo(String username, String password) {
        // Implementar la lógica para comprobar el inicio de sesión del usuario

        NavDirections accion = LoginFragmentDirections
            .actionLoginFragmentToPantallaPrincipalFragment(username);
        NavHostFragment.findNavController(this).navigate(accion);
    }
}
