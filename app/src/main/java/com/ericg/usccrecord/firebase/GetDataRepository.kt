/*
 * Copyright (c) 2020. This software is owned by @Eric_gacoki
 */

package com.ericg.usccrecord.firebase

import com.ericg.usccrecord.firebase.FirebaseUtils.mUser
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class GetDataRepository {
    fun get(): Task<QuerySnapshot>? {
        val userUID = mUser?.uid as String
        return FirebaseUtils.userDatabase?.collection("USCCRecord/${userUID}/personData")
            ?.orderBy("timeStamp", Query.Direction.DESCENDING)
            ?.get()
    }
}