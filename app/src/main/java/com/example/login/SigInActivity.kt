package com.example.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.login.databinding.ActivitySigInBinding

class SigInActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var binding: ActivitySigInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  mCallbackManager:CallbackManager
    private lateinit var googleSigInClient:GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivitySigInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth= FirebaseAuth.getInstance()
        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken( "851332325020-mmok2i8kduu82npt48jq40cgv9v4rtd3.apps.googleusercontent.com").requestEmail().build()
        googleSigInClient=GoogleSignIn.getClient(this,gso)
        binding.google.setOnClickListener {
            signIn()
        }
        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(application);
         mCallbackManager=CallbackManager.Factory.create()
      val facebook=  binding.facebook
        facebook.setReadPermissions("email")
        facebook.registerCallback(mCallbackManager,object:FacebookCallback<LoginResult>{
            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
            }
        })
        binding.textView.setOnClickListener{
            val intent=Intent(this,SingUpActivity::class.java)
            startActivity(intent)
        }
        binding.forgetpassword.setOnClickListener {
            val intent=Intent(this,Forgetpassword::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            binding.progressBar.visibility=View.VISIBLE
            val email=binding.emailEt.text.toString()
            val pass=binding.passET.text.toString()
            if(email.isNotEmpty() && pass.isNotEmpty())
            {
                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {

                    if(it.isSuccessful)
                    {
                        Toast.makeText(this,"Hello" ,Toast.LENGTH_SHORT).show()

                        val intent=Intent(this,MainActivity2::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        binding.progressBar.visibility=View.GONE
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else
            {
                binding.progressBar.visibility=View.GONE
                Toast.makeText(this,"Enter all the fields ",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSigInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }
        private fun handleResults(task: Task<GoogleSignInAccount>) {
            if (task.isSuccessful){
                val account : GoogleSignInAccount? = task.result
                if (account != null){
                    updateUI(account)
                }
            }else{
                Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
            }
        }
    private fun updateUI(account: GoogleSignInAccount) {
        binding.progressBar.visibility=View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.progressBar.visibility=View.GONE
                Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            } else {
                binding.progressBar.visibility=View.GONE
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

            }
        }
    }

        private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this,"Hello" ,Toast.LENGTH_SHORT).show()

                    val intent=Intent(this,MainActivity2::class.java)
                    startActivity(intent)
                    finish()
                    Log.d(TAG, "signInWithCredential:success")

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
       mCallbackManager .onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(p0: View?) {

    }
}