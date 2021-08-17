package com.example.vacationproj3.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.vacationproj3.activity.community.CommunityActivity
import com.example.vacationproj3.function.Firestore
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivityEditBinding
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
        val context = this;
        binding.edWriteEd.setText(text)
        binding.btnEditEd.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val result =
                    Firestore.editPost(postUid.toString(), binding.edWriteEd.text.toString())
                if (result == true) {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("수정 완료")
                    builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                        startActivity(Intent(context, CommunityActivity::class.java))
                    }
                    builder.show()
                } else {
                    Log.d(">", Firestore.errorMessage.toString());
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("수정 실패")
                    builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                    }
                    builder.show()
                }
            }
        }

    }
}