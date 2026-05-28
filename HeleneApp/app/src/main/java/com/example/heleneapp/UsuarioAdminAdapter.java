package com.example.heleneapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class UsuarioAdminAdapter extends BaseAdapter {

    private final Context context;
    private final List<Map<String, Object>> listaUsuarios;
    private final OnUsuarioClickListener listener;
    private TextView txtAvatar, txtNombre, txtEmail, txtRol;

    public interface OnUsuarioClickListener {
        void onUsuarioClick(Map<String, Object> usuario);
    }

    public UsuarioAdminAdapter(Context context, List<Map<String, Object>> listaUsuarios, OnUsuarioClickListener listener) {
        this.context = context;
        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return listaUsuarios.size();
    }

    @Override
    public Object getItem(int position) {
        return listaUsuarios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {convertView = LayoutInflater.from(context).inflate(R.layout.item_usuario_admin, parent, false);}

        Map<String, Object> u = listaUsuarios.get(position);

        txtAvatar = convertView.findViewById(R.id.txtAvatar);
        txtNombre = convertView.findViewById(R.id.txtNombreUsuario);
        txtEmail  = convertView.findViewById(R.id.txtEmailUsuario);
        txtRol    = convertView.findViewById(R.id.txtRolUsuario);

        String username = (String) u.get("username");
        String email = u.get("email") != null ? (String) u.get("email") : "Sin email";

        txtAvatar.setText(username != null && !username.isEmpty()
                ? String.valueOf(username.charAt(0)).toUpperCase()
                : "?");

        txtNombre.setText(username);
        txtEmail.setText(email);

        List<Map<String, Object>> roles = (List<Map<String, Object>>) u.get("roles");

        String rolNombre = (roles != null && !roles.isEmpty()) ? (String) roles.get(0).get("nombre") : "SIN ROL";

        txtRol.setText(rolNombre);

        int color;
        switch (rolNombre) {
            case "ADMIN":      color = 0xFFE53935; break;
            case "EMPLEADO":   color = 0xFF1E88E5; break;
            case "SUSCRIPTOR": color = 0xFF43A047; break;
            default:           color = 0xFF9E9E9E; break;
        }

        txtRol.setBackgroundTintList(ColorStateList.valueOf(color));

        convertView.setOnClickListener(v -> listener.onUsuarioClick(u));

        return convertView;
    }
}