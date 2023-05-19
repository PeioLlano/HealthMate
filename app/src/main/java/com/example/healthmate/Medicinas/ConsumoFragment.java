package com.example.healthmate.Medicinas;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.healthmate.Medicinas.ConsumoAdapter;
import com.example.healthmate.Modelo.Medicina;
import com.example.healthmate.R;
import com.example.healthmate.Workers.DeleteWorker;
import com.example.healthmate.Workers.SelectWorker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class ConsumoFragment extends Fragment {

    private String username;
    private ListView lvconsumos;
    private View llVacia;
    private ConsumoAdapter pAdapter;
    private ArrayList<Medicina> consumos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_consumo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtenemos la referencia a la vista de lista de consumos
        lvconsumos = view.findViewById(R.id.lvConsumos);

        // Obtenemos la referencia a la vista de "lista vacía"
        llVacia = view.findViewById(R.id.llVacia);

        // Ejemplo hasta que decidimamos como almacenamos los datos
        consumos = new ArrayList<>();

        //Pedimos todos los ejercicios que tenga el usuario que hemos recibido
        final JSONArray[] jsonArray = {new JSONArray()};

        Data data = new Data.Builder()
                .putString("tabla", "Medicinas")
                .putString("condicion", "Usuario='"+((MedicinasActivity) getActivity()).cargarLogeado()+"'")
                .build();

        Constraints constr = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(SelectWorker.class)
                .setConstraints(constr)
                .setInputData(data)
                .build();

        WorkManager workManager = WorkManager.getInstance(requireContext());
        workManager.enqueue(req);

        workManager.getWorkInfoByIdLiveData(req.getId())
                .observe(requireActivity(), status -> {
                    if (status != null && status.getState().isFinished()) {
                        String resultados = status.getOutputData().getString("resultados");
                        if (resultados.equals("null") || resultados.equals("")) resultados = null;
                        if(resultados != null) {
                            try {
                                jsonArray[0] = new JSONArray(resultados);

                                for (int i = 0; i < jsonArray[0].length(); i++) {
                                    JSONObject obj = jsonArray[0].getJSONObject(i);
                                    Integer Codigo = obj.getInt("Codigo");
                                    String Nombre = obj.getString("Nombre");
                                    String Hora = obj.getString("Hora");
                                    String Dias = obj.getString("Dias");

                                    for (String dia:stringArrayList(Dias)) {
                                        if(isCurrentDayOfWeek(dia)){
                                            consumos.add(new Medicina(Codigo,Nombre,Hora,stringArrayList(Dias)));
                                        }
                                    }

                                    pAdapter.notifyDataSetChanged();

                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        actualizarVacioLleno(consumos);
                    }
                });
        WorkManager.getInstance(requireContext()).enqueue(req);

        // Creamos un adaptador para la lista de consumos
        pAdapter = new ConsumoAdapter(requireContext(), consumos);

        // Configuramos el adaptador para la vista de lista de consumos
        lvconsumos.setAdapter(pAdapter);
    }

    //Actualizar lo que se ve dependiendo del tamaño de la lista.
    private void actualizarVacioLleno(ArrayList<Medicina> grupos) {

        if(grupos.size() > 0) {
            llVacia.setVisibility(View.GONE);
        } else {
            llVacia.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<String> stringArrayList(String str){
        // Separar el string por comas y obtener un array de strings
        String[] elementos = str.split(",");

        // Crear un ArrayList y agregar los elementos al mismo
        ArrayList<String> lista = new ArrayList<>(Arrays.asList(elementos));

        return lista;
    }

    public static int getDayOfWeek(String dayOfWeek) {
        switch (dayOfWeek.toLowerCase()) {
            case "domingo":
                return Calendar.SUNDAY;
            case "lunes":
                return Calendar.MONDAY;
            case "martes":
                return Calendar.TUESDAY;
            case "miercoles":
                return Calendar.WEDNESDAY;
            case "jueves":
                return Calendar.THURSDAY;
            case "viernes":
                return Calendar.FRIDAY;
            case "sabado":
                return Calendar.SATURDAY;
            default:
                return -1; // Valor no válido
        }
    }

    public static boolean isCurrentDayOfWeek(String dayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int targetDayOfWeek = getDayOfWeek(dayOfWeek);

        return currentDayOfWeek == targetDayOfWeek;
    }
}