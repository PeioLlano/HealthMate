package com.example.healthmate.Ejercicio;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.healthmate.Modelo.Ejercicio;
import com.example.healthmate.R;
import com.example.healthmate.Workers.InsertWorker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
                dialog.show(getActivity().getSupportFragmentManager(), "DialogoAñadir");
            }
        });

        // Obtenemos la referencia a la vista de lista de ejercicios
        lvEjercicio = view.findViewById(R.id.lvEjercicio);

        // Obtenemos la referencia a la vista de "lista vacía"
        llVacia = view.findViewById(R.id.llVacia);

        // Ejemplo hasta que decidimamos como almacenamos los datos
        ejercicios = new ArrayList<>();
        ejercicios.add(new Ejercicio(1,"Carrera de tarde",new Date("12/12/2022"), 8.3, "Running"));
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
        ejercicios.add(new Ejercicio(12,"Carrera de mediatarde",new Date("12/12/2022"), 8.3, "Running"));

        // Creamos un adaptador para la lista de ejercicios
        pAdapter = new EjercicioAdapter(requireContext(), ejercicios);

        // Configuramos el adaptador para la vista de lista de ejercicios
        lvEjercicio.setAdapter(pAdapter);

        // Si el adaptador no contiene elementos, mostramos la vista de "lista vacía"
        if (pAdapter.getCount() == 0) {
            llVacia.setVisibility(View.VISIBLE);
            // lvMediciones.setVisibility(View.GONE);
        }

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
                    // Borramos el ejercicio seleccionado y notificamos al adaptador
                    borrarEjercicio((Ejercicio) pAdapter.getItem(posAborrar[0]));
                    posAborrar[0] = -1;
                    pAdapter.notifyDataSetChanged();
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
        //Hacemos try de insertar el grupo para mostrar un toast en caso de que no se pueda insertar
        Data data = new Data.Builder()
                .putString("tabla", "Mediciones")
                .putStringArray("keys", new String[]{"Usuario","Titulo","Distancia","Fecha","Tipo"})
                .putStringArray("values", new String[]{username,item.getTitulo(), item.getDistancia().toString(), item.getFecha().toString(), item.getTipo()})
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

}