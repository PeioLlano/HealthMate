package com.example.healthmate.Medicinas;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.healthmate.R;

import java.util.ArrayList;
import java.util.Date;

public class AddMedicinaDialog extends AppCompatDialogFragment {

    CheckBox cbLunes;
    CheckBox cbMartes;
    CheckBox cbMiercoles;
    CheckBox cbJueves;
    CheckBox cbViernes;
    CheckBox cbSabado;
    CheckBox cbDomingo;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_medicina, null);

        EditText etNombre = view.findViewById(R.id.etNombre);
        TimePicker dpHora = view.findViewById(R.id.dpHora);
        dpHora.setIs24HourView(true);
        cbLunes = view.findViewById(R.id.cbLunes);
        cbMartes = view.findViewById(R.id.cbMartes);
        cbMiercoles = view.findViewById(R.id.cbMiercoles);
        cbJueves = view.findViewById(R.id.cbJueves);
        cbViernes = view.findViewById(R.id.cbViernes);
        cbSabado = view.findViewById(R.id.cbSabado);
        cbDomingo = view.findViewById(R.id.cbDomingo);

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
                        if (!etNombre.getText().toString().equals("")) {
                            Bundle nuevaMedicina = new Bundle();
                            nuevaMedicina.putString("nombre", etNombre.getText().toString());
                            nuevaMedicina.putString("hora", String.valueOf(dpHora.getHour()) + ":" + String.valueOf(dpHora.getMinute()));
                            nuevaMedicina.putString("dias", getDias());

                            getParentFragmentManager()
                                    .setFragmentResult("nuevaMedicina", nuevaMedicina);
                        }
                        else{
                            int tiempoToast= Toast.LENGTH_SHORT;
                            Toast avisoMedicion = Toast.makeText(view.getContext(), getString(R.string.fill_fields), tiempoToast);
                            avisoMedicion.show();
                        }
                    }
                });

        return builder.create();
    }

    private String getDias() {
        ArrayList<String> diasSeleccionados = new ArrayList<>();

        if (cbLunes.isChecked()) {
            diasSeleccionados.add("lunes");
        }
        if (cbMartes.isChecked()) {
            diasSeleccionados.add("martes");
        }
        if (cbMiercoles.isChecked()) {
            diasSeleccionados.add("miercoles");
        }
        if (cbJueves.isChecked()) {
            diasSeleccionados.add("jueves");
        }
        if (cbViernes.isChecked()) {
            diasSeleccionados.add("viernes");
        }
        if (cbSabado.isChecked()) {
            diasSeleccionados.add("sabado");
        }
        if (cbDomingo.isChecked()) {
            diasSeleccionados.add("domingo");
        }

        return TextUtils.join(",", diasSeleccionados);
    }
}