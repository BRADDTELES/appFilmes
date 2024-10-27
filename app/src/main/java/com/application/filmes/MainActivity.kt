package com.application.filmes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.application.filmes.adapter.FilmeAdapter
import com.application.filmes.api.RetrofitService
import com.application.filmes.databinding.ActivityMainBinding
import com.application.filmes.model.Filme
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val filmeAPI by lazy {
        RetrofitService.filmeAPI
    }
    var jobTodosOsFilmes: Job? = null
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

        filmeAdapter = FilmeAdapter { filme ->
            val intent = Intent(this, DetalhesActivity::class.java)
            intent.putExtra("filme", filme)
            startActivity(intent)
        }
        binding.rvPopulares.adapter = filmeAdapter

        gridLayoutManager = GridLayoutManager(
            this,
            2,
            GridLayoutManager.VERTICAL,
            false
        )
        binding.rvPopulares.layoutManager = gridLayoutManager

        // Logica Esconder a seta de adicionar quando o scroll desce e quando sobe aparece a seta (VERTICAL)
        binding.rvPopulares.addOnScrollListener( object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val podeDescerVerticalmente = recyclerView.canScrollVertically(1)// Verificar scroll na vertical e se está descendo
                Log.i("recycler_test", "podeDescerVerticalmente: $podeDescerVerticalmente")
                Log.i("recycler_test", "onScrolled: dx: $dx, dy: $dy")
                if (dy > 0) {//descendo
                    binding.faEditar.hide()
                }else{//dy < 0 está subindo
                    binding.faEditar.show()
                }
            }
        })

    }

    override fun onStart() {
        super.onStart()
        recuperarTodosFilmes()
    }

    private fun recuperarTodosFilmes() {
        jobTodosOsFilmes = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = filmeAPI.recuperarTodosFilmes()
                withContext(Dispatchers.Main) {
                    when {
                        response.isSuccessful -> {
                            response.body()?.let { listaFilmes ->
                                filmeAdapter.adicionarLista(listaFilmes)
                                listaFilmes.forEach { msg ->
                                    Log.i(
                                        "info_filmes",
                                        "ID: ${msg.id} - TÍTULO: ${msg.titulo} - GÊNERO: ${msg.genero} - ANO: ${msg.ano}\n"
                                    )
                                }
                            }
                        }

                        response.code() == 404 -> {
                            exibirMensagem("Filmes não encontrados.")
                        }

                        response.code() == 500 -> {
                            exibirMensagem("Erro interno do servidor. Tente novamente mais tarde.")
                        }

                        response.code() == 400 -> {
                            Log.e("info_filmes", "Erro na requisição. Verifique os dados enviados.")
                        }

                        response.code() == 401 -> {
                            Log.e("info_filmes", "Não autorizado. Verifique suas credenciais.")
                        }

                        response.code() == 405 -> {
                            Log.e(
                                "info_filmes",
                                "Método não permitido. Verifique o tipo de requisição."
                            )
                        }

                        response.code() == 406 -> {
                            Log.e(
                                "info_filmes",
                                "Not Acceptable. Verifique os cabeçalhos da requisição."
                            )
                        }

                        response.code() == 409 -> {
                            Log.e("info_filmes", "Erro de conflito. Verifique os dados enviados.")
                        }

                        else -> {
                            exibirMensagem("Erro ao recuperar filmes.")
                            Log.e(
                                "info_filmes",
                                response.errorBody()?.string() ?: "Erro desconhecido"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    exibirMensagem("Erro inesperado: ${e.message}")
                    Log.e("info_filmes", e.stackTraceToString())
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    exibirMensagem("Erro de conexão. Verifique sua internet.")
                    Log.e("info_filmes", e.stackTraceToString())
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