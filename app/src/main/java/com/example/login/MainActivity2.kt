package com.example.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.PrimaryKey
import com.example.login.databinding.ActivityMain2Binding
import com.example.login.model.Data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var list:ArrayList<Data>
       var count=0;
    val TAG = "BreakingNewsFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        database= FirebaseDatabase.getInstance().getReference("DATA")
    list= arrayListOf<Data>()

        firebaseAuth = FirebaseAuth.getInstance()
        binding.floatingActionButton.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, SigInActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.floatingActionButton2.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)

            scanner.setBeepEnabled(false)
            scanner.initiateScan()



        }
        binding.recycleview.layoutManager=LinearLayoutManager(this)
        binding.recycleview.setHasFixedSize(true)
        getUserData()

    }

    private fun getUserData() {
        database=FirebaseDatabase.getInstance().getReference("DATA")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    for(usersnapshot in snapshot.children)
                    {
                        val data=usersnapshot.getValue(Data::class.java)
                        if(list.contains(data))
                            continue
                        else
                        list.add(data!!)
                    }
                    binding.recycleview.adapter=com.example.login.Adapter.Adapter(list)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode== Activity.RESULT_OK)
        {
            val result= IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
            if(result!=null)
            {
                if(result.contents==null)
                {
                    Toast.makeText(this,"Cancelled", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    val data= Data(result.contents)
                    if(data.data!="") {
                        count++
                        val sdf = SimpleDateFormat("'Date'dd-MM-yyyy 'andTime'HH:mm:ss z")

                        // on below line we are creating a variable for
                        // current date and time and calling a simple
                        // date format in it.
                        val currentDateAndTime = sdf.format(Date())
                        Toast.makeText(this,currentDateAndTime,Toast.LENGTH_SHORT).show()
                        database.child(currentDateAndTime).setValue(data).addOnSuccessListener {
                            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data)
    }
}