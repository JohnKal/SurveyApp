package com.example.data.businessmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionDomainModel(
    val id: String,
    val question: String,
    var answer: String = "",
    var isButtonEnabled: Boolean = true
): Parcelable