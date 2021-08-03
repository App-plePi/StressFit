package com.example.vacationproj3.Function

import android.annotation.SuppressLint
import com.example.vacationproj3.Data.MyData
import com.google.common.collect.ArrayListMultimap
import com.google.firebase.firestore.FieldValue
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

    //suspend fun upload
    suspend fun heartButton(uid: String): Boolean? { //하트 추가시 true리턴, 하트 삭제시 false리턴, 실패시 null
        var returnD : Boolean? = null
        var isPressed : Boolean? = null

        return try{
            db.collection("posts").document(uid).get().addOnSuccessListener {
                val heartList = it.data?.get("heart") as ArrayList<*>?
                heartList?.let {
                    isPressed = heartList.contains(MyData.uid)
                }
            }.await()
            lateinit var temp : FieldValue
            if(isPressed == true)  {
                temp = FieldValue.arrayUnion(MyData.uid)
                returnD = false
            }
            else {
                temp = FieldValue.arrayRemove(MyData.uid)
                returnD = true
            }
            db.collection("posts").document(uid).update("heart",temp).addOnSuccessListener {

            }.addOnFailureListener {
                errorMessage = it.message
                returnD = null
            }.await()
            returnD
        } catch(e: Exception) {
            errorMessage= e.message
            null
        }
    }
    suspend fun getPosts(): QuerySnapshot? {
        var data : QuerySnapshot? = null
        try {
            db.collection("posts").get().addOnSuccessListener {
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
    suspend fun delPost(uid: String) : Boolean? { //성공시 true 실패시 false
        if(!uid.equals(MyData.uid)) {
            errorMessage = "No Permission"
            return false
        }
        var returnD = false
        return try {
            db.collection("posts").document(uid).delete().addOnSuccessListener {
                returnD = true
            }.addOnFailureListener {
                errorMessage = it.message
                returnD = false
            }.await()
            returnD
        } catch (e: Exception) {
            errorMessage = e.message
            null
        }
    }
    suspend fun editPost(uid: String, text: String) : Boolean? {
        if(!uid.equals(MyData.uid)) {
            errorMessage = "No Permission"
            return false
        }
        var returnD = false
        return try {
            db.collection("posts").document(uid).update("text", text).addOnSuccessListener {
                returnD = true
            }.addOnFailureListener {
                errorMessage = it.message
                returnD = false
            }.await()
            returnD
        } catch(e: Exception) {
            errorMessage = e.message
            false
        }
    }

}