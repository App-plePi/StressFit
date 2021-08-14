package com.example.vacationproj3.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.vacationproj3.Function.Firestore
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivitySurveyBinding
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import com.example.vacationproj3.Function.Firestore.getStressQuestions as getStressQuestions



class SurveyActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySurveyBinding

    private var firstBtnPoint : Int = 0;
    private var secondBtnPoint : Int = 0;
    private var thirdBtnPoint : Int = 0;
    private var fourthBtnPoint : Int = 0;
    private var fifthBtnPoint : Int = 0;

    private var sumPoint : Int = 0;
    private var currentArrayPosition : Int = 0;
    private var stressQues : QuerySnapshot? = null

    private var stressQuestionTextArray : ArrayList<String> = ArrayList()
    private var stressQuestionPointsArray : ArrayList<ArrayList<Int>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_survey)
        CoroutineScope(Dispatchers.Main).launch {
            Log.d(">>>>","Asdfsdf")
            stressQues = getStressQuestions()
            for(docs in stressQues!!) {
                stressQuestionTextArray.add(docs.data["quiz"].toString())
                stressQuestionPointsArray.add(docs.data["point"] as ArrayList<Int>)
                Log.d(">",docs.data["quiz"].toString())
            }
            //changeT()
            binding.question.text = stressQuestionTextArray[currentArrayPosition]
            binding.count.text = currentArrayPosition.toString()

            Log.d(">>",binding.question.text.toString())
            firstBtnPoint = stressQuestionPointsArray[currentArrayPosition][0]
            secondBtnPoint =stressQuestionPointsArray[currentArrayPosition][1]
            thirdBtnPoint = stressQuestionPointsArray[currentArrayPosition][2]
            fourthBtnPoint = stressQuestionPointsArray[currentArrayPosition][3]
            fifthBtnPoint = stressQuestionPointsArray[currentArrayPosition][4]


        }


        binding.answer1.setOnClickListener {
            sumPoint += firstBtnPoint
            btnPressed()
        }
        binding.answer2.setOnClickListener {
            sumPoint += secondBtnPoint
            btnPressed()
        }
        binding.answer3.setOnClickListener {
            sumPoint += thirdBtnPoint
            btnPressed()
        }
        binding.answer4.setOnClickListener {
            sumPoint += fourthBtnPoint
            btnPressed()
        }
        binding.answer5.setOnClickListener {
            sumPoint += fifthBtnPoint
            btnPressed()
        }


    }

    fun btnPressed() {
        currentArrayPosition += 1
        if(currentArrayPosition == 10) {
            var intent = Intent(applicationContext, MainActivity::class.java)
            CoroutineScope(Dispatchers.Main).launch {

                lateinit var lvl : String
                if(sumPoint <= 13) { lvl = "낮음" }
                else if(sumPoint <= 16) { lvl = "보통" }
                else { lvl ="높음" }
                val updateStressLevel = Firestore.updateStressLevel(lvl)
                startActivity(intent)

            }
        } else {
            binding.question.text = stressQuestionTextArray[currentArrayPosition]
            binding.count.text = currentArrayPosition.toString()
            firstBtnPoint = stressQuestionPointsArray[currentArrayPosition][0]
            secondBtnPoint =stressQuestionPointsArray[currentArrayPosition][1]
            thirdBtnPoint = stressQuestionPointsArray[currentArrayPosition][2]
            fourthBtnPoint = stressQuestionPointsArray[currentArrayPosition][3]
            fifthBtnPoint = stressQuestionPointsArray[currentArrayPosition][4]
        }

    }
}
