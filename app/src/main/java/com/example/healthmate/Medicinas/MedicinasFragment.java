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
import android.util.Log;
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
import com.example.healthmate.Medicinas.MedicinaAdapter;
import com.example.healthmate.Mediciones.AddMedicionDialog;
import com.example.healthmate.Modelo.Medicina;
import com.example.healthmate.Modelo.Medicion;
import com.example.healthmate.R;
import com.example.healthmate.Workers.DeleteWorker;
import com.example.healthmate.Workers.InsertWorker;
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
import java.util.Date;

public class MedicinasFragment extends Fragment {

    private String username;
    private ListView lvMedicinas;
    private View llVacia;
    private MedicinaAdapter pAdapter;
    private ArrayList<Medicina> medicinas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Listener para recoger los datos del nuevo evento enviado por el diálogo
         * 'AddMedicionDialog'.
         */
        getParentFragmentManager().setFragmentResultListener(
                "nuevaMedicina", this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                        String nombre = bundle.getString("nombre");
                        String dias = bundle.getString("dias");
                        String hora = bundle.getString("hora");

                        Medicina medicinaNuevo = new Medicina(-1, nombre,hora,stringArrayList(dias));
                        añadirMedicina(medicinaNuevo);
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
        return inflater.inflate(R.layout.fragment_medicinas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtenemos la referencia al botón flotante de añadir
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);

        // Configuramos el listener para el botón de añadir
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMedicinaDialog dialog = new AddMedicinaDialog();
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

        // Obtenemos la referencia a la vista de lista de medicinas
        lvMedicinas = view.findViewById(R.id.lvMedicinas);

        // Obtenemos la referencia a la vista de "lista vacía"
        llVacia = view.findViewById(R.id.llVacia);

        // Ejemplo hasta que decidimamos como almacenamos los datos
        medicinas = new ArrayList<>();

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

                                    medicinas.add(new Medicina(Codigo,Nombre,Hora,stringArrayList(Dias)));

                                    pAdapter.notifyDataSetChanged();

                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        actualizarVacioLleno(medicinas);
                    }
                });
        WorkManager.getInstance(requireContext()).enqueue(req);

        // Creamos un adaptador para la lista de medicinas
        pAdapter = new MedicinaAdapter(requireContext(), medicinas);

        // Configuramos el adaptador para la vista de lista de medicinas
        lvMedicinas.setAdapter(pAdapter);

        // Creamos una variable para guardar la posición del elemento a borrar
        final Integer[] posAborrar = {-1};

        // Creamos un cuadro de diálogo para confirmar el borrado de una medición
        AlertDialog.Builder builderG = new AlertDialog.Builder(requireContext());
        builderG.setCancelable(true);
        builderG.setTitle(getString(R.string.delete_medicine));
        builderG.setPositiveButton(R.string.confirm,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Borramos la medición seleccionada y notificamos al adaptador
                    Data data = new Data.Builder()
                            .putString("tabla", "Medicinas")
                            .putString("condicion", "Usuario = '" + ((MedicinasActivity) requireActivity()).cargarLogeado() + "' AND Codigo = '" + ((Medicina) pAdapter.getItem(posAborrar[0])).getCodigo() + "'")
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
                                        borrarMedicina((Medicina) pAdapter.getItem(posAborrar[0]));
                                        posAborrar[0] = -1;
                                        pAdapter.notifyDataSetChanged();
                                        actualizarVacioLleno(medicinas);
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

        // Configuramos el listener para la vista de lista de medicinas al hacer clic prolongado
        lvMedicinas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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

    // Método para borrar una medición (se implementa fuera del método onCreate)
    private void borrarMedicina (Medicina item){
        // Aquí se implementaría la lógica para borrar la medición
        medicinas.remove(item);
    }

    private void añadirMedicina(Medicina item){

        //Hacemos try de insertar el grupo para mostrar un toast en caso de que no se pueda insertar
        Data data = new Data.Builder()
                .putString("tabla", "Medicinas")
                .putStringArray("keys", new String[]{"Usuario","Nombre","Hora","Dias"})
                .putStringArray("values", new String[]{((MedicinasActivity) getActivity()).cargarLogeado(),item.getNombre(), item.getHora(), convertListToString(item.getDias())})
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
                        if(resultados) {
                            medicinas.add(item);
                            pAdapter.notifyDataSetChanged();
                            actualizarVacioLleno(medicinas);
                        }
                        else {
                            Toast aviso = Toast.makeText(requireActivity(), getResources().getString(R.string.error), Toast.LENGTH_SHORT);
                            aviso.show();
                        }
                    }});
    }

    //Actualizar lo que se ve dependiendo del tamaño de la lista.
    private void actualizarVacioLleno(ArrayList<Medicina> grupos) {

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

            File archivo = new File(dir, "medicinas.pdf");
            FileOutputStream fos = new FileOutputStream(archivo);

            Document documento = new Document();
            PdfWriter.getInstance(documento, fos);

            // Define los colores personalizados
            BaseColor colorAzul = new BaseColor(33, 150, 243);
            BaseColor colorVerde = new BaseColor(165,165, 167);

            // Define los estilos de fuente personalizados
            Font tituloFont = FontFactory.getFont("times new roman", 22, Font.BOLD, colorAzul);
            Font encabezadoFont = FontFactory.getFont("times new roman", 12, Font.BOLD);
            Font contenidoFont = FontFactory.getFont("times new roman", 12);

            documento.open();

            // Agrega el título con un fondo azul claro
            Paragraph titulo = new Paragraph("HealthMate - Lista de medicinas\n\n", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20f);
            //titulo.setBackgroundColor(new BaseColor(197, 232, 255));
            documento.add(titulo);

            // Crea la tabla con colores de fondo alternados para las filas
            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidthPercentage(100);
            tabla.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.setSpacingBefore(10f);

            tabla.addCell(createCell("Nombre", encabezadoFont, colorVerde));
            tabla.addCell(createCell("Días", encabezadoFont, colorVerde));
            tabla.addCell(createCell("Hora", encabezadoFont, colorVerde));

            // Define los colores de fondo alternados para las filas de la tabla
            BaseColor colorFondo1 = new BaseColor(255, 255, 255); // Blanco
            BaseColor colorFondo2 = new BaseColor(240, 240, 240); // Gris claro
            boolean fondoAlternado = false;

            // Define el formato deseado para la fecha
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            for (int i = 0; i < medicinas.size(); i++) {
                tabla.addCell(createCell(medicinas.get(i).getNombre(), contenidoFont, fondoAlternado ? colorFondo1 : colorFondo2));
                tabla.addCell(createCell(medicinas.get(i).concatenateWithCommasAndAmpersand(medicinas.get(i).getDias()), contenidoFont, fondoAlternado ? colorFondo1 : colorFondo2));
                tabla.addCell(createCell(medicinas.get(i).getHora(), contenidoFont, fondoAlternado ? colorFondo1 : colorFondo2));

                fondoAlternado = !fondoAlternado; // Cambia el color de fondo para la siguiente fila
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

    // Método auxiliar para crear una celda de la tabla con estilo personalizado
    private PdfPCell createCell(String contenido, Font font, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(contenido, font));
        cell.setPadding(8f);
        cell.setBackgroundColor(backgroundColor);
        return cell;
    }

    private void openPDF() {
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Download/archivospdf/medicinas.pdf");

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

    private ArrayList<String> stringArrayList(String str){
        // Separar el string por comas y obtener un array de strings
        String[] elementos = str.split(",");

        // Crear un ArrayList y agregar los elementos al mismo
        ArrayList<String> lista = new ArrayList<>(Arrays.asList(elementos));

        return lista;
    }

    private String convertListToString(ArrayList<String> lista) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < lista.size(); i++) {
            stringBuilder.append(lista.get(i));
            if (i < lista.size() - 1) {
                stringBuilder.append(",");
            }
        }

        return stringBuilder.toString();
    }

}