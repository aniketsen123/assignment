package com.example.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.example.login.databinding.ActivitySingUpBinding

class SingUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingUpBinding
    private lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
    firebaseAuth= FirebaseAuth.getInstance()
        binding.textView.setOnClickListener {
            val intent=Intent(this,SigInActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.button.setOnClickListener {
            binding.progressBar2.visibility=View.VISIBLE
            val email=binding.emailEt.text.toString()
            val pass=binding.passET.text.toString()
            val confirm=binding.confirmPassEt.text.toString()
            if(email.isNotEmpty() && pass.isNotEmpty() && confirm.isNotEmpty())
            {
                if(pass==confirm)
                {
                firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        val intent=Intent(this,SigInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        binding.progressBar2.visibility=View.GONE
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
                }
                else
                {
                    binding.progressBar2.visibility=View.GONE
                    Toast.makeText(this,"PassWord Doesn't Match",Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                binding.progressBar2.visibility=View.GONE
                Toast.makeText(this,"Enter all the required Field",Toast.LENGTH_SHORT).show()
            }
        }
    }
}