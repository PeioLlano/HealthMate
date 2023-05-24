package com.example.healthmate.NotificacionNoEjercicio;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.healthmate.MainActivity;
import com.example.healthmate.R;

public class NoEjercicioReceiver extends BroadcastReceiver {

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        // Aquí puedes crear y mostrar la notificación
        // Puedes utilizar NotificationCompat.Builder para construir la notificación

        // Ejemplo básico de creación de la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.noti)
                .setContentTitle(context.getString(R.string.register_activity_time_title))
                .setContentText(context.getString(R.string.register_activity_time_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Crea un intent para abrir la actividad de registro de actividad cuando se haga clic en la notificación
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            );
        }


        builder.setContentIntent(pendingIntent);

        // Muestra la notificación utilizando el NotificationManager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Añadir canal de notificaciones para poder enviar notificaciones a partir de Android 8.0
        NotificationChannel canalNotificaciones = new NotificationChannel(
                "channel_id",
                "HealthMate",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager.createNotificationChannel(canalNotificaciones);

        canalNotificaciones.setDescription("APK Euroliga");
        canalNotificaciones.enableLights(true);
        canalNotificaciones.setLightColor(Color.RED);

        notificationManager.notify(0, builder.build());
    }
}