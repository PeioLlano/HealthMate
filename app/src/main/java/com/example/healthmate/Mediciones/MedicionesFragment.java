package com.example.healthmate.Mediciones;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

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

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.healthmate.Ejercicio.AddEjercicioDialog;
import com.example.healthmate.Ejercicio.EjercicioAdapter;
import com.example.healthmate.Ejercicio.FilterEjercicioDialog;
import com.example.healthmate.MainActivity;
import com.example.healthmate.Modelo.Ejercicio;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class MedicionesFragment extends Fragment {

    private String username;
    private ListView lvMediciones;
    private View llVacia;
    private MedicionAdapter pAdapter;
    private ArrayList<Medicion> mediciones;
    private ArrayList<Medicion> listaFiltrada;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Listener para recoger los datos del nuevo evento enviado por el diálogo
         * 'AddMedicionDialog'.
         */
        getParentFragmentManager().setFragmentResultListener(
                "nuevaMedicion", this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                        String titulo = bundle.getString("titulo");
                        String fecha = bundle.getString("fecha");
                        Log.d("fecha", fecha);
                        String fechabien = new Date().toString();
                        if (fecha != "") {
                            fechabien = fecha.split("/")[1] + "/" + fecha.split("/")[0] + "/" + fecha.split("/")[2];
                        }
                        Log.d("fechabien", fechabien);
                        String medicion = bundle.getString("medicion");
                        String tipo = bundle.getString("tipo");
                        Log.d("MedicionesFragment", "titulo = " +
                                titulo + "; fecha = " + fecha + "; medicion = " + medicion +
                                "; tipo = " + tipo);

                        Medicion medicionNuevo = new Medicion(-1, titulo,new Date(fechabien),medicion,tipo);
                        añadirMedicion(medicionNuevo);
                    }
                });

        /*
         * Listener para recoger los datos del nuevo evento enviado por el diálogo
         * 'FilterMedicionDialog'.
         */
        getParentFragmentManager().setFragmentResultListener(
                "filtro", this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                        // FILTRAR POR TIPO
                        String tipo = bundle.getString("Tipo");
                        Log.d("MedicionesFragment", "tipo = " + tipo);
                        listaFiltrada = mediciones;
                        if (tipo != null) {
                            listaFiltrada = mediciones.stream()
                                    .filter(medicion -> medicion.getTipo().equals(tipo))
                                    .collect(Collectors.toCollection(ArrayList::new));
                        }

                        // FILTRAR POR FECHA
                        String fecha = bundle.getString("Fecha");
                        Log.d("MedicionesFragment", "fecha = " + fecha);
                        if (fecha != null) {
                            listaFiltrada = listaFiltrada.stream()
                                    .filter(medicion -> medicion.getDiaString().equals(fecha))
                                    .collect(Collectors.toCollection(ArrayList::new));
                        }

                        // FILTRAR POR NOMBRE
                        String nombre = bundle.getString("Nombre");
                        Log.d("MedicionesFragment", "Nombre = " + nombre);
                        if (nombre != null) {
                            listaFiltrada = listaFiltrada.stream()
                                    .filter(medicion -> medicion.isInTitulo(nombre))
                                    .collect(Collectors.toCollection(ArrayList::new));
                        }

                        Log.d("MedicionesFragment", "mediciones = " + mediciones.size());
                        MedicionAdapter pAdapterFiltro = new MedicionAdapter(requireContext(),
                            listaFiltrada);
                        lvMediciones.setAdapter(pAdapterFiltro);
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
        return inflater.inflate(R.layout.fragment_mediciones, container, false);
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
                FilterMedicionDialog dialog = new FilterMedicionDialog();
                dialog.show(getParentFragmentManager(), "DialogoFiltrarMedicion");
            }
        });

        // Obtenemos la referencia al botón flotante de añadir
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);

        // Configuramos el listener para el botón de añadir
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMedicionDialog dialog = new AddMedicionDialog();
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

        // Obtenermos la referencia al botón flotante de borrar filtros
        FloatingActionButton fabRmFiltros = view.findViewById(R.id.fabRmFiltros);

        // Configuramos el listener para el botón de borrar filtros
        fabRmFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MedicionAdapter pAdapter = new MedicionAdapter(requireContext(), mediciones);
                lvMediciones.setAdapter(pAdapter);
                listaFiltrada = null;
            }
        });

        // Obtenemos la referencia a la vista de lista de mediciones
        lvMediciones = view.findViewById(R.id.lvMediciones);

        // Obtenemos la referencia a la vista de "lista vacía"
        llVacia = view.findViewById(R.id.llVacia);

        // Ejemplo hasta que decidimamos como almacenamos los datos
        mediciones = new ArrayList<>();

        //Pedimos todos los ejercicios que tenga el usuario que hemos recibido
        final JSONArray[] jsonArray = {new JSONArray()};

        Data data = new Data.Builder()
                .putString("tabla", "Mediciones")
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
                                    String Medicion = obj.getString("Medicion");
                                    String Tipo = obj.getString("Tipo");


                                    Date fechaImp;
                                    try {
                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        fechaImp = df.parse(obj.getString("Fecha"));
                                    } catch (Exception e) {
                                        fechaImp = new Date();
                                    }

                                    mediciones.add(new Medicion(Codigo,Titulo,fechaImp,Medicion,Tipo));

                                    pAdapter.notifyDataSetChanged();

                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        actualizarVacioLleno(mediciones);
                    }
                });
        WorkManager.getInstance(requireContext()).enqueue(req);
/*
        mediciones.add(new Medicion(1,"Medicion de peso",new Date("12/12/2022"), "88 kg", "Peso"));
        mediciones.add(new Medicion(2,"Medicion de altura",new Date("12/2/2023"), "189 cm", "Altura"));
        mediciones.add(new Medicion(3,"Medicion de IMC",new Date("19/2/2023"), "19.8", "IMC"));
        mediciones.add(new Medicion(4,"Medicion de FC",new Date("21/3/2023"), "190 ppm", "Frecuencia cardíaca"));
        mediciones.add(new Medicion(4,"Medicion de PA",new Date("1/3/2023"), "140 mmHg", "Presión arterial"));
        mediciones.add(new Medicion(4,"Medicion de NOS",new Date("23/3/2023"), "93 %", "Nivel de oxígeno en sangre"));
*/

        // Creamos un adaptador para la lista de mediciones
        pAdapter = new MedicionAdapter(requireContext(), mediciones);

        // Configuramos el adaptador para la vista de lista de mediciones
        lvMediciones.setAdapter(pAdapter);

        // Creamos una variable para guardar la posición del elemento a borrar
        final Integer[] posAborrar = {-1};

        // Creamos un cuadro de diálogo para confirmar el borrado de una medición
        AlertDialog.Builder builderG = new AlertDialog.Builder(requireContext());
        builderG.setCancelable(true);
        builderG.setTitle(getString(R.string.delete_measurement));
        builderG.setPositiveButton(R.string.confirm,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Borramos la medición seleccionada y notificamos al adaptador
                    Data data = new Data.Builder()
                            .putString("tabla", "Mediciones")
                            .putString("condicion", "Usuario = '" + ((MainActivity) requireActivity()).cargarLogeado() + "' AND Codigo = '" + ((Medicion) pAdapter.getItem(posAborrar[0])).getCodigo() + "'")
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
                                        borrarMedicion((Medicion) pAdapter.getItem(posAborrar[0]));
                                        posAborrar[0] = -1;
                                        pAdapter.notifyDataSetChanged();
                                        actualizarVacioLleno(mediciones);
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

        // Configuramos el listener para la vista de lista de mediciones al hacer clic prolongado
        lvMediciones.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
    private void borrarMedicion (Medicion item){
        // Aquí se implementaría la lógica para borrar la medición
        mediciones.remove(item);
    }

    private void añadirMedicion(Medicion item){

        // Define el formato deseado para la fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        // Formatea la fecha utilizando el formato definido
        String formattedDate = dateFormat.format(item.getFecha());


        //Hacemos try de insertar el grupo para mostrar un toast en caso de que no se pueda insertar
        Data data = new Data.Builder()
                .putString("tabla", "Mediciones")
                .putStringArray("keys", new String[]{"Usuario","Titulo","Medicion","Fecha","Tipo"})
                .putStringArray("values", new String[]{((MainActivity) getActivity()).cargarLogeado(),item.getTitulo(), item.getMedicion(), formattedDate, item.getTipo()})
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
                            mediciones.add(item);
                            pAdapter.notifyDataSetChanged();
                            actualizarVacioLleno(mediciones);
                        }
                        else {
                            Toast aviso = Toast.makeText(requireActivity(), getResources().getString(R.string.error), Toast.LENGTH_SHORT);
                            aviso.show();
                        }
                    }});
    }

    //Actualizar lo que se ve dependiendo del tamaño de la lista.
    private void actualizarVacioLleno(ArrayList<Medicion> grupos) {

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

            File archivo = new File(dir, "mediciones.pdf");
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
            Paragraph titulo = new Paragraph("HealthMate - Lista de mediciones\n\n", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20f);
            //titulo.setBackgroundColor(new BaseColor(197, 232, 255));
            documento.add(titulo);

            // Crea la tabla con colores de fondo alternados para las filas
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.setSpacingBefore(10f);

            tabla.addCell(createCell("Titulo", encabezadoFont, colorVerde));
            tabla.addCell(createCell("Fecha", encabezadoFont, colorVerde));
            tabla.addCell(createCell("Tipo", encabezadoFont, colorVerde));
            tabla.addCell(createCell("Medición", encabezadoFont, colorVerde));

            // Define los colores de fondo alternados para las filas de la tabla
            BaseColor colorFondo1 = new BaseColor(255, 255, 255); // Blanco
            BaseColor colorFondo2 = new BaseColor(240, 240, 240); // Gris claro
            boolean fondoAlternado = false;

            // Define el formato deseado para la fecha
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            if (listaFiltrada == null) {
                for (int i = 0; i < mediciones.size(); i++) {
                    tabla.addCell(createCell(mediciones.get(i).getTitulo(), contenidoFont, fondoAlternado ? colorFondo1 : colorFondo2));
                    tabla.addCell(createCell(dateFormat.format(mediciones.get(i).getFecha()), contenidoFont, fondoAlternado ? colorFondo1 : colorFondo2));
                    tabla.addCell(createCell(mediciones.get(i).getTipo(), contenidoFont, fondoAlternado ? colorFondo1 : colorFondo2));
                    tabla.addCell(createCell(String.valueOf(mediciones.get(i).getMedicion()), contenidoFont, fondoAlternado ? colorFondo1 : colorFondo2));

                    fondoAlternado = !fondoAlternado; // Cambia el color de fondo para la siguiente fila
                }
            }
            else{
                for (int i = 0; i < listaFiltrada.size(); i++) {
                    tabla.addCell(createCell(listaFiltrada.get(i).getTitulo(), contenidoFont, fondoAlternado ? colorFondo1 : colorFondo2));
                    tabla.addCell(createCell(dateFormat.format(listaFiltrada.get(i).getFecha()), contenidoFont, fondoAlternado ? colorFondo1 : colorFondo2));
                    tabla.addCell(createCell(listaFiltrada.get(i).getTipo(), contenidoFont, fondoAlternado ? colorFondo1 : colorFondo2));
                    tabla.addCell(createCell(String.valueOf(listaFiltrada.get(i).getMedicion()), contenidoFont, fondoAlternado ? colorFondo1 : colorFondo2));

                    fondoAlternado = !fondoAlternado; // Cambia el color de fondo para la siguiente fila
                }
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
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Download/archivospdf/mediciones.pdf");

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