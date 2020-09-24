/*
 * Copyright (c) 2020. This software is owned by @Eric_gacoki
 */

package com.ericg.usccrecord.firebase

import com.ericg.usccrecord.firebase.FirebaseUtils.mUser
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

/**
 * @author eric
 * @date 9/16/20
 */
class GetData {
    fun get(type: String): Task<QuerySnapshot>? {
        val userUID = mUser?.uid as String
        return if (type == "personData") {
            // todo set root collection to USCCMembers
            FirebaseUtils.userDatabase?.collection("USCCMember1/${userUID}/personData")
                ?.orderBy("timeStamp", Query.Direction.DESCENDING)
                ?.get()
        } else {
            FirebaseUtils.userDatabase?.collection("USCCMember1/${userUID}/cumulativeData")
                ?.orderBy("timeStamp", Query.Direction.DESCENDING)
                ?.get()
        }
    }
}