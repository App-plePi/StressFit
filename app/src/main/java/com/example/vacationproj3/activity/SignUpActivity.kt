package com.example.vacationproj3.activity

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.vacationproj3.data.MyData
import com.example.vacationproj3.function.Firestore
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivitySignUpBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    var isEmail = false
    var isUsername = false
    var isPw = false
    var isPwC = false

    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.signUpButton.isEnabled = false
        binding.signUpButton.isClickable = false


        val textWatcherEmail = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (emailCheck(binding.inputSignUpEmail.text.toString())) {
                    isEmail = true
                    if (btnEnable()) {
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

        val textWatcherPw = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (passwordCheck(binding.inputSignUpPassword.text.toString())) {
                    isPw = true
                    if (btnEnable()) {
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

        val textWatcherPwC = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (passwordCheckCheck(
                        binding.inputSignUpPassword.text.toString(),
                        binding.inputSignUpPasswordCheck.text.toString()
                    )
                ) {
                    isPwC = true
                    if (btnEnable()) {
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

        val textWatcherUsername = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (usernameCheck(binding.inputSignUpUsername.text.toString())) {
                    isUsername = true
                    if (btnEnable()) {
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
            sf = getSharedPreferences("Auth", MODE_PRIVATE)
            editor = sf.edit()
            editor.putString("email", binding.inputSignUpEmail.text.toString())
            editor.commit()
            val str = Firestore.hashSHA256(binding.inputSignUpPassword.text.toString())
            editor.putString("pw", str)
            editor.commit()
            Log.d(">?<", str)

            signUp(
                binding.inputSignUpEmail.text.toString(),
                str,
                binding.inputSignUpUsername.text.toString(),
                getString(R.string.default_profile_url)
            )
        }
    }

    override fun onStart() {
        super.onStart()
    }

    fun btnEnable(): Boolean {
        return isEmail and isUsername and isPw and isPwC
    }

    fun emailCheck(email: String): Boolean {
        val emailRegex =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        return Pattern.matches(emailRegex, email)

    }

    fun passwordCheck(pw: String): Boolean {
        val pwRegex =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#\$%^&*()+|=])[A-Za-z\\d~!@#\$%^&*()+|=]{8,16}"
        return Pattern.matches(pwRegex, pw)
    }

    fun passwordCheckCheck(pw: String, pwC: String): Boolean {
        return pw.equals(pwC)
    }

    fun usernameCheck(username: String): Boolean {
        return (username.length <= 8)
    }

    fun signUp(email: String, pw: String, username: String, url: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val profile = userProfileChangeRequest {
                    displayName = username
                    photoUri = Uri.parse(url)
                }
                Firebase.auth.currentUser!!.updateProfile(profile).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        MyData.uid = Firebase.auth.currentUser?.uid.toString()
                        MyData.displayName = username
                        MyData.photoUrl = url

                        Firebase.firestore.collection("users").document(MyData.uid).set(
                            hashMapOf(
                                "stressLevel" to "??????"
                            )
                        ).addOnSuccessListener {
                            MyData.stressLevel = "??????"
                            goMainActivity()
                        }.addOnFailureListener {
                            showAlertDialog("???????????? ??????", it.message.toString())
                        }


                    } else {
                        showAlertDialog("???????????? ??????", task.exception?.message.toString())
                    }
                }
            } else {
                showAlertDialog("???????????? ??????", task.exception?.message.toString())
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
        builder.setTitle("???????????? ??????")
        builder.setMessage("????????? ??? ?????? ??????????????????.")
        builder.setPositiveButton("??????") { _: DialogInterface, _: Int ->
            startActivity(Intent(this, MainActivity::class.java))
        }
        builder.show()
    }
}
