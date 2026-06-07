package com.curso.banca.home.beneficiarios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.curso.banca.databinding.ItemBeneficiarioBinding

enum class AdapterMode { NORMAL, SELECT }


class BeneficiarioAdapter(
    private var lista: MutableList<Beneficiario>,
    private val mode: AdapterMode = AdapterMode.NORMAL,
    private val onSelect: ((Beneficiario) -> Unit)? = null,
    private val onDelete: ((Beneficiario) -> Unit)? = null
) : RecyclerView.Adapter<BeneficiarioAdapter.VH>() {

    inner class VH(val b: ItemBeneficiarioBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemBeneficiarioBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = lista[position]
        holder.b.tvNombre.text = item.nombre
        holder.b.tvBanco.text  = "${item.banco} · ${item.cuentaOculta}"
        // Iniciales del nombre
        val iniciales = item.nombre.split(" ").take(2).map { it.first() }.joinToString("")
        holder.b.tvIniciales.text = iniciales

        if (mode == AdapterMode.SELECT) {
            holder.b.btnMore.visibility = View.GONE
            holder.itemView.setOnClickListener { onSelect?.invoke(item) }
        } else {
            holder.b.btnMore.visibility = View.VISIBLE
            holder.b.btnMore.setOnClickListener { v ->
                val popup = PopupMenu(v.context, v)
                popup.menu.add("Eliminar")
                popup.setOnMenuItemClickListener {
                    if (it.title == "Eliminar") onDelete?.invoke(item)
                    true
                }
                popup.show()
            }
        }
    }

    fun actualizar(nuevaLista: List<Beneficiario>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}