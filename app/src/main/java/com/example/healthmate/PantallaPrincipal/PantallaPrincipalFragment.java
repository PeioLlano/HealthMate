package com.example.healthmate.PantallaPrincipal;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.healthmate.R;

public class PantallaPrincipalFragment extends Fragment {

    /* Atributos de la interfaz gráfica */


    /* Otros atributos */
    private ListenerPantallaPrincipalFragment listenerPantallaPrincipalFragment;


    /*
     * Interfaz para que 'MainActivity' haga visible el 'BottomNavigationView' (tan sólo esta
     * actividad puede acceder a este elemento)
     */
    public interface ListenerPantallaPrincipalFragment {
        void mostrarBarraDeNavegacion();
        void ocultarBarraDeNavegacion();
    }

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
        return inflater.inflate(R.layout.fragment_pantalla_principal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listenerPantallaPrincipalFragment.mostrarBarraDeNavegacion();

        /* RECUPERAR DATOS DEL PARTIDO SELECCIONADO */
        if (getArguments() != null) {
            String usuario = getArguments().getString("usuario");
            Log.d("PantallaPrincipalFragment", "USUARIO --> " + usuario);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listenerPantallaPrincipalFragment = (ListenerPantallaPrincipalFragment) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("La clase " + context
                + " debe implementar ListenerPantallaPrincipalFragment");
        }
    }
}