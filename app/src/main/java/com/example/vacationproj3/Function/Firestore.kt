package com.example.vacationproj3.Function

import android.annotation.SuppressLint
import com.example.vacationproj3.Data.MyData
import com.google.common.collect.ArrayListMultimap
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.io.Serializable

object Firestore{
    @SuppressLint("StaticFieldLeak")
    val db = Firebase.firestore
    var errorMessage: String? = null

    suspend fun getUserStressLevel(uid: String) : String? {

        try {
            var returnD: String? = null
            db.collection("users").document(uid).get().addOnSuccessListener {
                if (it != null) {
                    returnD = it.data?.get("stressLevel")?.toString()
                } else {
                    errorMessage = "Document not Found"
                    returnD = null
                }
            }.addOnFailureListener {
                errorMessage = it.message
                returnD = null
            }.await()

            return returnD
        } catch(e: Exception) {
            errorMessage = e.message
            return null
        }
    }

    suspend fun getMyStressLevel() : String? {
        try {
            var returnD : String? = null
            db.collection("users").document(MyData.uid).get().addOnSuccessListener {
                if (it != null) {
                    returnD = it.data?.get("stressLevel")?.toString()
                } else {
                    errorMessage = "Document not Found"
                    returnD = null
                }
            }.addOnFailureListener {
                errorMessage = it.message
                returnD = null
            }.await()

            return returnD

        }catch(e: Exception) {
            errorMessage = e.message
            return null
        }
    }

    suspend fun updateStressLevel(stressLvl: String) {
        var returnD : Boolean = false
        try {
            MyData.stressLevel = stressLvl
            db.collection("users").document(MyData.uid).set(hashMapOf("stressLevel" to stressLvl), SetOptions.merge()).addOnSuccessListener {
                returnD = true
            }.addOnFailureListener {
                errorMessage = it.message
                returnD = false
            }.await()
        } catch(e: Exception) {
            errorMessage = e.message
            returnD = false
        }
    }

    //suspend fun uploadPost()
    suspend fun getStressQuestions(): QuerySnapshot? {
        var data : QuerySnapshot? = null
        try {
            db.collection("stress").get().addOnSuccessListener {
                data = it
            }.addOnFailureListener {
                errorMessage = it.message

            }.await()
            return data
        } catch(e: Exception) {
            errorMessage= e.message
            return data
        }
    }


}