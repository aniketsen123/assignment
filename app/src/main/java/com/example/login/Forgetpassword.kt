package com.example.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.example.login.databinding.ActivityForgetpasswordBinding

class Forgetpassword : AppCompatActivity() {
    private lateinit var binding:ActivityForgetpasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityForgetpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth= FirebaseAuth.getInstance()
        binding.button.setOnClickListener {
            val email=binding.emailEt.text.toString()
            if(email.isNotEmpty())
            {
                firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {
                       Toast.makeText(this,"Check Your Email",Toast.LENGTH_SHORT).show()
                    val intent=Intent(this,SigInActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener{
                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}