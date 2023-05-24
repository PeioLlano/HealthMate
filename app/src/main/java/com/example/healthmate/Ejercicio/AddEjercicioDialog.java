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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_ejercicio, null);

        EditText etTitulo = view.findViewById(R.id.etTitulo);
        EditText etDistancia = view.findViewById(R.id.etDistancia);
        EditText etDate = view.findViewById(R.id.etDate);

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
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!etTitulo.getText().toString().equals("")) {
                            Bundle nuevoEjercicio = new Bundle();
                            nuevoEjercicio.putInt("codigo", -1);
                            nuevoEjercicio.putString("titulo", etTitulo.getText().toString());
                            if (etDate.getText().toString().equals("")) {
                                nuevoEjercicio.putString("fecha", "");
                            }
                            else{
                                nuevoEjercicio.putString("fecha", etDate.getText().toString());
                            }
                            nuevoEjercicio.putDouble("distancia",
                                    Double.parseDouble(etDistancia.getText().toString()));
                            nuevoEjercicio.putString("tipo", sTipo.getSelectedItem().toString());
                            getParentFragmentManager()
                                    .setFragmentResult("nuevoEjercicio", nuevoEjercicio);
                        }
                        else{
                            int tiempoToast= Toast.LENGTH_SHORT;
                            Toast avisoEjercicio = Toast.makeText(view.getContext(), getString(R.string.fill_fields), tiempoToast);
                            avisoEjercicio.show();
                        }
                    }
                });

        return builder.create();
    }
}