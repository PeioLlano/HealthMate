package com.example.healthmate.Ejercicio;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.healthmate.R;

import java.util.ArrayList;
import java.util.Date;

public class AddEjercicioDialog extends AppCompatDialogFragment {

    private AddEjercicioDialogListener miListener;

    public interface AddEjercicioDialogListener {
        void añadirEjercicio(String titulo, Date fecha, Double distancia, String tipo);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_ejercicio, null);

        EditText etTitulo = view.findViewById(R.id.etTitulo);
        EditText etDistancia = view.findViewById(R.id.etDistancia);

        miListener =(AddEjercicioDialogListener) getActivity();

        Spinner sTipo = (Spinner) view.findViewById(R.id.sTipo);
        ArrayList<String> tipos = new ArrayList<>();

        tipos.add("Running");
        tipos.add("Nadar");
        tipos.add("Ciclismo");
        tipos.add("Futbol");
        tipos.add("Ski");
        tipos.add("Remo");
        tipos.add("Basket");

        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_texto, tipos);
        adapter.setDropDownViewResource(R.layout.spinner_drop);
        sTipo.setAdapter(adapter);


        builder.setView(view)
                .setTitle(getResources().getString(R.string.enterExerciseData))
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!etTitulo.getText().toString().equals("")) {
                            miListener.añadirEjercicio(etTitulo.getText().toString(), new Date(),Double.parseDouble(etDistancia.getText().toString()), sTipo.getSelectedItem().toString());
                        }
                        else{
                            int tiempoToast= Toast.LENGTH_SHORT;
                            Toast avisoGasto = Toast.makeText(view.getContext(), getString(R.string.fill_fields), tiempoToast);
                            avisoGasto.show();
                        }
                    }
                });

        return builder.create();
    }
}