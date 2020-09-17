package com.ericg.usccrecord.data

import com.google.firebase.firestore.FieldValue

/**
 * @author eric
 * @date 9/15/20
 */

data class PersonData(
    val name: String,
    val gender: String,
    val age: Int,
    val temp: Int,
    val phone: String,
    var date: Any?,
    var locationName: String?,
    var locationCode: String?
) {
    init {
        date = FieldValue.serverTimestamp()
        locationCode = when (locationName) {
            // todo pass the villages into a spinner fill the appropriate location codes
            "githure" -> "map code"
            "kiamugumo" -> ""
            "gituba" -> ""
            "ngariama" -> ""
            "karinga" -> ""
            "kiriko" -> ""
            "gaciongo" -> ""
            "gitemani" -> ""
            "kiamutugu" -> ""
            "other" -> ""
            else -> "set githure code as default"
        }
    }

    /* for fireStore data objects */
    constructor() : this("", "", 0, 0, "", null, null, null)
}