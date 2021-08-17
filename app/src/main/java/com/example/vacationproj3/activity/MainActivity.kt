package com.example.vacationproj3.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.vacationproj3.activity.community.CommunityActivity
import com.example.vacationproj3.activity.community.WriteActivity
import com.example.vacationproj3.data.MyData
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var backKeyPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.stressLevel.text = MyData.stressLevel


        binding.goTest.setOnClickListener {
            Log.d(">>>", "asfasdf")
            startActivity(Intent(this, SurveyActivity::class.java))
        }

        binding.goHelp.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }

        binding.goSns.setOnClickListener {
            startActivity(Intent(this, CommunityActivity::class.java)) // 엑티비티 이름 작성
        }

        binding.goWrite.setOnClickListener {
            startActivity(Intent(this, WriteActivity::class.java))
        }

        when (MyData.stressLevel) {
            "높음" -> {
                binding.mainBackground.setBackgroundResource(R.drawable.high_background)
                binding.box.setBackgroundResource(R.drawable.high_box)
                binding.text1.setTextColor(Color.parseColor("#FFFFFF"))
                binding.stressLevel.setTextColor(Color.parseColor("#FFFFFF"))
                binding.text2.setTextColor(Color.parseColor("#FFFFFF"))
                binding.text2.setText("스트레스의 원인을 찾고,\n" + "해결해야 할 상황이에요\n")
            }
            "보통" -> {
                binding.mainBackground.setBackgroundResource(R.drawable.mid_background)
                binding.box.setBackgroundResource(R.drawable.mid_box)
                binding.text1.setTextColor(Color.parseColor("#2c2c2c"))
                binding.stressLevel.setTextColor(Color.parseColor("#132b34"))
                binding.text2.setTextColor(Color.parseColor("#2c2c2c"))
                binding.text2.setText("스트레스가 높지는 않지만,\n" + "준수한 상태를 위해 노력이 필요해요!\n")
            }
            "낮음" -> {
                binding.mainBackground.setBackgroundResource(R.drawable.low_background)
                binding.box.setBackgroundResource(R.drawable.low_box)
                binding.text1.setTextColor(Color.parseColor("#2c2c2c"))
                binding.stressLevel.setTextColor(Color.parseColor("#004246"))
                binding.text2.setTextColor(Color.parseColor("#2c2c2c"))
                binding.text2.setText("스트레스가 높지 않은 상태에요!\n" + "낮추는 방안을 공유해주세요!")
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onStart() {
        super.onStart()
        if (MyData.uid == "") startActivity(Intent(this, LoginActivity::class.java))


    }

    override fun onBackPressed() {
        lateinit var toast: Toast
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis()
            toast = Toast.makeText(this, "뒤로가기를 한번 더 누르면 종료됩니다.", Toast.LENGTH_LONG)
            toast.show()
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finishAffinity()
            finish()
            System.runFinalization()
            moveTaskToBack(true)
            finishAndRemoveTask()
            exitProcess(0)
        }
    }


}