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
    private val userUID = mUser?.uid
    fun get(type: String): Task<QuerySnapshot>? {
        return if (type == "personData") {
            // todo set root collection to USCCMembers
            FirebaseUtils.userDatabase?.collection("USCCMember1")
                ?.document(userUID!!)
                ?.collection("personData")
                ?.orderBy("date", Query.Direction.DESCENDING)
                ?.get()
        } else {
            FirebaseUtils.userDatabase?.collection("USCCMembers")
                ?.document(userUID!!)
                ?.collection("cumulativeData")
                ?.orderBy("date", Query.Direction.DESCENDING)
                ?.get()
        }
    }
}