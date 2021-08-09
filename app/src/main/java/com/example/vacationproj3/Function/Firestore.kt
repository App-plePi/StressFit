package com.example.vacationproj3.Function

import android.annotation.SuppressLint
import android.graphics.Bitmap
import com.example.vacationproj3.Data.MyData
import com.example.vacationproj3.Data.PostData
import com.google.android.gms.common.util.Base64Utils
import com.google.common.collect.ArrayListMultimap
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.security.DigestException
import java.security.MessageDigest
import java.time.LocalDate

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

    suspend fun updateStressLevel(stressLvl: String): Boolean? {
        var returnD : Boolean? = false
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
        return returnD
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
    suspend fun getPosts(): ArrayList<PostData> {
        var newData : ArrayList<PostData> = ArrayList()
        try {
            db.collection("posts").get().addOnSuccessListener {
                for(t in it) {
                    var tmp = PostData(
                        t.data["username"].toString(),
                        t.data["uid"].toString(),
                        t.data["photoUrl"].toString(),
                        t.data["stressLevel"].toString(),
                        t.data["heart"] as ArrayList<String>,
                        t.data["photoUid"].toString(),
                        t.data["text"].toString(),
                        t.id
                    )
                    newData.add(tmp)
                }
            }.addOnFailureListener {
                errorMessage = it.message

            }.await()
            return newData
        } catch(e: Exception) {
            errorMessage= e.message
            return newData
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

    suspend fun uploadPost(bitmap: Bitmap, text: String): Boolean? {
        var returnD : Boolean? = null
        try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,baos)
            val data = baos.toByteArray()
            val storageRef = Firebase.storage.reference
            val PHOTOUID = createPhotoUid()
            val photoRef = storageRef.child("postImages/$PHOTOUID")
            val uploadTask = photoRef.putBytes(data)
            uploadTask.addOnFailureListener{
                throw Exception(it.message)
            }.await()
            var t = arrayListOf<String>()
            val uploadData = hashMapOf(
                "username" to MyData.displayName,
                "uid" to MyData.uid,
                "photoUrl" to MyData.photoUrl,
                "stressLevel" to MyData.stressLevel,
                "time" to LocalDate.now(),
                "heart" to t,
                "photoUid" to PHOTOUID,
                "text" to text
            )
            db.collection("posts").document().set(uploadData).addOnFailureListener {
                throw Exception(it.message)
            }.addOnSuccessListener {
                returnD = true
            }.await()
        } catch(e: Exception) {
            errorMessage = e.message
            returnD = false
        }
        return returnD

    }




    fun createPhotoUid(): String {
        return hashSHA256(getTimestamp() + MyData.uid)
    }

    fun getTimestamp(): String {
        return System.currentTimeMillis().toString()
    }

    fun hashSHA256(msg: String): String {
        val hash: ByteArray
        try{
            val md = MessageDigest.getInstance("SHA-256")
            md.update(msg.toByteArray())
            hash = md.digest()
        }catch (e: CloneNotSupportedException){
            throw DigestException("couldn't make digest of patial content")
        }
        return Base64Utils.encode(hash)
    }

}
