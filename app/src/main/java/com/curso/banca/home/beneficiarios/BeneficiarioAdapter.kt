package com.curso.banca.home.beneficiarios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.curso.banca.databinding.ItemBeneficiarioBinding

enum class AdapterMode { NORMAL, SELECT }

class BeneficiarioAdapter(
    lista: MutableList<Beneficiario>,
    private val mode: AdapterMode = AdapterMode.NORMAL,
    private val onSelect: ((Beneficiario) -> Unit)? = null,
    private val onDelete: ((Beneficiario) -> Unit)? = null
) : RecyclerView.Adapter<BeneficiarioAdapter.VH>() {

    // Lista completa (fuente de verdad) y lista visible (filtrada)
    private var listaCompleta: MutableList<Beneficiario> = lista.toMutableList()
    private var listaVisible: MutableList<Beneficiario> = lista.toMutableList()
    private var filtroActual = ""

    inner class VH(val b: ItemBeneficiarioBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemBeneficiarioBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = listaVisible.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = listaVisible[position]

        holder.b.tvNombre.text = item.nombreCompleto
        holder.b.tvBanco.text  = "${item.banco} · ${item.cuentaOculta}"

        // Iniciales del nombre (ignorando espacios dobles)
        val iniciales = item.nombreCompleto.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
        holder.b.tvIniciales.text = iniciales

        if (mode == AdapterMode.SELECT) {
            holder.b.btnMore.visibility = View.GONE
            holder.itemView.setOnClickListener { onSelect?.invoke(item) }
        } else {
            holder.itemView.setOnClickListener { onSelect?.invoke(item) }
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

    /** Reemplaza la lista completa y vuelve a aplicar el filtro vigente. */
    fun actualizar(nuevaLista: List<Beneficiario>) {
        listaCompleta = nuevaLista.toMutableList()
        aplicarFiltro()
    }

    /** Filtra por nombre, alias o número de cuenta. */
    fun filtrar(texto: String) {
        filtroActual = texto.trim().lowercase()
        aplicarFiltro()
    }

    private fun aplicarFiltro() {
        listaVisible = if (filtroActual.isEmpty()) {
            listaCompleta.toMutableList()
        } else {
            listaCompleta.filter {
                it.nombreCompleto.lowercase().contains(filtroActual) ||
                        it.banco.lowercase().contains(filtroActual) ||
                        it.cuenta.contains(filtroActual)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}