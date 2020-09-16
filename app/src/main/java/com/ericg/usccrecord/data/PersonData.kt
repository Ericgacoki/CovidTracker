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
    var date: Any?

) {
    init {
        date = FieldValue.serverTimestamp()
    }

    /* for fireStore data objects */
    constructor() : this("", "", 0, 0, "", null)
}