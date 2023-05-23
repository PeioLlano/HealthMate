package com.example.healthmate.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.RemoteViews;

import com.example.healthmate.MainActivity;
import com.example.healthmate.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class TextWidget extends AppWidgetProvider {

    private static ArrayList<String> frases = new ArrayList<>();
    private static final int UPDATE_INTERVAL = 5000; // 5 segundos en milisegundos
    private Handler handler = new Handler();


    // Método que actualiza el widget
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Crear las vistas del widget
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.text_widget);

        // Seleccionar una frase aleatoria de la lista de frases
        int numeroAleatorio = 0 + new Random().nextInt(frases.size() - 1);

        // Establecer la frase seleccionada en la vista del widget
        views.setTextViewText(R.id.appwidget_text, frases.get(numeroAleatorio));

        // Crear un intent para lanzar la actividad Login cuando se haga clic en el widget
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Establecer el intent como acción al hacer clic en la vista del widget
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        // Actualizar la vista del widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Si la lista de frases está vacía, cargar las frases desde un archivo de recursos
        if (frases.size() == 0){
            InputStream fich = context.getResources().openRawResource(R.raw.frases);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fich));
            try {
                String line = reader.readLine();
                while (line != null) {
                    line = reader.readLine();
                    frases.add(line);
                }
                reader.close();
                fich.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        handler.postDelayed(updateWidgetRunnable(context, appWidgetManager, appWidgetIds), UPDATE_INTERVAL);
    }

    private Runnable updateWidgetRunnable(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        return new Runnable() {
            @Override
            public void run() {
                for (int appWidgetId : appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId);
                }
                // Programar la próxima actualización
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        InputStream fich = context.getResources().openRawResource(R.raw.frases);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fich));
        try {
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                frases.add(line);
            }
            reader.close();
            fich.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}