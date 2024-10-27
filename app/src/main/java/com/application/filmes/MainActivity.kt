package com.application.filmes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.application.filmes.adapter.FilmeAdapter
import com.application.filmes.api.RetrofitService
import com.application.filmes.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val filmeAPI by lazy {
        RetrofitService.filmeAPI
    }
    var jobTodosOsFilmes: Job? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var gridLayoutManager: GridLayoutManager? = null
    private lateinit var filmeAdapter: FilmeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        inicializarViews()

        binding.faEditar.setOnClickListener {
            val intent = Intent(this, DetalhesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun inicializarViews() {

        filmeAdapter = FilmeAdapter{ filme ->
            val intent = Intent(this, DetalhesActivity::class.java)
            intent.putExtra("filme", filme)
            startActivity(intent)
        }
        binding.rvPopulares.adapter = filmeAdapter

        /*linearLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvPopulares.layoutManager = linearLayoutManager*/

        gridLayoutManager = GridLayoutManager(
            this,
            2,
            GridLayoutManager.VERTICAL,
            false
        )
        binding.rvPopulares.layoutManager = gridLayoutManager
        
    }

    override fun onStart() {
        super.onStart()
        recuperarTodosFilmes()
    }

    private fun recuperarTodosFilmes() {
        jobTodosOsFilmes = CoroutineScope(Dispatchers.IO).launch {
            val response = filmeAPI.recuperarTodosFilmes()
            withContext(Dispatchers.Main){
                if (response.isSuccessful){
                    response.body()?.let { listaFilmes ->
                        filmeAdapter.adicionarLista(listaFilmes)
                    }
                } else {
                    exibirMensagem("Erro ao recuperar filmes")
                    Log.e("info_filmes", response.errorBody().toString())
                    Log.i("info_filmes", "ERRO CODE: ${response.code()}")
                }
            }
        }
    }

    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(
            applicationContext,
            mensagem,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onStop() {
        super.onStop()
        jobTodosOsFilmes?.cancel()
    }
}