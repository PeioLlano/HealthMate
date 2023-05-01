package com.example.healthmate.PantallaPrincipal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.healthmate.Mediciones.Mediciones;
import com.example.healthmate.Modelo.Medicion;
import com.example.healthmate.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MedicionAdapter extends BaseAdapter {

    private ArrayList<Medicion> mediciones;
    private LayoutInflater inflater;

    // Constructor del adaptador
    public MedicionAdapter(Context context, ArrayList<Medicion> vMediciones) {
        mediciones = vMediciones;  // asignar la lista de mediciones recibida a la variable interna mediciones
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  // inflar la vista para los items de la lista
    }

    // Obtener la cantidad de elementos en la lista
    @Override
    public int getCount() {
        return mediciones.size();
    }

    // Obtener un elemento de la lista en una posición específica
    @Override
    public Object getItem(int i) {
        return mediciones.get(i);
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
        view = inflater.inflate(R.layout.item_medicion, null);

        // Obtener las referencias a los elementos de la vista del item de la lista
        TextView tvTitulo = view.findViewById(R.id.tvTitulo);
        TextView tvFecha = view.findViewById(R.id.tvFecha);
        TextView tvMedicionDestacada = view.findViewById(R.id.tvMedicionDestacada);
        ImageView ivMedicion = view.findViewById(R.id.ivMedicion);

        // Obtener la medicion en la posición actual
        Medicion medicion = mediciones.get(position);

        // Establecer los valores de los elementos de la vista con los datos de la medicion actual
        tvTitulo.setText(medicion.getTitulo());

        // Define el formato deseado para la fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        // Formatea la fecha utilizando el formato definido
        String formattedDate = dateFormat.format(medicion.getFecha());

        tvFecha.setText(formattedDate);

        tvMedicionDestacada.setText(medicion.getMedicion());

        // Establecer la imagen adecuada según el tipo de medicion
        switch (medicion.getTipo()) {
            case "Peso":
                ivMedicion.setImageResource(R.drawable.weight);
                break;
            case "Altura":
                ivMedicion.setImageResource(R.drawable.height);
                break;
            case "IMC":
                ivMedicion.setImageResource(R.drawable.imc);
                break;
            case "Frecuencia cardíaca":
                ivMedicion.setImageResource(R.drawable.cardiac);
                break;
            case "Presión arterial":
                ivMedicion.setImageResource(R.drawable.info);
                break;
            case "Niveles de glucemia":
                ivMedicion.setImageResource(R.drawable.home);
                break;
            case "Nivel de oxígeno en sangre":
                ivMedicion.setImageResource(R.drawable.circle);
                break;
        }

        // Devolver la vista del item de la lista con los datos actualizados
        return view;
    }
}