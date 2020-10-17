/*
 * Copyright (c) 2020. This software is owned by @Eric_gacoki
 */

package com.ericg.usccrecord.firebase

import androidx.lifecycle.MutableLiveData
import com.ericg.usccrecord.firebase.FirebaseUtils.mUser
import com.ericg.usccrecord.firebase.FirebaseUtils.userDatabase
import com.ericg.usccrecord.model.PersonData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SaveData {
    fun newEntry(
        docId: String, personData: PersonData
    ): MutableLiveData<Boolean> {
        val success = MutableLiveData(false)
        val userUID = mUser?.uid as String
        GlobalScope.launch(Dispatchers.IO) {
            /* first create a real document (by adding a dummy field)*/

            userDatabase?.document("USCCRecord/${userUID}")?.apply {
                set(hashMapOf("this doc" to "is real"))

                collection("personData").document(docId)
                    .set(personData).addOnCompleteListener { save ->
                        success.value = save.isSuccessful
                    }
            }
        }
        return success
    }
}