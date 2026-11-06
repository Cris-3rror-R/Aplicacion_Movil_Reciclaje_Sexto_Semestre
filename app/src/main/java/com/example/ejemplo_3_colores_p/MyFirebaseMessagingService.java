package com.example.ejemplo_3_colores_p;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        
        String title = "Gestión de residuos";
        String body = "Has recibido una nueva recomendación";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }
        
        showNotification(title, body);
    }

    private void showNotification(String title, String message) {
        // Se utiliza recicla5 como icono de notificación para mantener la consistencia
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "residuos_channel")
                .setSmallIcon(R.drawable.recicla5)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                == PackageManager.PERMISSION_GRANTED) {
            manager.notify(1, builder.build());
        } else {
            Log.w("FCM", "No se mostró la notificación: Permiso POST_NOTIFICATIONS no concedido.");
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Nuevo token: " + token);
    }
}
