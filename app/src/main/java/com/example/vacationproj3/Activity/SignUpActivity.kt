package com.example.vacationproj3.Activity

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.example.vacationproj3.Data.MyData
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivitySignUpBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    var isEmail = false
    var isUsername = false
    var isPw = false
    var isPwC = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.signUpButton.isEnabled = false
        binding.signUpButton.isClickable = false


        val textWatcherEmail = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(EmailCheck(binding.inputSignUpEmail.text.toString())) {
                    isEmail = true
                    if(BtnEnable()) {
                        binding.signUpButton.isEnabled = true
                        binding.signUpButton.isClickable = true
                    }
                    binding.emailCheck.isVisible = false
                } else {
                    isEmail = false
                    binding.signUpButton.isEnabled = false
                    binding.signUpButton.isClickable = false
                    binding.emailCheck.isVisible = true
                }
            }
        }

        val textWatcherPw = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(PasswordCheck(binding.inputSignUpPassword.text.toString())) {
                    isPw = true
                    if(BtnEnable()) {
                        binding.signUpButton.isEnabled = true
                        binding.signUpButton.isClickable = true
                    }
                    binding.passwordCheck.isVisible = false
                } else {
                    isPw = false
                    binding.signUpButton.isEnabled = false
                    binding.signUpButton.isClickable = false
                    binding.passwordCheck.isVisible = true
                }
            }
        }

        val textWatcherPwC = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(PasswordCheckCheck(binding.inputSignUpPassword.text.toString(), binding.inputSignUpPasswordCheck.text.toString())) {
                    isPwC = true
                    if(BtnEnable()) {
                        binding.signUpButton.isEnabled = true
                        binding.signUpButton.isClickable = true
                    }
                    binding.passwordCheckCheck.isVisible = false
                } else {
                    isPwC = false
                    binding.signUpButton.isEnabled = false
                    binding.signUpButton.isClickable = false
                    binding.passwordCheckCheck.isVisible = true
                }
            }
        }

        val textWatcherUsername = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(UsernameCheck(binding.inputSignUpUsername.text.toString())) {
                    isUsername = true
                    if(BtnEnable()) {
                        binding.signUpButton.isEnabled = true
                        binding.signUpButton.isClickable = true
                    }
                    binding.usernameCheck.isVisible = false
                } else {
                    isUsername = false
                    binding.signUpButton.isEnabled = false
                    binding.signUpButton.isClickable = false
                    binding.usernameCheck.isVisible = true
                }
            }
        }





        binding.inputSignUpEmail.addTextChangedListener(textWatcherEmail)
        binding.inputSignUpPassword.addTextChangedListener(textWatcherPw)
        binding.inputSignUpPasswordCheck.addTextChangedListener(textWatcherPwC)
        binding.inputSignUpUsername.addTextChangedListener(textWatcherUsername)



        binding.signUpButton.setOnClickListener {
            signUp(binding.inputSignUpEmail.text.toString(), binding.inputSignUpPassword.text.toString(), binding.inputSignUpUsername.text.toString(),
                getString(R.string.default_profile_url)
            )
        }
    }

    override fun onStart() {
        super.onStart()
    }

    fun BtnEnable(): Boolean {
        return isEmail and isUsername and isPw and isPwC
    }

    fun EmailCheck(email: String): Boolean {
        val emailRegex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        return Pattern.matches(emailRegex, email)

    }

    fun PasswordCheck(pw: String): Boolean {
        val pwRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#\$%^&*()+|=])[A-Za-z\\d~!@#\$%^&*()+|=]{8,16}"
        return Pattern.matches(pwRegex, pw)
    }

    fun PasswordCheckCheck(pw: String, pwC : String ) : Boolean {
        return pw.equals(pwC)
    }

    fun UsernameCheck(username: String) : Boolean {
        return (username.length <= 8)
    }

    fun signUp(email: String, pw: String, username: String, url: String) {
        Firebase.auth.createUserWithEmailAndPassword(email,pw).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val profile = userProfileChangeRequest {
                    displayName = username
                    photoUri = Uri.parse(url)
                }
                Firebase.auth.currentUser!!.updateProfile(profile).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        MyData.uid = Firebase.auth.currentUser?.uid.toString()
                        MyData.displayName = username
                        MyData.photoUrl = url

                        Firebase.firestore.collection("users").document(MyData.uid).set(hashMapOf(
                            "stressLevel" to "없음"
                        )).addOnSuccessListener {
                            MyData.stressLevel = "없음"
                            goMainActivity()
                        }.addOnFailureListener {
                            showAlertDialog("회원가입 오류",it.message.toString())
                        }


                    } else {
                        showAlertDialog("회원가입 오류",task.exception?.message.toString())
                    }
                }
            } else {
                showAlertDialog("회원가입 오류",task.exception?.message.toString())
            }
        }

    }

    fun showAlertDialog(title: String, message: String) {
        var builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        builder.show()

    }

    fun goMainActivity() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("회원가입 성공")
        builder.setMessage("회원이 된 것을 축하드립니다.")
        builder.setPositiveButton("확인") {_:DialogInterface, _: Int ->
            startActivity(Intent(this,MainActivity::class.java))
        }
        builder.show()
    }
}
