package com.ericg.usccrecord.firebase

import com.ericg.usccrecord.firebase.FirebaseUtils.mUser
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CoroutineScope

/**
 * @author eric
 * @date 9/16/20
 */
class GetData() {
    val userUID = mUser?.uid
    fun get(type: String): Task<QuerySnapshot>? {

        return if (type == "personData"){
            FirebaseUtils.userDatabase?.collection("USCCUsers")
                ?.document(userUID!!)
                ?.collection("personData")
                ?.get()
        } else {
            FirebaseUtils.userDatabase?.collection("USCCUsers")
                ?.document(userUID!!)
                ?.collection("personData")
                ?.get()
        }
    }
}