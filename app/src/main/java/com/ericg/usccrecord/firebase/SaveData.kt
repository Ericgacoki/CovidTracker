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

            if (type == "personData") {
                if (personData != null) {
                    userDatabase?.collection("USCCUsers")
                        ?.document(userUID!!)
                        ?.collection("personData")
                        ?.add(personData)
                }
            } else {
                if (cumulativeData != null) {
                    userDatabase?.collection("USCCUsers")
                        ?.document(userUID!!)
                        ?.collection("cumulativeData")
                        ?.add(cumulativeData)
                }
            }
        }
    }
}