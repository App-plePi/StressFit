package com.example.vacationproj3.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var sf : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)


        binding.inputSignButton.setOnClickListener {
            if(EmailCheck(binding.inputEmail.text.toString()) and PasswordCheck(binding.inputPw.text.toString())) {
                editor.putString("email",binding.inputEmail.text.toString())
                editor.commit()
                editor.putString("pw",binding.inputPw.text.toString())
                editor.commit()
                login(binding.inputEmail.text.toString(), binding.inputPw.text.toString(),1)
            } else {
                showAlertNoListener("로그인 오류","이메일 및 비밀번호 형식을 확인해주세요. 비밀번호는 특수문자, 숫자, 영어를 하나 이상 포함한 8자 이상 16자 이하입니다.")
            }
        }

        binding.inputJoinButton.setOnClickListener {
            if(Firebase.auth.currentUser == null) {
                startActivity(Intent(this,SignUpActivity::class.java ))
            }
        }

    }

    override fun onStart() {
        super.onStart()
        sf =getSharedPreferences("Auth", MODE_PRIVATE)
        editor = sf.edit()
        if(!(sf.getString("email","").equals("")) and !(sf.getString("pw","").equals(""))) {
            login(sf.getString("email","").toString(), sf.getString("pw","").toString(),0)
        }
    }

    fun showAlertNoListener(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK",null)
        builder.show()
    }

    fun EmailCheck(email: String): Boolean {
        val emailRegex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        return Pattern.matches(emailRegex, email)

    }

    fun PasswordCheck(pw: String): Boolean {
        val pwRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#\$%^&*()+|=])[A-Za-z\\d~!@#\$%^&*()+|=]{8,16}"
        return Pattern.matches(pwRegex, pw)
    }

    fun login(email: String, pw: String, mode: Int) {
        Firebase.auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                if(mode == 0) showAlertNoListener("자동로그인 오류",task.exception?.message.toString())
                else showAlertNoListener("로그인 오류",task.exception?.message.toString())
            }

        }
    }

}