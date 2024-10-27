package com.application.filmes.api

import com.application.filmes.model.Filme
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FilmeAPI {

    @GET("api.filme/filme-dao/getAll")
    suspend fun recuperarTodosFilmes() : Response<List<Filme>>

    @POST("api.filme/filme-dao/insert")
    suspend fun salvarFilme(
        @Body filme: Filme
    ): Response<Filme>

    @DELETE("api.filme/filme-dao/delete/{id}")
    suspend fun deletarFilme(
        @Path("id") id: Int
    ): Response<Unit>

    @PUT("api.filme/filme-dao/update/{id}")
    suspend fun atualizarFilme(
        @Path("id") id: Int,
        @Body filme: Filme
    ): Response<Filme>

}