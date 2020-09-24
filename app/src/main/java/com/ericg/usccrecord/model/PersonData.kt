/*
 * Copyright (c) 2020. This software is owned by @Eric_gacoki
 */

package com.ericg.usccrecord.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FieldValue
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @author eric
 * @date 9/15/20
 */

@RequiresApi(Build.VERSION_CODES.O)
data class PersonData(
    val name: String,
    val gender: String,
    val age: Int,
    val temp: Float,
    val phone: String,
    var locationName: String?,
    var date: String?,
    var timeStamp: FieldValue?,
    var locationCode: String?
) {
    init {
        date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        //format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
        timeStamp = FieldValue.serverTimestamp()
        locationCode = when (locationName.toString().toLowerCase(Locale.ROOT)) {
            // todo pass the villages into a spinner fill the appropriate location codes
            /* longitude and latitude codes */

            "githure" -> "-0.444112,37.411218"
            "gituba" -> "0,0"
            "kiamutugu" -> "-0.467202,37.389121"
            "ngerwe" -> "-0.467339,37.411982"
            "ngariama" -> "-0.445471,37.388800"
            "nyangeni" -> "-0.431525,37.401651"
            "gitemani" -> "0,0"
            "gaciongo" -> "0,0"
            "kiriko" -> "0,0"
            "karinga" -> "0,0"
            "kiamugumo" -> "-0.430787,37.411841"

            "other" -> "0,0"
            /*set githure code as default*/
            else -> "-0.444112,37.411218"
        }
    }

    /* for fireStore data objects */
    constructor() : this("", "", 0, 0F, "", null, null, null, null)
}