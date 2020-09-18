package com.ericg.usccrecord.firebase

import com.ericg.usccrecord.model.CumulativeData
import com.ericg.usccrecord.model.PersonData
import com.ericg.usccrecord.firebase.FirebaseUtils.mUser
import com.ericg.usccrecord.firebase.FirebaseUtils.userDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author eric
 * @date 9/15/20
 */

class SaveData {
    fun newEntry(type: String, personData: PersonData?, cumulativeData: CumulativeData?) {
        val userUID = mUser?.uid as String
        GlobalScope.launch(Dispatchers.IO) {
            /* first create a real document (by adding a dummy field)*/

            val docUserUID = userDatabase?.document("USCCMember1/${userUID}")
            docUserUID?.set(hashMapOf("this doc" to "is real"))

            if (type == "personData") {
                if (personData != null) {
                    docUserUID
                        ?.collection("personData")
                        ?.add(personData)
                }
            } else {
                if (cumulativeData != null) {
                    docUserUID
                        ?.collection("cumulativeData")
                        ?.add(cumulativeData)
                }
            }
        }
    }
}