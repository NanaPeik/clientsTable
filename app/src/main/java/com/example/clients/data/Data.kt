package com.example.clients.data

import java.util.Date

data class Data(
    val created: Date? = null,
    val email: String? = "",
    val firstName: String = "",
    val fixedLinePhone: String = "",
    val id: String = "",
    val lastName: String = "",
    val mobilePhone: String? = "",
    val status: String = "",
    val clientOrganisation: ClientOrganisation = ClientOrganisation("", "")
)

fun Data.fullName() = "$firstName $lastName"
fun Data.contains(searchText: String) =
    id.contains(searchText) || firstName.lowercase().contains(searchText)
            || lastName.lowercase().contains(searchText) || fixedLinePhone.lowercase()
        .contains(searchText)
            || mobilePhone?.lowercase()
        ?.contains(searchText) == true || clientOrganisation.name.lowercase().contains(searchText)
            || email?.lowercase()?.contains(searchText) == true || status.lowercase()
        .contains(searchText)