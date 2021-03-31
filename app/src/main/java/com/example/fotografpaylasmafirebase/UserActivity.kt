package com.example.fotografpaylasmafirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class UserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        auth.currentUser?.let {
            val intent = Intent(this,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signIn(view: View) {

        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val currentUser = auth.currentUser?.email.toString()
                Toast.makeText(this,"Hoşgeldin: $currentUser",Toast.LENGTH_LONG).show()
                val intent = Intent(this,FeedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(
                this,
                e.localizedMessage,
                Toast.LENGTH_LONG
            ).show()
        }

    }

    fun signUp(view: View) {

        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            //Asenkron
            if (task.isSuccessful) {
                //diğer activity'e gidicez.
                val intent = Intent(this, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(
                this,
                e.localizedMessage,
                Toast.LENGTH_LONG
            ).show()
        }

    }
}
