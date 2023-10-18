package com.example.clients.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Singleton
class ClientsRepository constructor(private val context: Context) {

    private var dataList = MutableStateFlow<List<Data>>(listOf())

    suspend fun fetchDataFromJsonAndStore(): MutableStateFlow<List<Data>> {
        val filename = DATA_FILENAME
        context.assets.open(filename).use { inputStream ->
            JsonReader(inputStream.reader()).use { jsonReader ->
                val dataType = object : TypeToken<ClientsList>() {}.type
                val plantList: ClientsList = Gson().fromJson(jsonReader, dataType)
                dataList.emit(plantList.data)
            }
        }
        return dataList
    }
}
