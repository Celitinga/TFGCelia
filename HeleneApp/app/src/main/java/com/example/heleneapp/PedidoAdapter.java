package com.example.heleneapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private final List<Map<String, Object>> pedidos;

    public PedidoAdapter(List<Map<String, Object>> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Map<String, Object> pedido = pedidos.get(position);

        holder.numero.setText(String.valueOf(pedido.get("numeroPedido")));
        holder.estado.setText(String.valueOf(pedido.get("estado")));
        holder.total.setText(String.format("%.2f €", ((long)(Double.parseDouble(String.valueOf(pedido.get("total"))) * 100)) / 100.0));

        Object fecha = pedido.get("fechaCreacion");
        if (fecha != null) {
            String fechaStr = String.valueOf(fecha);
            String[] partes = fechaStr.split("T")[0].split("-");
            holder.fecha.setText(partes[2] + "/" + partes[1] + "/" + partes[0]);
        } else {
            holder.fecha.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {

        TextView numero, estado, total, fecha;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            numero = itemView.findViewById(R.id.numeroPedido);
            estado = itemView.findViewById(R.id.estadoPedido);
            total = itemView.findViewById(R.id.totalPedido);
            fecha = itemView.findViewById(R.id.fechaPedido);
        }
    }
}