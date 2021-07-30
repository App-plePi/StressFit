package com.example.vacationproj3.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.vacationproj3.Data.MyData
import com.example.vacationproj3.Function.Firestore.getMyStressLevel
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.regex.Pattern
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var sf : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor
    private var backKeyPressedTime : Long = 0
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
            if(MyData.uid == "") {
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
                editor.putString("email",binding.inputEmail.text.toString())
                editor.commit()
                editor.putString("pw",binding.inputPw.text.toString())
                editor.commit()

                MyData.uid = Firebase.auth.currentUser?.uid.toString()
                MyData.displayName = Firebase.auth.currentUser?.displayName.toString()
                MyData.photoUrl = Firebase.auth.currentUser?.photoUrl.toString()

                CoroutineScope(Dispatchers.IO).launch {
                    var data1 : String? = null
                    runBlocking {
                        data1 = getMyStressLevel()
                        data1?.let{
                            MyData.stressLevel = data1.toString()
                        }
                    }
                }

                startActivity(Intent(this, MainActivity::class.java))
            } else {
                if(mode == 0) showAlertNoListener("자동로그인 오류",task.exception?.message.toString())
                else showAlertNoListener("로그인 오류",task.exception?.message.toString())
            }

        }
    }


    override fun onBackPressed() {
        lateinit var toast: Toast
        if(System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis()
            toast = Toast.makeText(this,"뒤로가기를 한번 더 누르면 종료됩니다.",Toast.LENGTH_LONG)
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
