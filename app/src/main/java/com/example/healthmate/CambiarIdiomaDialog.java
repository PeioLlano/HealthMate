package com.example.healthmate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.os.LocaleListCompat;
import androidx.preference.PreferenceManager;

import com.example.healthmate.Modelo.Medicina;
import com.example.healthmate.PantallaPrincipal.PantallaPrincipalFragment;

public class CambiarIdiomaDialog extends AppCompatDialogFragment {

    private String idiomaSeleccionado;
    private RadioButton rbEuskara, rbCastellano, rbIngles;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.cambiar_idioma_dialog, null);
        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(requireContext());

        rbEuskara = view.findViewById(R.id.rbEuskara);
        rbEuskara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idiomaSeleccionado = "eu";
            }
        });

        rbCastellano = view.findViewById(R.id.rbCastellano);
        rbCastellano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idiomaSeleccionado = "es";
            }
        });

        rbIngles = view.findViewById(R.id.rbIngles);
        rbIngles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idiomaSeleccionado = "en";
            }
        });

        String idiomaGuardado = preferencias.getString("idioma", null);
        marcarIdiomaGuardado(idiomaGuardado);

        builder.setView(view)
            .setTitle(R.string.change_language)
            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dismiss();
                }
            })
            .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    cambiarIdioma(idiomaSeleccionado);
                    dismiss();
                }
            });


        return builder.create();
    }

    private void marcarIdiomaGuardado(String pIdioma) {
        if (pIdioma != null)
            switch (pIdioma) {
                case "eu":
                    rbEuskara.setChecked(true);
                    break;
                case "es":
                    rbCastellano.setChecked(true);
                    break;
                case "en":
                    rbIngles.setChecked(true);
                    break;
            }
    }

    private void cambiarIdioma(String pIdioma) {
        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("idioma", pIdioma);
        editor.apply();

        Log.d("CambiarIdiomaDialog", "Idioma = " + idiomaSeleccionado);
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(pIdioma);
        AppCompatDelegate.setApplicationLocales(appLocale);
    }
}
