package com.application.filmes.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitService {
    const val BASE_URL = "http://10.0.2.2:3000/"
    //const val BASE_URL = "http://localhost:3000/" // NÃO FUNCIONA


    private val okhttpClient: OkHttpClient = OkHttpClient.Builder()
        .writeTimeout(10, TimeUnit.SECONDS)// Escrita (salvando na API)
        .readTimeout(20, TimeUnit.SECONDS)// Leitura (recuperando dados da API)
        .connectTimeout(1, TimeUnit.SECONDS)// Tempo máximo de conexão
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl( BASE_URL )
        .client(okhttpClient)
        .addConverterFactory( GsonConverterFactory.create() )
        .build()

    val filmeAPI = retrofit.create( FilmeAPI::class.java )
}