package com.application.filmes.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Filme(
    val id: Int,
    val titulo: String,
    val genero: String,
    val ano: Int
) : Parcelable
