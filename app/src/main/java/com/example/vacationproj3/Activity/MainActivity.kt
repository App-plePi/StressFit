package com.example.vacationproj3.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.vacationproj3.Activity.Community.CommunityActivity
import com.example.vacationproj3.data.MyData
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var backKeyPressedTime : Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.stressLevel.text = MyData.stressLevel


        binding.goTest.setOnClickListener{
            Log.d(">>>","asfasdf")
            startActivity(Intent(this,SurveyActivity::class.java))
        }

        binding.goSns.setOnClickListener{
            startActivity(Intent(this, CommunityActivity::class.java)) // 엑티비티 이름 작성
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onStart() {
        super.onStart()
        if(MyData.uid == "") startActivity(Intent(this,LoginActivity::class.java))



    }

    override fun onBackPressed() {
        lateinit var toast: Toast
        if(System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis()
            toast = Toast.makeText(this,"뒤로가기를 한번 더 누르면 종료됩니다.", Toast.LENGTH_LONG)
            toast.show()
            return;
        }
        if(System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finishAffinity()
            finish()
            System.runFinalization()
            moveTaskToBack(true)
            finishAndRemoveTask()
            exitProcess(0)
        }
    }




}