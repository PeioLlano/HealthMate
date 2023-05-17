package com.example.healthmate.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.healthmate.MainActivity;
import com.example.healthmate.R;
import com.example.healthmate.Workers.InsertWorker;
import com.example.healthmate.Workers.SelectWorker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


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

        cargarLogeado();

        ((MainActivity) getActivity()).disableOptions();

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
                navegarHaciarRegistro();
                /*Toast.makeText(
                    requireContext(),
                    "REGISTRARSE",
                    Toast.LENGTH_SHORT
                ).show();*/
            }
        });
    }

    // Método privado para comprobar el inicio de sesión del usuario
    private void comprobarLogeo(String username, String password) {

        //Inicializar toast de inicio incorreto
        int tiempoToast= Toast.LENGTH_SHORT;
        Toast avisoInicioIncorrecto = Toast.makeText(requireContext(), getString(R.string.incorrect_cred), tiempoToast);

        if (!(username.isEmpty() || password.isEmpty())) {

            String passHash = hashPassword(password);

            Data data = new Data.Builder()
                    .putString("tabla", "Usuarios")
                    .putString("condicion", "Usuario='"+username+"' AND Contraseña='" + passHash+"'")
                    .build();

            Constraints constr = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(SelectWorker.class)
                    .setConstraints(constr)
                    .setInputData(data)
                    .build();

            WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(req.getId())
                    .observe(this, status -> {
                        if (status != null && status.getState().isFinished()) {
                            String resultados = status.getOutputData().getString("resultados");
                            if (resultados.equals("null") || resultados.equals("")) resultados = null;
                            if(resultados != null) {
                                subirTokenFirebase(username);
                                guardarPreferenciaLogin(username);
                                NavDirections accion = LoginFragmentDirections
                                        .actionLoginFragmentToPantallaPrincipalFragment(username);
                                NavHostFragment.findNavController(this).navigate(accion);
                            }
                            //En caso contrario el toast de inicio incorrecto
                            else {
                                avisoInicioIncorrecto.show();
                            }
                        }
                    });
            WorkManager.getInstance(requireContext()).enqueue(req);

        }//En caso contrario el toast de inicio incorrecto
        else {
            avisoInicioIncorrecto.show();
        }
    }

    public static String hashPassword(String password) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private void subirTokenFirebase(String username) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            task.getException();
                        }

                        String token = task.getResult().getToken();

                        try {
                            Data data = new Data.Builder()
                                    .putString("tabla", "Token")
                                    .putStringArray("keys", new String[]{"Usuario", "Token"})
                                    .putStringArray("values", new String[]{username, token})
                                    .build();

                            Constraints constr = new Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build();

                            OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(InsertWorker.class)
                                    .setConstraints(constr)
                                    .setInputData(data)
                                    .build();

                            WorkManager workManager = WorkManager.getInstance(requireContext());
                            workManager.enqueue(req);

                            workManager.getWorkInfoByIdLiveData(req.getId())
                                    .observe((LifecycleOwner) requireContext(), status -> {
                                        if (status != null && status.getState().isFinished()) {
                                            Boolean resultados = status.getOutputData().getBoolean("resultado", false);
                                            if(resultados) {
                                                Log.d("Token", "Token añadido correctamente");
                                            }
                                        }});

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //Guardar las preferencias del usuario que ha iniciado sesion
    private void guardarPreferenciaLogin(String user){
        SharedPreferences preferences = getContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("loged_user", user);
        editor.commit();
    }

    private void cargarLogeado() {
        SharedPreferences preferences = getContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String loged_user = preferences.getString("loged_user", "");

        if(!loged_user.equals("")) {
            NavDirections accion = LoginFragmentDirections
                    .actionLoginFragmentToPantallaPrincipalFragment(loged_user);
            NavHostFragment.findNavController(this).navigate(accion);
        }
    }

    private void navegarHaciarRegistro() {
        NavDirections accion = LoginFragmentDirections
                .actionLoginFragmentToRegistroFragment();
        NavHostFragment.findNavController(this).navigate(accion);
    }

}
