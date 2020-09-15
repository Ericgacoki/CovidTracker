package com.ericg.usccrecord.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

/**
 * @author eric
 * @date 9/15/20
 */
object FirebaseUtils {
    var userDatabase: FirebaseFirestore? = FirebaseFirestore.getInstance()
    val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    val mUser: FirebaseUser? by lazy { mAuth?.currentUser }

    init {
        userDatabase?.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }
}