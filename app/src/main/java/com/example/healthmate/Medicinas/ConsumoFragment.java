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

import com.example.healthmate.MainActivity;
import com.example.healthmate.Medicinas.ConsumoAdapter;
import com.example.healthmate.Modelo.Medicina;
import com.example.healthmate.R;
import com.example.healthmate.Workers.DeleteWorker;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ConsumoFragment extends Fragment {

    private String username;
    private ListView lvconsumos;
    private View llVacia;
    private ConsumoAdapter pAdapter;
    private ArrayList<Medicina> consumos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Listener para recoger los datos del nuevo evento enviado por el diálogo
         * 'AddMedicinaDialog'.
         */
        getParentFragmentManager().setFragmentResultListener(
                "nuevaMedicina", this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {

                    }
                });
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

        /*//Pedimos todos los ejercicios que tenga el usuario que hemos recibido
        final JSONArray[] jsonArray = {new JSONArray()};

        Data data = new Data.Builder()
                .putString("tabla", "consumos")
                .putString("condicion", "Usuario='"+((MainActivity) getActivity()).cargarLogeado()+"'")
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
                                    String Titulo = obj.getString("Titulo");
                                    String Medicina = obj.getString("Medicina");
                                    String Tipo = obj.getString("Tipo");


                                    Date fechaImp;
                                    try {
                                        fechaImp = new Date(obj.getString("Fecha"));
                                    } catch (Exception e) {
                                        fechaImp = new Date();
                                    }

                                    consumos.add(new Medicina(Codigo,Titulo,fechaImp,Medicina,Tipo));

                                    pAdapter.notifyDataSetChanged();

                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        actualizarVacioLleno(consumos);
                    }
                });
        WorkManager.getInstance(requireContext()).enqueue(req);*/
        ArrayList<String> lista = new ArrayList<>();
        lista.add("Lunes");
        lista.add("Jueves");
        lista.add("Domingo");

        ArrayList<String> lista2 = new ArrayList<>();
        lista2.add("Lunes");
        lista2.add("Martes");
        lista2.add("Miercoles");
        lista2.add("Jueves");
        lista2.add("Viernes");
        lista2.add("Sabado");
        lista2.add("Domingo");


        consumos.add(new Medicina(1,"Ibuprofeno","10:00", lista));
        consumos.add(new Medicina(2,"Gelocatil","20:00", lista));
        consumos.add(new Medicina(3,"Cronotolis","15:00", lista2));


        // Creamos un adaptador para la lista de consumos
        pAdapter = new ConsumoAdapter(requireContext(), consumos);

        // Configuramos el adaptador para la vista de lista de consumos
        lvconsumos.setAdapter(pAdapter);

        actualizarVacioLleno(consumos);
    }

    //Actualizar lo que se ve dependiendo del tamaño de la lista.
    private void actualizarVacioLleno(ArrayList<Medicina> grupos) {

        if(grupos.size() > 0) {
            llVacia.setVisibility(View.GONE);
        } else {
            llVacia.setVisibility(View.VISIBLE);
        }
    }
}