package com.example.healthmate.Ejercicio;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.healthmate.Modelo.Ejercicio;
import com.example.healthmate.R;
import com.example.healthmate.Workers.DeleteWorker;
import com.example.healthmate.Workers.InsertWorker;
import com.example.healthmate.Workers.SelectWorker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EjercicioFragment extends Fragment {

    private String username;
    private ListView lvEjercicio;
    private View llVacia;
    private EjercicioAdapter pAdapter;
    private ArrayList<Ejercicio> ejercicios;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Listener para recoger los datos del nuevo evento enviado por el diálogo
         * 'AddEjercicioDialog'.
         */
        getParentFragmentManager().setFragmentResultListener(
                "nuevoEjercicio", this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                        int codigo = bundle.getInt("codigo");
                        String titulo = bundle.getString("titulo");
                        String fecha = bundle.getString("fecha");
                        Double distancia = bundle.getDouble("distancia");
                        String tipo = bundle.getString("tipo");
                        Log.d("EjercicioFragment", "Código = " + codigo + "; titulo = " +
                            titulo + "; fecha = " + fecha + "; distancia = " + distancia +
                            "; tipo = " + tipo);

                        Ejercicio ejercicioNuevo = new Ejercicio(codigo, titulo,new Date(fecha),distancia,tipo);
                        añadirEjercicio(ejercicioNuevo);
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
        return inflater.inflate(R.layout.fragment_ejercicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtenemos la referencia al botón flotante de filtrar
        FloatingActionButton fabFilter = view.findViewById(R.id.fabFilter);

        // Configuramos el listener para el botón de filtrar
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "filter", Toast.LENGTH_SHORT).show();
            }
        });

        // Obtenemos la referencia al botón flotante de añadir
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);

        // Configuramos el listener para el botón de filtrar
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddEjercicioDialog dialog = new AddEjercicioDialog();
                dialog.show(getParentFragmentManager(), "DialogoAñadir");
            }
        });

        // Obtenemos la referencia al botón flotante de descargar
        FloatingActionButton fabDownload = view.findViewById(R.id.fabDownload);

        // Configuramos el listener para el botón de descargar
        fabDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarPermisos(view);
                crearPDF();
            }

        });

        // Obtenemos la referencia a la vista de lista de ejercicios
        lvEjercicio = view.findViewById(R.id.lvEjercicio);

        // Obtenemos la referencia a la vista de "lista vacía"
        llVacia = view.findViewById(R.id.llVacia);
        llVacia.setVisibility(View.INVISIBLE);

        // Ejemplo hasta que decidimamos como almacenamos los datos
        ejercicios = new ArrayList<>();

        //Pedimos todos los ejercicios que tenga el usuario que hemos recibido
        final JSONArray[] jsonArray = {new JSONArray()};

        Data data = new Data.Builder()
                .putString("tabla", "Ejercicios")
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
                                    double Distancia = obj.getDouble("Distancia");
                                    String Tipo = obj.getString("Tipo");


                                    Date fechaImp;
                                    try {
                                        fechaImp = new Date(obj.getString("Fecha"));
                                    } catch (Exception e) {
                                        fechaImp = new Date();
                                    }

                                    ejercicios.add(new Ejercicio(Codigo,Titulo,fechaImp,Distancia,Tipo));

                                    pAdapter.notifyDataSetChanged();

                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        actualizarVacioLleno(ejercicios);
                    }
                });
        WorkManager.getInstance(requireContext()).enqueue(req);

        /*ejercicios.add(new Ejercicio(1,"Carrera de tarde",new Date("12/12/2022"), 8.3, "Running"));
        ejercicios.add(new Ejercicio(2,"Entrenamiento de natacion",new Date("12/2/2023"), 1.23, "Nadar"));
        ejercicios.add(new Ejercicio(3,"Bici por la playa",new Date("19/2/2023"), 19.6, "Ciclismo"));
        ejercicios.add(new Ejercicio(4,"Carrera de mediatarde",new Date("12/12/2022"), 8.3, "Running"));
        ejercicios.add(new Ejercicio(5,"Carrera de tarde",new Date("12/12/2022"), 8.3, "Running"));
        ejercicios.add(new Ejercicio(6,"Entrenamiento de natacion",new Date("12/2/2023"), 1.23, "Nadar"));
        ejercicios.add(new Ejercicio(7,"Bici por la playa",new Date("19/2/2023"), 19.6, "Ciclismo"));
        ejercicios.add(new Ejercicio(8,"Carrera de mediatarde",new Date("12/12/2022"), 8.3, "Running"));
        ejercicios.add(new Ejercicio(9,"Carrera de tarde",new Date("12/12/2022"), 8.3, "Running"));
        ejercicios.add(new Ejercicio(10,"Entrenamiento de natacion",new Date("12/2/2023"), 1.23, "Nadar"));
        ejercicios.add(new Ejercicio(11,"Bici por la playa",new Date("19/2/2023"), 19.6, "Ciclismo"));
        ejercicios.add(new Ejercicio(12,"Carrera de mediatarde",new Date("12/12/2022"), 8.3, "Running"));*/

        // Creamos un adaptador para la lista de ejercicios
        pAdapter = new EjercicioAdapter(requireContext(), ejercicios);

        // Configuramos el adaptador para la vista de lista de ejercicios
        lvEjercicio.setAdapter(pAdapter);

        // Creamos una variable para guardar la posición del elemento a borrar
        final Integer[] posAborrar = {-1};

        // Creamos un cuadro de diálogo para confirmar el borrado de un ejercicio
        AlertDialog.Builder builderG = new AlertDialog.Builder(requireContext());
        builderG.setCancelable(true);
        builderG.setTitle(getString(R.string.delete_measurement));
        builderG.setPositiveButton(R.string.confirm,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Data data = new Data.Builder()
                            .putString("tabla", "Ejercicios")
                            .putString("condicion", "Usuario = '" + ((MainActivity) requireActivity()).cargarLogeado() + "' AND Codigo = '" + ((Ejercicio) pAdapter.getItem(posAborrar[0])).getCodigo() + "'")
                            .build();

                    Constraints constr = new Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build();

                    OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(DeleteWorker.class)
                            .setConstraints(constr)
                            .setInputData(data)
                            .build();

                    WorkManager workManager = WorkManager.getInstance(requireContext());
                    workManager.enqueue(req);

                    workManager.getWorkInfoByIdLiveData(req.getId())
                            .observe(requireActivity(), status -> {
                                if (status != null && status.getState().isFinished()) {
                                    String resultados = status.getOutputData().getString("resultados");
                                    if(resultados.equals("Ok")) {
                                        // Borramos el ejercicio seleccionado y notificamos al adaptador
                                        borrarEjercicio((Ejercicio) pAdapter.getItem(posAborrar[0]));
                                        posAborrar[0] = -1;
                                        pAdapter.notifyDataSetChanged();
                                        actualizarVacioLleno(ejercicios);
                                    }
                                }});
                }
            });
        builderG.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Creamos el cuadro de diálogo
        AlertDialog dialogBorrar = builderG.create();

        // Configuramos el listener para la vista de lista de ejercicios al hacer clic prolongado
        lvEjercicio.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // Guardamos la posición del elemento a borrar
                posAborrar[0] = pos;
                // Mostramos el cuadro de diálogo para confirmar el borrado
                dialogBorrar.show();
                return true;
            }
        });
    }

    // Método para borrar un ejercicio (se implementa fuera del método onCreate)
    private void borrarEjercicio (Ejercicio item){
        // Aquí se implementaría la lógica para borrar el ejercicio
        ejercicios.remove(item);
    }

    private void añadirEjercicio(Ejercicio item){
        // Define el formato deseado para la fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        // Formatea la fecha utilizando el formato definido
        String formattedDate = dateFormat.format(item.getFecha());

        //Hacemos try de insertar el grupo para mostrar un toast en caso de que no se pueda insertar
        Data data = new Data.Builder()
                .putString("tabla", "Ejercicios")
                .putStringArray("keys", new String[]{"Usuario","Titulo","Distancia","Fecha","Tipo"})
                .putStringArray("values", new String[]{((MainActivity) getActivity()).cargarLogeado(),item.getTitulo(), item.getDistancia().toString(), formattedDate, item.getTipo()})
                .build();

        Constraints constr = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(InsertWorker.class)
                .setConstraints(constr)
                .setInputData(data)
                .build();

        WorkManager workManager = WorkManager.getInstance(requireContext());
        workManager.enqueue(req);

        workManager.getWorkInfoByIdLiveData(req.getId())
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) {
                        Boolean resultados = status.getOutputData().getBoolean("resultado", false);
                        Integer id = status.getOutputData().getInt("id", -1);

                        if(resultados) {
                            item.setCodigo(id);
                            ejercicios.add(item);
                            pAdapter.notifyDataSetChanged();
                            actualizarVacioLleno(ejercicios);
                        }
                        else {
                            Toast aviso = Toast.makeText(requireActivity(), getResources().getString(R.string.error), Toast.LENGTH_SHORT);
                            aviso.show();
                        }
                    }});
    }

    //Actualizar lo que se ve dependiendo del tamaño de la lista.
    private void actualizarVacioLleno(ArrayList<Ejercicio> grupos) {

        if(grupos.size() > 0) {
            llVacia.setVisibility(View.GONE);
        } else {
            llVacia.setVisibility(View.VISIBLE);
        }
    }

    private void crearPDF() {
        try {
            String carpeta = "/archivospdf";
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + carpeta;

            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
                Toast.makeText(requireContext(), "CARPETA CREADA", Toast.LENGTH_SHORT).show();
            }

            File archivo = new File(dir, "ejercicios.pdf");
            FileOutputStream fos = new FileOutputStream(archivo);

            Document documento = new Document();
            PdfWriter.getInstance(documento, fos);

            documento.open();

            Paragraph titulo = new Paragraph(
                    "HealthMate    -    Lista de ejercicios\n\n",
                    FontFactory.getFont("times new roman", 22, Font.BOLD)
            );
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            PdfPTable tabla = new PdfPTable(4);
            tabla.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.deleteBodyRows();

            tabla.addCell("Titulo");
            tabla.addCell("Fecha");
            tabla.addCell("Tipo");
            tabla.addCell("Distancia (kms)");

            // Define el formato deseado para la fecha
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");


            for (int i = 0; i < ejercicios.size(); i++) {
                tabla.addCell(ejercicios.get(i).getTitulo());

                // Formatea la fecha utilizando el formato definido
                String formattedDate = dateFormat.format(ejercicios.get(i).getFecha());
                tabla.addCell(formattedDate);

                tabla.addCell(ejercicios.get(i).getTipo());
                tabla.addCell(String.valueOf(ejercicios.get(i).getDistancia()));
            }

            documento.add(tabla);

            documento.close();

            openPDF();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void openPDF() {
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Download/archivospdf/ejercicios.pdf");

        Uri pdfUri = FileProvider.getUriForFile(requireContext(), "com.example.healthmate.fileprovider", pdfFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "No se encontró ninguna aplicación para abrir el archivo PDF", Toast.LENGTH_SHORT).show();
        }
    }


    private void verificarPermisos(View view) {
        if (checkPermission()){

        }
        else {
            //Android is below 11(R)
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    100
            );
        }
    }

    public boolean checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            return Environment.isExternalStorageManager();
        }
        else{
            //Android is below 11(R)
            int write = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }
}