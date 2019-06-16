package com.freedomhi.praisepolice

import com.google.gson.annotations.SerializedName


data class Police (
        @SerializedName("n") val name: String,
        @SerializedName("p") val position: String,
        @SerializedName("a") val auxiliary: Int,
        @SerializedName("s") val source: String
){
    var id: String = ""
}
