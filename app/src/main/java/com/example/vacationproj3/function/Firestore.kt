package com.example.vacationproj3.function

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import com.example.vacationproj3.data.MyData
import com.example.vacationproj3.data.PostData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.security.DigestException
import java.security.MessageDigest
import java.time.LocalDate
import kotlin.experimental.and

object Firestore {
    @SuppressLint("StaticFieldLeak")
    val db = Firebase.firestore
    var errorMessage: String? = null

    suspend fun getUserStressLevel(uid: String): String? {

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
        } catch (e: Exception) {
            errorMessage = e.message
            return null
        }
    }

    suspend fun getMyStressLevel(): String? {
        try {
            var returnD: String? = null
            db.collection("users").document(MyData.uid).get().addOnSuccessListener {
                returnD = it.data?.get("stressLevel").toString()
                Log.d(">", returnD.toString())
            }.addOnFailureListener {
                errorMessage = it.message
                returnD = null
            }.await()

            return returnD

        } catch (e: Exception) {
            errorMessage = e.message
            return null
        }
    }

    suspend fun updateStressLevel(stressLvl: String): Boolean? {
        var returnD: Boolean? = false
        try {
            MyData.stressLevel = stressLvl
            db.collection("users").document(MyData.uid)
                .set(hashMapOf("stressLevel" to stressLvl), SetOptions.merge())
                .addOnSuccessListener {
                    returnD = true
                }.addOnFailureListener {
                errorMessage = it.message
                returnD = false
            }.await()
        } catch (e: Exception) {
            errorMessage = e.message
            returnD = false
        }
        return returnD
    }

    suspend fun getStressQuestions(): QuerySnapshot? {
        var data: QuerySnapshot? = null
        try {
            db.collection("stress").get().addOnSuccessListener {
                data = it
            }.addOnFailureListener {
                errorMessage = it.message

            }.await()
            return data
        } catch (e: Exception) {
            errorMessage = e.message
            return data
        }
    }

    //suspend fun upload
    suspend fun heartButton(uid: String): Boolean? { //?????? ????????? true??????, ?????? ????????? false??????, ????????? null
        var returnD: Boolean? = null
        var isPressed: Boolean? = null
        try {
            db.collection("posts").document(uid).get().addOnSuccessListener {
                //val heartList = it.data?.get("heart") as ArrayList<String>?
                val heartList = it.get("heart") as ArrayList<String>?
                isPressed = heartList?.contains(MyData.uid)
                Log.d(">1", isPressed.toString())

            }.await()
            if (isPressed == true) {
                db.collection("posts").document(uid)
                    .update("heart", FieldValue.arrayRemove(MyData.uid)).addOnSuccessListener {
                    returnD = false
                }.addOnFailureListener {
                    errorMessage = it.message
                    returnD = null
                }.await()
                Log.d(">2", isPressed.toString())
            } else {
                db.collection("posts").document(uid)
                    .update("heart", FieldValue.arrayUnion(MyData.uid)).addOnSuccessListener {
                    returnD = true
                }.addOnFailureListener {
                    errorMessage = it.message
                    returnD = null
                }.await()
                Log.d(">3", isPressed.toString())
            }
            Log.d(">4", returnD.toString())
            return returnD
        } catch (e: Exception) {
            errorMessage = e.message
            Log.d(">>>", errorMessage.toString())
            return null
        }
    }

    suspend fun getPosts(): ArrayList<PostData> {
        var newData: ArrayList<PostData> = ArrayList()
        try {
            db.collection("posts").get().addOnSuccessListener {
                for (t in it) {
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
        } catch (e: Exception) {
            errorMessage = e.message
            return newData
        }
    }

    suspend fun delPost(uid: String): Boolean? { //????????? true ????????? false
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

    suspend fun editPost(uid: String, text: String): Boolean? {
        var returnD = false
        return try {
            db.collection("posts").document(uid).update(mapOf("text" to text))
                .addOnSuccessListener {
                    returnD = true
                }.addOnFailureListener {
                errorMessage = it.message
                returnD = false
            }.await()
            returnD
        } catch (e: Exception) {
            errorMessage = e.message
            false
        }
    }

    suspend fun uploadPost(bitmap: Bitmap, text: String): Boolean? {
        var returnD: Boolean? = null
        try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val data = baos.toByteArray()
            val storageRef = Firebase.storage.reference
            val PHOTOUID = createPhotoUid()
            val photoRef = storageRef.child("postImages/$PHOTOUID")
            val uploadTask = photoRef.putBytes(data)
            uploadTask.addOnFailureListener {
                throw Exception(it.message)
            }.await()
            var t = arrayListOf<String>()
            val uploadData = hashMapOf(
                "username" to MyData.displayName,
                "uid" to MyData.uid,
                "photoUrl" to MyData.photoUrl,
                "stressLevel" to MyData.stressLevel,
                "time" to LocalDate.now().toString(),
                "heart" to t,
                "photoUid" to PHOTOUID,
                "text" to text
            )
            db.collection("posts").document().set(uploadData).addOnFailureListener {
                throw Exception(it.message)
            }.addOnSuccessListener {
                returnD = true
            }.await()
        } catch (e: Exception) {
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
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(msg.toByteArray())
            hash = md.digest()
        } catch (e: CloneNotSupportedException) {
            throw DigestException("couldn't make digest of patial content")
        }
        //return Base64Utils.encode(hash)
        return bToHexStr(hash)
    }

    fun bToHexStr(data: ByteArray): String {
        val sb = StringBuilder()
        for (b in data) {
            sb.append(Integer.toString((b.and(0xff.toByte())) + 0x100, 16).substring(1));
        }
        return sb.toString()
    }

}
