package com.example.vacationproj3.Activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.vacationproj3.Activity.Community.CommunityActivity
import com.example.vacationproj3.Function.Firestore
import com.example.vacationproj3.R
import com.example.vacationproj3.data.MyData
import com.example.vacationproj3.data.PostData
import com.example.vacationproj3.databinding.ActivityEditBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit)
        val text = intent.getStringExtra("text")
        val postUid = intent.getStringExtra("postUid")

        binding.edWriteEd.setText(text)
        binding.btnEditEd.setOnClickListener{
            CoroutineScope(Dispatchers.Main).launch {
                val result = Firestore.editPost(postUid.toString(), binding.edWriteEd.toString())
                if(result==true){
                    val builder = AlertDialog.Builder(baseContext)
                    builder.setTitle("수정 완료")
                    builder.setPositiveButton("확인") { _: DialogInterface, _:Int ->
                        startActivity(Intent(baseContext, CommunityActivity::class.java))
                    }
                    builder.show()
                }
                else{
                    val builder = AlertDialog.Builder(baseContext)
                    builder.setTitle("수정 실패")
                    builder.setPositiveButton("확인") { _: DialogInterface, _:Int ->
                    }
                    builder.show()
                }
            }
        }

    }
}