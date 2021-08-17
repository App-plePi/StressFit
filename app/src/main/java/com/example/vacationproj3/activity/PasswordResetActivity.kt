package com.example.vacationproj3.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivityPasswordResetBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class PasswordResetActivity : AppCompatActivity() {
    lateinit var binding: ActivityPasswordResetBinding
    var ec: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_password_reset)
        val context = this
        binding.passwordFind.setOnClickListener {
            if (emailCheck(binding.findMyPasswordInputEmail.text.toString())) {
                Firebase.auth.sendPasswordResetEmail(binding.findMyPasswordInputEmail.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("메일 전송!")
                            builder.setMessage("비밀번호 재설정 메일이 전송되었습니다. 메일함을 확인해주세요.")
                            builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                                startActivity(Intent(context, LoginActivity::class.java))
                            }
                        } else {
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("메일 전송 실패!")
                            builder.setMessage("오류 : " + it.exception?.message)
                            builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                                startActivity(Intent(context, LoginActivity::class.java))
                            }
                        }
                    }

            } else {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("이메일 오류")
                builder.setMessage("이메일 형식을 확인해주세요")
                builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->

                }
            }
        }

    }

    fun emailCheck(email: String): Boolean {
        val emailRegex =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        return Pattern.matches(emailRegex, email)

    }
}