package com.example.healthmate.Medicinas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.healthmate.Modelo.Medicina;
import com.example.healthmate.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MedicinaAdapter extends BaseAdapter {

    private ArrayList<Medicina> medicinas;
    private LayoutInflater inflater;

    // Constructor del adaptador
    public MedicinaAdapter(Context context, ArrayList<Medicina> vMedicinas) {
        medicinas = vMedicinas;  // asignar la lista de medicinas recibida a la variable interna medicinas
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  // inflar la vista para los items de la lista
    }

    // Obtener la cantidad de elementos en la lista
    @Override
    public int getCount() {
        return medicinas.size();
    }

    // Obtener un elemento de la lista en una posición específica
    @Override
    public Object getItem(int i) {
        return medicinas.get(i);
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
        view = inflater.inflate(R.layout.item_medicina, null);

        // Obtener las referencias a los elementos de la vista del item de la lista
        TextView etNombre = view.findViewById(R.id.etNombre);
        TextView tvDias = view.findViewById(R.id.tvDias);
        TextView tvHora = view.findViewById(R.id.tvHora);

        // Obtener la medicina en la posición actual
        Medicina medicina = medicinas.get(position);

        // Establecer los valores de los elementos de la vista con los datos de la medicina actual
        etNombre.setText(medicina.getNombre());
        tvDias.setText(medicina.concatenateWithCommasAndAmpersand(medicina.getDias()));
        tvHora.setText(medicina.concatenateWithCommasAndAmpersand(medicina.getHoras()));

        // Devolver la vista del item de la lista con los datos actualizados
        return view;
    }
}