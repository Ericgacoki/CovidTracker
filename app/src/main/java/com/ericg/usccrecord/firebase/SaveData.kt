/*
 * Copyright (c) 2020. This software is owned by @Eric_gacoki
 */

package com.ericg.usccrecord.firebase

import androidx.lifecycle.MutableLiveData
import com.ericg.usccrecord.firebase.FirebaseUtils.mUser
import com.ericg.usccrecord.firebase.FirebaseUtils.userDatabase
import com.ericg.usccrecord.model.CumulativeData
import com.ericg.usccrecord.model.PersonData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author eric
 * @date 9/15/20
 */

class SaveData {
    fun newEntry(
        type: String, personData: PersonData?, cumulativeData: CumulativeData?
    ): MutableLiveData<Boolean> {
        val success = MutableLiveData<Boolean>()
        val userUID = mUser?.uid as String
        GlobalScope.launch(Dispatchers.IO) {

            /* first create a real document (by adding a dummy field)*/

            val docUserUID = userDatabase?.document("USCCMember1/${userUID}")
            docUserUID?.set(hashMapOf("this doc" to "is real"))

            if (type == "personData") {
                if (personData != null) {
                    docUserUID
                        ?.collection("personData")
                        ?.add(personData)?.addOnCompleteListener { save ->
                            success.value = save.isSuccessful
                        }
                }
            } else {
                if (cumulativeData != null) {
                    docUserUID
                        ?.collection("cumulativeData")
                        ?.add(cumulativeData)?.addOnCompleteListener { save ->
                            success.value = save.isSuccessful
                        }
                }
            }
        }
        return success
    }
}