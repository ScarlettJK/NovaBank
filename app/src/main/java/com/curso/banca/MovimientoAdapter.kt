package com.curso.banca

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.curso.banca.network.TransactionResponse
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MovimientoAdapter(
    private var lista: List<TransactionResponse>
) : RecyclerView.Adapter<MovimientoAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivDireccion: ImageView = view.findViewById(R.id.ivDireccion)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvMonto: TextView = view.findViewById(R.id.tvMonto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movimiento, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mov = lista[position]

        val fmt = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        val monto = fmt.format(mov.amount / 100.0)

        if (mov.direction == "out") {
            holder.tvMonto.text = "-$monto"
            holder.tvMonto.setTextColor(Color.parseColor("#B71C1C"))
        } else {
            holder.tvMonto.text = "+$monto"
            holder.tvMonto.setTextColor(Color.parseColor("#1B5E20"))
        }

        holder.tvDescripcion.text = mov.description ?: "Transferencia"

        val fecha = mov.date?.let {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX"))
            sdf.format(Date(it.seconds * 1000))
        } ?: "Sin fecha"
        holder.tvFecha.text = fecha
    }

    fun actualizarLista(nuevaLista: List<TransactionResponse>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}