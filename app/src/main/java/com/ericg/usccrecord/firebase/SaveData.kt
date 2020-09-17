package com.ericg.usccrecord.firebase

import com.ericg.usccrecord.data.CumulativeData
import com.ericg.usccrecord.data.PersonData
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
        val userUID = mUser?.uid
        GlobalScope.launch(Dispatchers.IO) {
// todo set root collection to USCCMembers
            if (type == "personData") {
                if (personData != null) {
                    userDatabase?.collection("USCCMember1")
                        ?.document(userUID!!)
                        ?.collection("personData")
                        ?.add(personData)
                }
            } else {
                if (cumulativeData != null) {
                    userDatabase?.collection("USCCMembers")
                        ?.document(userUID!!)
                        ?.collection("cumulativeData")
                        ?.add(cumulativeData)
                }
            }
        }
    }
}