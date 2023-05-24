package com.example.healthmate;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class CambiarEstiloDialog extends DialogFragment {

    ListenerdelDialogoEstilo miListener;

    public interface ListenerdelDialogoEstilo{
        void alElegirEstilo(int i);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        miListener =(ListenerdelDialogoEstilo) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] opciones = {getString(R.string.dark), getString(R.string.normal)};
        builder.setTitle(getString(R.string.select_style))
                .setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        miListener.alElegirEstilo(i);
                        dismiss();
                    }
                });
        return builder.create();
    }
}
