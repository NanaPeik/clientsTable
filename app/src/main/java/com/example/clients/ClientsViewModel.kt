package com.example.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clients.data.ClientsRepository
import com.example.clients.data.Data
import com.example.clients.data.fullName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientsViewModel @Inject constructor(private val clientsRepository: ClientsRepository) :
    ViewModel() {

    val properties = listOf("Id", "Name", "Company", "Created", "Status")

    private var clientsList: MutableSharedFlow<List<Data>> = MutableStateFlow(listOf())

    var filteredClientList: MutableSharedFlow<List<Data>> = clientsList

    fun getClientsList() {
        viewModelScope.launch {
            clientsList = clientsRepository.fetchDataFromJsonAndStore()
            filteredClientList.emitAll(clientsList)
        }
    }


    fun sortByProperty(propertyType: String) {
        viewModelScope.launch {
            val list: List<Data>?

            when (propertyType) {
                PropertyEnum.ID.value -> {
                    list = clientsList.firstOrNull()?.sortedWith(compareBy { it.id })
                }

                PropertyEnum.FIRST_NAME.value -> {
                    list = clientsList.firstOrNull()?.sortedWith(compareBy { it.firstName })
                }

                PropertyEnum.COMPANY.value -> {
                    list = clientsList.firstOrNull()
                        ?.sortedWith(compareBy { it.clientOrganisation.name })
                }

                PropertyEnum.CREATED_DATA.value -> {
                    list = clientsList.firstOrNull()?.sortedWith(compareBy { it.created })
                }

                PropertyEnum.STATUS.value -> {
                    list = clientsList.firstOrNull()?.sortedWith(compareBy { it.status })
                }

                else -> {
                    list = clientsList.firstOrNull()
                }
            }

            filteredClientList.emit(list ?: listOf())
        }
    }

    fun searchByProperty(searchText: String) {
        viewModelScope.launch {
            val list = mutableListOf<Data>()
            clientsList.firstOrNull()?.forEach { data ->
                if (data.fullName().lowercase().contains(searchText.lowercase())) {
                    list.add(data)
                }
            }
            filteredClientList.emit(list)
        }
    }

}