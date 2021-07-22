package com.example.vacationproj3.Firebase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Firebase {
    val auth = Firebase.auth
    val db = Firebase.firestore

}