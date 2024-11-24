package com.application.filmes.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitService {
    const val BASE_URL = "http://10.0.2.2:3000/" // IP Emulador, funciona somente no emulador, caso usar celular físico não funciona
    const val IP = "Inserir o IP do computador AQUI"
    //const val BASE_URL = "http://$IP:3000/" // IP Computador funciona no celular físico
    /* Alterar o IP no arquivo network_security_config.xml */
    /* localhost NÃO FUNCIONA */

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