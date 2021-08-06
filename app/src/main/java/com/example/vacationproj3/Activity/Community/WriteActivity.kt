package com.example.vacationproj3.Activity.Community

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivityWriteBinding

class WriteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityWriteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write)

        binding.btnSubmitWrite.setOnClickListener{

        }
    }
}