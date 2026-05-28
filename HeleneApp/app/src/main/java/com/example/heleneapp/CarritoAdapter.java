package com.example.heleneapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.heleneapp.network.ApiClient;

import java.util.List;
import java.util.Map;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {

    private CarritoActivity activity;
    private List<CarritoItem> lista;
    private Context context;
    private ApiClient apiClient;

    public CarritoAdapter(List<CarritoItem> lista, Context context, ApiClient apiClient, CarritoActivity activity) {
        this.lista = lista;
        this.context = context;
        this.apiClient = apiClient;
        this.activity = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView nombre, precio, cantidad, subtotal;
        ImageButton mas, menos;

        @SuppressLint("WrongViewCast")
        public ViewHolder(View v) {
            super(v);

            img = v.findViewById(R.id.imgProducto);
            nombre = v.findViewById(R.id.nombreProducto);
            precio = v.findViewById(R.id.precioProducto);
            cantidad = v.findViewById(R.id.cantidad);
            subtotal = v.findViewById(R.id.subtotal);

            mas = v.findViewById(R.id.btnMas);
            menos = v.findViewById(R.id.btnMenos);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_carrito, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        CarritoItem item = lista.get(position);

        h.nombre.setText(item.getNombreProducto());
        h.precio.setText(String.format("%.2f", item.getPrecioProducto()) + " €");
        h.cantidad.setText(String.valueOf(item.getCantidad()));
        h.subtotal.setText("Subtotal: " + String.format("%.2f", item.getSubtotal()) + " €");

        Glide.with(context)
                .load(item.getImagenUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(h.img);

        h.mas.setOnClickListener(v -> {
            actualizarCantidad(item, position, item.getCantidad() + 1);
        });

        h.menos.setOnClickListener(v -> {

            int nuevaCantidad = item.getCantidad() - 1;

            if (nuevaCantidad <= 0) {
                eliminarItem(item, position);
            } else {
                actualizarCantidad(item, position, nuevaCantidad);
            }
        });
    }

    private void actualizarCantidad(CarritoItem item, int position, int nuevaCantidad) {

        String url = "/carrito/" + item.getProductoId() + "?cantidad=" + nuevaCantidad;

        apiClient.putRaw(url, "", new ApiClient.ApiCallback<Map<String, Object>>() {

                    @Override
                    public void onSuccess(Map<String, Object> response) {

                        item.setCantidad(nuevaCantidad);
                        notifyItemChanged(position);

                        NotificationHelper.showToast(context, "Carrito actualizado");

                        activity.actualizarUI();
                    }
                    @Override
                    public void onError(String error) {
                        NotificationHelper.showToast(context, error);
                    }
                }
        );
    }
    private void eliminarItem(CarritoItem item, int position) {

        String url = "/carrito/" + item.getProductoId();

        apiClient.delete(url, new ApiClient.ApiCallback<Map<String, Object>>() {

                    @Override
                    public void onSuccess(Map<String, Object> response) {

                        lista.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, lista.size());

                        NotificationHelper.showToast(context, "Producto eliminado del carrito");

                        activity.actualizarUI();
                    }

                    @Override
                    public void onError(String error) {
                        NotificationHelper.showToast(context, error);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}