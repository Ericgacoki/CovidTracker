package com.ericg.usccrecord.firebase

import com.ericg.usccrecord.data.CumulativeData
import com.ericg.usccrecord.data.PersonData
import com.ericg.usccrecord.firebase.FirebaseUtils.mAuth
import com.ericg.usccrecord.firebase.FirebaseUtils.userDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author eric
 * @date 9/15/20
 */
class Save{
    fun newEntry(type: String, personData: PersonData?, cumulativeData: CumulativeData?) {
        val userUID = mAuth?.currentUser?.uid
        GlobalScope.launch(Dispatchers.IO) {

            if (type == "personData") {
                if (personData != null) {
                    userDatabase?.collection("USCCData")
                        ?.document(userUID!!)
                        ?.collection("personData")
                        ?.add(personData)
                }
            } else {
                if (cumulativeData != null) {
                    userDatabase?.collection("USCCData")
                        ?.document(userUID!!)
                        ?.collection("cumulativeData")
                        ?.add(cumulativeData)
                }
            }
        }
    }
}