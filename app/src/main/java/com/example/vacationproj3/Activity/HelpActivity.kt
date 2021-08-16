package com.example.vacationproj3.Activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help)

        binding.goMain.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
        }

        binding.help1.setOnClickListener {
            runBrowser("http://teenstress.co.kr/",this)
        }
        binding.help2.setOnClickListener {
            runBrowser("https://health.seoulmc.or.kr/",this)
        }
        binding.help3.setOnClickListener {
            runBrowser("https://blutouch.net/index.asp",this)
        }

    }

    fun runBrowser(uri : String, context: Context){
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    }
}