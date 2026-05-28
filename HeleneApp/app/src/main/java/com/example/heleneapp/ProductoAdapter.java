package com.example.heleneapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private static final int REQUEST_CODE_EDITAR = 100;
    private List<Producto> lista;
    private Context context;


    public ProductoAdapter(List<Producto> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombre, precio, descripcion;
        ImageView imagen;
        Button btnVer;

        public ViewHolder(View v) {
            super(v);
            nombre = v.findViewById(R.id.txtNombre);
            precio = v.findViewById(R.id.txtPrecio);
            descripcion = v.findViewById(R.id.txtDescripcion);
            imagen = v.findViewById(R.id.imgProducto);
            btnVer = v.findViewById(R.id.btnMasInfo);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        Producto p = lista.get(position);

        h.nombre.setText(p.getNombre());
        h.precio.setText(String.format("%.2f €", p.getPrecioFinal()));
        h.descripcion.setText(p.getDescripcion());
        h.btnVer.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleProductoActivity.class);

            intent.putExtra("nombre", p.getNombre());
            intent.putExtra("descripcion", p.getDescripcion());
            intent.putExtra("precio", p.getPrecioFinal());
            intent.putExtra("imagen", p.getImagenUrl());
            intent.putExtra("productoId", p.getId());
            intent.putExtra("categoriaId", p.getCategoriaId());
            intent.putExtra("descuento", p.getDescuento());

            ((AppCompatActivity) context).startActivityForResult(intent, REQUEST_CODE_EDITAR);
        });

        Glide.with(context)
                .load(p.getImagenUrl())
                .into(h.imagen);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
