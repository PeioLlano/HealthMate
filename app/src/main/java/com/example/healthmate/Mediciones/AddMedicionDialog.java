package com.example.healthmate.Mediciones;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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

public class AddMedicionDialog extends AppCompatDialogFragment {

    private AddMedicionDialogListener miListener;

    public interface AddMedicionDialogListener {
        void añadirMedicion(String titulo, Date fecha, String medicion, String tipo);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_mediciones, null);

        EditText etTitulo = view.findViewById(R.id.etTitulo);
        EditText etMedicion = view.findViewById(R.id.etMedicion);
        EditText etDate = view.findViewById(R.id.etDate);

        miListener = (AddMedicionDialogListener) getActivity();

        Spinner sTipo = (Spinner) view.findViewById(R.id.sTipo);
        ArrayList<String> tipos = new ArrayList<>();

        tipos.add("Peso");
        tipos.add("Altura");
        tipos.add("IMC");
        tipos.add("Frecuencia cardíaca");
        tipos.add("Presión arterial");
        tipos.add("Niveles de glucemia");
        tipos.add("Nivel de oxígeno en sangre");

        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_texto, tipos);
        adapter.setDropDownViewResource(R.layout.spinner_drop);
        sTipo.setAdapter(adapter);


        builder.setView(view)
                .setTitle(getResources().getString(R.string.enterMedicionData))
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!etTitulo.getText().toString().equals("") && !etMedicion.getText().toString().equals("")) {
                            Bundle nuevaMedicion = new Bundle();
                            nuevaMedicion.putString("titulo", etTitulo.getText().toString());
                            nuevaMedicion.putString("fecha", etDate.getText().toString());
                            nuevaMedicion.putString("medicion", etMedicion.getText().toString());
                            nuevaMedicion.putString("tipo", sTipo.getSelectedItem().toString());
                            getParentFragmentManager()
                                    .setFragmentResult("nuevaMedicion", nuevaMedicion);
                            /*if (etDate.getText().toString().equals(""))miListener.añadirMedicion(etTitulo.getText().toString(), new Date(), etMedicion.getText().toString(), sTipo.getSelectedItem().toString());
                            else miListener.añadirMedicion(etTitulo.getText().toString(), new Date(etDate.getText().toString()), etMedicion.getText().toString(), sTipo.getSelectedItem().toString());*/
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