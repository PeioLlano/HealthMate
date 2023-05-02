package com.example.healthmate.ChatBot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthmate.Modelo.Mensaje;
import com.example.healthmate.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    // variable para nuestra lista de mensajes y el contexto.
    private ArrayList<Mensaje> listaMensajes;
    private Context contexto;

    // constructor.
    public ChatAdapter(ArrayList<Mensaje> listaMensajes, Context contexto) {
        this.listaMensajes = listaMensajes;
        this.contexto = contexto;
    }

    // crea un nuevo ViewHolder según el tipo de vista.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista;
        switch (viewType) {
            case 0:
                // infla el diseño de mensaje del usuario.
                vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_messages, parent, false);
                return new UsuarioViewHolder(vista);
            case 1:
                // infla el diseño de mensaje del bot.
                vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bot_messages, parent, false);
                return new BotViewHolder(vista);
        }
        return null;
    }

    // vincula los datos con los elementos de la vista.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mensaje mensaje = listaMensajes.get(position);
        switch (mensaje.getRemitente()) {
            case "user":
                // establece el texto del mensaje del usuario en la vista.
                ((UsuarioViewHolder) holder).tvMensajeUser.setText(mensaje.getMensaje());
                break;
            case "bot":
                // establece el texto del mensaje del bot en la vista.
                ((BotViewHolder) holder).tvMensajeBot.setText(mensaje.getMensaje());
                break;
        }
    }

    // devuelve el número total de elementos en la lista.
    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    // devuelve el tipo de vista según la posición en la lista.
    @Override
    public int getItemViewType(int position) {
        switch (listaMensajes.get(position).getRemitente()) {
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return -1;
        }
    }

    // ViewHolder para mensajes del usuario.
    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {

        // variable para el texto del mensaje del usuario.
        TextView tvMensajeUser;

        public UsuarioViewHolder(@NonNull View vista) {
            super(vista);
            // inicializa la variable con el id del TextView en el diseño de mensaje del usuario.
            tvMensajeUser = vista.findViewById(R.id.tvMensajeUser);
        }
    }

    // ViewHolder para mensajes del bot.
    public static class BotViewHolder extends RecyclerView.ViewHolder {

        // variable para el texto del mensaje del bot.
        TextView tvMensajeBot;

        public BotViewHolder(@NonNull View vista) {
            super(vista);
            // inicializa la variable con el id del TextView en el diseño de mensaje del bot.
            tvMensajeBot = vista.findViewById(R.id.tvMensajeBot);
        }
    }
}