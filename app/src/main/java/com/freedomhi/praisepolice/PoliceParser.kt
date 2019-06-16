package com.freedomhi.praisepolice

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class PoliceParser : ContextWrapper{
    constructor(base:Context) : super(base)

    var json = JSONObject()
    
    // Init: load data
    fun setup() {
        val inputStream= resources.openRawResource(R.raw.data_compress)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        json = JSONObject(jsonString)
    }

    // Search
    fun search (id: String): ArrayList<Police> {
        val policeJsonArray = json.optString(id) ?: return arrayListOf()
        val policeArray: ArrayList<Police> = Gson().fromJson(policeJsonArray) ?: arrayListOf()
        policeArray.forEach{ it.id = id }
        return policeArray
    }

    // Inline helper function
    inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson<T>(json, object : TypeToken<T>() {}.type)
}
