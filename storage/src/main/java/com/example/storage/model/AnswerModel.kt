package com.example.storage.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnswerModel(
    val id: String,
    var answer: String): Parcelable