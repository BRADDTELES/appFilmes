package com.application.filmes

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.application.filmes.api.RetrofitService
import com.application.filmes.databinding.ActivityCadastroBinding
import com.application.filmes.model.Filme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CadastroActivity : AppCompatActivity() {

    private  val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }
    private val filmeAPI by lazy {
        RetrofitService.filmeAPI
    }
    private var jobSalvarFilme: Job? = null
    private var jobAtualizarFilme: Job? = null
    private var filme: Filme? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        filme = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("filme", Filme::class.java)
        } else {
            intent.getParcelableExtra("filme")
        }

        filme?.let { filme ->
            with(binding) {
                editTextTitulo.setText(filme.titulo)
                editTextGenero.setText(filme.genero)
                editTextAno.setText(filme.ano.toString())
                textViewResultado.text = "Editar"
            }
        }
        with(binding){
            fabSalvar.setOnClickListener{
                // Salvar filme
                val titulo = editTextTitulo.text.toString()
                val genero = editTextGenero.text.toString()
                val ano = editTextAno.text.toString().toIntOrNull() ?: 0
                if (titulo.isNullOrEmpty() || genero.isNullOrEmpty() || ano <= 1900) {
                    exibirMensagem("Preencha todos os campos")
                    return@setOnClickListener
                }
                if (filme == null) {
                    salvarFilme(Filme(0, titulo, genero, ano))
                } else {
                    filme?.let { filmeNaoNulo ->
                        atualizarFilme(
                            Filme(
                                filmeNaoNulo.id,
                                titulo,
                                genero,
                                ano
                            )
                        )
                    }
                }
            }
        }
    }

    private fun salvarFilme(filme: Filme) {
        jobSalvarFilme = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = filmeAPI.salvarFilme(filme)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        exibirMensagem("Filme salvo com sucesso!")
                        val intent = Intent(this@CadastroActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        exibirMensagem("Erro ao salvar filme.")
                        Log.e("info_filmes", response.errorBody()?.string() ?: "Erro desconhecido")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    exibirMensagem("Erro ao salvar filme: ${e.message}")
                    Log.e("info_filmes", e.stackTraceToString())
                }
            }
        }
    }
    private fun atualizarFilme(filme: Filme) {
        jobAtualizarFilme = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = filmeAPI.atualizarFilme(filme.id, filme)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        exibirMensagem("Filme atualizado com sucesso!")
                        val intent = Intent(this@CadastroActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        exibirMensagem("Erro ao atualizar filme.")
                        Log.e("info_filmes", response.errorBody()?.string() ?: "Erro desconhecido")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    exibirMensagem("Erro ao atualizar filme: ${e.message}")
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
        jobSalvarFilme?.cancel()
        jobAtualizarFilme?.cancel()
    }
}