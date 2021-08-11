package com.example.vacationproj3.Activity.Community

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.databinding.DataBindingUtil
import com.example.vacationproj3.Activity.MainActivity
import com.example.vacationproj3.Function.Firestore
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivityWriteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class WriteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityWriteBinding
    private var currnetImageBitmap : Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write)
        val ctx = this
        binding.btnSubmitWrite.setOnClickListener{
            if(currnetImageBitmap != null && !binding.edWrite.text.equals("")) {
                CoroutineScope(Dispatchers.Main).launch {
                    val result = Firestore.uploadPost(currnetImageBitmap!!, binding.edWrite.text.toString())
                    if(result == true) {
                        val builder = AlertDialog.Builder(ctx)
                        builder.setTitle("업로드 완료")
                        builder.setMessage("포스트 업로드 완료!")
                        builder.setPositiveButton("확인") {_:DialogInterface, _:Int ->
                            startActivity(Intent(baseContext, CommunityActivity::class.java))
                        }
                        builder.show()
                    } else {
                        val builder = AlertDialog.Builder(ctx)
                        builder.setTitle("업로드 실패")
                        builder.setMessage(Firestore.errorMessage)
                        builder.setPositiveButton("확인") {_:DialogInterface, _:Int ->
                            startActivity(Intent(baseContext, MainActivity::class.java))
                        }
                        builder.show()
                    }
                }
            }
        }

        binding.btnPicture.setOnClickListener {
            openGallery()
        }
    }
    fun openGallery() {
        val intent : Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent,1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 1) {
            val currentImageUrl = data?.data
            try {
                currnetImageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageUrl)
                binding.imgPicture.setImageBitmap(currnetImageBitmap)
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }
}