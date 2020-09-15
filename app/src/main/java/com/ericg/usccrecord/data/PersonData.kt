package com.ericg.usccrecord.data

import com.google.firebase.firestore.FieldValue

/**
 * @author eric
 * @date 9/15/20
 */

data class PersonData(
    val name: String,
    val gender: String,
    val age: String,
    val temp: Int,
    val phone: String,
    var date: Any?

) {
    init {
        date = FieldValue.serverTimestamp()
    }
}