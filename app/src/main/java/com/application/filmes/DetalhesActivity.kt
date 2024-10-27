package com.application.filmes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.application.filmes.api.RetrofitService
import com.application.filmes.databinding.ActivityDetalhesBinding
import com.application.filmes.model.Filme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalhesActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityDetalhesBinding.inflate(layoutInflater)
    }
    private val filmeAPI by lazy {
        RetrofitService.filmeAPI
    }
    private var jobDeletarFilme: Job? = null
    private var filme: Filme? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        filme = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("filme", Filme::class.java)
        } else {
            intent.getParcelableExtra("filme")
        }

        if (filme!= null){
            binding.textViewTitulo.text = filme?.titulo
            binding.textViewGenero.text = filme?.genero
            binding.textViewAno.text = filme?.ano.toString()
        }

        /*filme?.let { filme ->
            with(binding) {
                textViewTitulo.text = filme.titulo
                textViewGenero.text = filme.genero
                textViewAno.text = filme.ano.toString()
            }
        }*/

        with(binding) {
            fabBotao.setOnClickListener {
                if (groupMenu.isVisible) {
                    groupMenu.visibility = View.INVISIBLE
                } else {
                    groupMenu.visibility = View.VISIBLE
                    fabEditar.setOnClickListener {
                        val intent = Intent(this@DetalhesActivity, CadastroActivity::class.java)
                        intent.putExtra("filme", filme)
                        startActivity(intent)
                    }
                    fabDeletar.setOnClickListener {
                        jobDeletarFilme = CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = filme?.id?.let { id ->
                                    filmeAPI.deletarFilme(id)
                                }
                                withContext(Dispatchers.Main) {
                                    if (response != null) {
                                        if (response.isSuccessful) {
                                            exibirMensagem("Filme deletado com sucesso!")
                                            val intent =
                                                Intent(
                                                    this@DetalhesActivity,
                                                    MainActivity::class.java
                                                )
                                            startActivity(intent)
                                        } else {
                                            exibirMensagem("Erro ao deletar filme")
                                            Log.i(
                                                "info_filmes",
                                                response.errorBody()?.string()
                                                    ?: "Erro desconhecido"
                                            )
                                            Log.i("info_filmes", "ERRO CODE: ${response.code()}")
                                        }
                                    }
                                }

                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    exibirMensagem("Erro ao deletar filme: ${e.message}")
                                    Log.e("info_filmes", e.stackTraceToString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(
            this@DetalhesActivity,
            mensagem,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onStop() {
        super.onStop()
        jobDeletarFilme?.cancel()
    }
}