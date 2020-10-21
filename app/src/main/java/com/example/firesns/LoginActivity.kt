package com.example.firesns

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

private const val TAG = "LoginActivity"
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val auth = FirebaseAuth.getInstance() // 유저 credential 확인하는 객체
        if(auth.currentUser != null) {
            goFeedActivity()
        }

        btn_login.setOnClickListener{
            btn_login.isEnabled = false
            val email = et_email.text.toString()
            val password = et_password.text.toString()
            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                btn_login.isEnabled = true
                return@setOnClickListener
            }

            // Connect to Firebase - async operation
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    btn_login.isEnabled = true
                    if(task.isSuccessful){
                        Toast.makeText(this, "환영합니다!", Toast.LENGTH_SHORT).show()
                        goFeedActivity()
                    } else {
                        Log.i(TAG, "로그인 실패", task.exception)
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    private fun goFeedActivity() {
        Log.i(TAG, "navigate to feed")
        val intent = Intent(this, FeedActivity::class.java)
        startActivity(intent)
        finish()
    }
}