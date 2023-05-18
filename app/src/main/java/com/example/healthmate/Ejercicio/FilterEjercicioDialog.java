package com.example.healthmate.Ejercicio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.healthmate.R;

import java.util.ArrayList;
import java.util.Calendar;

public class FilterEjercicioDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.filter_ejercicio, null);

        // Inicializar spinner para elegir tipo de ejercicio por el que filtrar
        Spinner sTipo = view.findViewById(R.id.sTipo);
        ArrayList<String> tipos = new ArrayList<>();

        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_texto, tipos);
        adapter.setDropDownViewResource(R.layout.spinner_drop);
        sTipo.setEnabled(false);
        sTipo.setVisibility(View.INVISIBLE);
        sTipo.setAdapter(adapter);

        // Inicializar checkbox para activar/desactivar filtro de tipo de ejercicio
        CheckBox cbTipo = view.findViewById(R.id.cbTipo);
        cbTipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FiltrarEjercicioDialog", "Clicado tipo = " + cbTipo.isChecked());
                if (cbTipo.isChecked()) {
                    tipos.add("Running");
                    tipos.add("Nadar");
                    tipos.add("Ciclismo");
                    tipos.add("Futbol");
                    tipos.add("Ski");
                    tipos.add("Remo");
                    tipos.add("Basket");
                    sTipo.setEnabled(true);
                    sTipo.setVisibility(View.VISIBLE);
                } else {
                    tipos.clear();
                    sTipo.setVisibility(View.INVISIBLE);
                }
                adapter.notifyDataSetChanged();
            }
        });

        // Preparar filtro fecha
        DatePicker dpFecha = view.findViewById(R.id.dpFecha);
        dpFecha.setEnabled(false);
        dpFecha.setVisibility(View.INVISIBLE);

        // Inicializar checkbox para activar/desactivar filtro de tipo de ejercicio
        CheckBox cbFecha = view.findViewById(R.id.cbFecha);
        cbFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FiltrarEjercicioDialog", "Clicado fecha = " + cbFecha.isChecked());
                if (cbFecha.isChecked()) {
                    dpFecha.setEnabled(true);
                    dpFecha.setVisibility(View.VISIBLE);
                } else {
                    dpFecha.setVisibility(View.INVISIBLE);
                }
                adapter.notifyDataSetChanged();
            }
        });

        builder.setView(view)
                .setTitle("Filter exercises")
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Bundle filtro = new Bundle();
                        if (cbTipo.isChecked()) {
                            filtro.putString("Tipo", sTipo.getSelectedItem().toString());
                        } else {
                            filtro.putString("Tipo", null);
                        }

                        if (cbFecha.isChecked()) {
                            int dia = dpFecha.getDayOfMonth();
                            int mes = dpFecha.getMonth();
                            int año =  dpFecha.getYear();

                            String fecha =  String.valueOf(dia)
                                    + '/' + String.valueOf(mes)
                                    + '/' + String.valueOf(año);
                            filtro.putString("Fecha", fecha);
                        } else {
                            filtro.putString("Fecha", null);
                        }

                        getParentFragmentManager()
                                .setFragmentResult("filtro", filtro);
                    }
                });

        return builder.create();
    }
}
