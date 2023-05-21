package com.example.healthmate.Firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.healthmate.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {

    public ServicioFirebase() {
    }

    // Este método es llamado cuando se recibe un mensaje desde Firebase Cloud Messaging
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Comprueba si el mensaje contiene datos
        if (remoteMessage.getData().size() > 0) {
            // Obtiene el servicio de notificaciones
            NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            String title = remoteMessage.getData().get("title");
            String text = remoteMessage.getData().get("body");

            // Crea un constructor de notificaciones
            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(getApplicationContext(), "IdCanal");
            elBuilder.setSmallIcon(R.drawable.noti)
                    .setContentTitle(title)
                    .setContentText(text)
                    //.addAction(0, getString(R.string.desplegar_ajustes), intentEnNot)
                    .setVibrate(new long[]{0, 1000, 500, 1000})
                    .setAutoCancel(true);

            // Si el dispositivo tiene una versión de Android mayor o igual a Oreo (API 26)
            // crea un canal de notificación
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal",
                        NotificationManager.IMPORTANCE_DEFAULT);
                elManager.createNotificationChannel(elCanal);
            }

            // Envía la notificación
            elManager.notify(1, elBuilder.build());
        }

        // Comprueba si el mensaje tiene una notificación asociada
        if (remoteMessage.getNotification() != null) {
            // En este caso no se realiza ninguna acción adicional
        }
    }
}