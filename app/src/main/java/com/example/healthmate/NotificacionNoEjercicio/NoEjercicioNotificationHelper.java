package com.example.healthmate.NotificacionNoEjercicio;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class NoEjercicioNotificationHelper {
    private static final int NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_HOUR = 20; // Hora en formato de 24 horas (ejemplo: 20 para las 8:00 PM)

    public static void scheduleNotification(Context context) {
        // Obtén el gestor de alarmas
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Crea un intent para el receptor de la notificación
        Intent intent = new Intent(context, NoEjercicioReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_IMMUTABLE);

        // Calcula la hora de la notificación
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Comprueba si la hora actual ya ha pasado la hora de la notificación
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            // Si la hora ya ha pasado, programa la notificación para el día siguiente
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Configura la alarma para que se active a la hora especificada
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void cancelNotification(Context context) {
        // Obtén el gestor de alarmas
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Crea un intent para el receptor de la notificación
        Intent intent = new Intent(context, NoEjercicioReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancela la alarma asociada con el intent
        alarmManager.cancel(pendingIntent);
    }
}
