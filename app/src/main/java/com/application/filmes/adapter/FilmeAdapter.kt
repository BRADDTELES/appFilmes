package com.application.filmes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.filmes.api.RetrofitService
import com.application.filmes.databinding.ItemFilmeBinding
import com.application.filmes.model.Filme
import com.squareup.picasso.Picasso

class FilmeAdapter(
    val onClick: (Filme) -> Unit
) : RecyclerView.Adapter<FilmeAdapter.FilmeViewHolder>(){

    private var listaFilmes = mutableListOf<Filme>() // Criando uma lista vazia

    // Método para adicionar a lista de filmes
    fun adicionarLista( lista: List<Filme> ){
        this.listaFilmes.addAll( lista )//20 + 20 + 20 + 20
        notifyDataSetChanged() // Notificar o adapter que a lista foi alterada
    }

    inner class FilmeViewHolder(val binding: ItemFilmeBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(filme: Filme){ // Recebe o filme como parâmetro

            binding.textTitulo.text = filme.titulo // Pegando o dado do filme titulo e exibindo na tela em textTitulo
            binding.clItem.setOnClickListener {
                onClick( filme )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilmeAdapter.FilmeViewHolder {
        // Inflar o layout do item do filme e criar o ViewHolder
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding  = ItemFilmeBinding.inflate(
            layoutInflater,
            parent,
            false
        )
        return FilmeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmeAdapter.FilmeViewHolder, position: Int) {
        val filme = listaFilmes[position] // Obter o filme na posição atual
        holder.bind( filme ) // Vincular os dados do filme com a view
    }

    override fun getItemCount(): Int {
        return listaFilmes.size // Retornar o tamanho da lista
    }

}
