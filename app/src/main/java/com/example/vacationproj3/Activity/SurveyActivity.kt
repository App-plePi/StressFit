package com.example.vacationproj3.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.example.vacationproj3.Data.MyData
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivityMainBinding
import com.example.vacationproj3.databinding.ActivitySurveyBinding
import com.example.vacationproj3.Function.Firestore.getStressQuestions as getStressQuestions



class SurveyActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySurveyBinding
    private var backKeyPressedTime : Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_survey)
        }

    // 이 밑으로는 다시 써야되는 부분임
    /*var doing: Int = 0
    var stressQues = getStressQuestions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun countCheck(count: Int){
        if(count < 10){
            var toast = Toast.makeText(this, "다음 질문으로 이동", Toast.LENGTH_LONG)
        }

        else {
            var toast = Toast.makeText(this, "주어진 설문조사를 모두 마치셨습니다!", Toast.LENGTH_LONG)
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    fun sum(num: Int){

    }*/
}
