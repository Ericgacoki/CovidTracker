package com.ericg.usccrecord.firebase

import com.ericg.usccrecord.firebase.FirebaseUtils.mUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author eric
 * @date 9/16/20
 */
class GetData(val scope: CoroutineScope) {
    val userUID = mUser?.uid
    suspend fun savedEntry(type: String) {
        scope.launch {

        }
    }
}