package com.example.healthmate.Ejercicio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.healthmate.Modelo.Ejercicio;
import com.example.healthmate.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EjercicioAdapter extends BaseAdapter {

    private ArrayList<Ejercicio> ejercicios;
    private LayoutInflater inflater;

    // Constructor del adaptador
    public EjercicioAdapter(Context context, ArrayList<Ejercicio> vMediciones) {
        ejercicios = vMediciones;  // asignar la lista de mediciones recibida a la variable interna ejercicios
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  // inflar la vista para los items de la lista
    }

    // Obtener la cantidad de elementos en la lista
    @Override
    public int getCount() {
        return ejercicios.size();
    }

    // Obtener un elemento de la lista en una posición específica
    @Override
    public Object getItem(int i) {
        return ejercicios.get(i);
    }

    // Obtener el ID de un elemento en una posición específica
    @Override
    public long getItemId(int i) {
        return i;
    }

    // Obtener la vista que se mostrará para cada item de la lista
    @NonNull
    @Override
    public View getView(int position, @NonNull View view, @NonNull ViewGroup viewGroup){

        // Inflar la vista del item de la lista
        view = inflater.inflate(R.layout.item_ejercicio, null);

        // Obtener las referencias a los elementos de la vista del item de la lista
        TextView tvTitulo = view.findViewById(R.id.tvTitulo);
        TextView tvFecha = view.findViewById(R.id.tvFecha);
        TextView tvEstadisticaDestacada = view.findViewById(R.id.tvEstadisticaDestacada);
        ImageView ivDeporte = view.findViewById(R.id.ivDeporte);

        // Obtener la medicion en la posición actual
        Ejercicio ejercicio = ejercicios.get(position);

        // Establecer los valores de los elementos de la vista con los datos de la medicion actual
        tvTitulo.setText(ejercicio.getTitulo());

        // Define el formato deseado para la fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        // Formatea la fecha utilizando el formato definido
        String formattedDate = dateFormat.format(ejercicio.getFecha());

        tvFecha.setText(formattedDate);

        tvEstadisticaDestacada.setText(Double.toString(ejercicio.getMedicion()) + "kms");

        // Establecer la imagen adecuada según el tipo de medicion
        switch (ejercicio.getTipo()) {
            case "Running":
                ivDeporte.setImageResource(R.drawable.running);
                break;
            case "Nadar":
                ivDeporte.setImageResource(R.drawable.nadar);
                break;
            case "Ciclismo":
                ivDeporte.setImageResource(R.drawable.ciclismo);
                break;
            case "Futbol":
                ivDeporte.setImageResource(R.drawable.futbol);
                break;
            case "Ski":
                ivDeporte.setImageResource(R.drawable.ski);
                break;
            case "Remo":
                ivDeporte.setImageResource(R.drawable.remo);
                break;
            case "Basket":
                ivDeporte.setImageResource(R.drawable.basket);
                break;
        }

        // Devolver la vista del item de la lista con los datos actualizados
        return view;
    }
}