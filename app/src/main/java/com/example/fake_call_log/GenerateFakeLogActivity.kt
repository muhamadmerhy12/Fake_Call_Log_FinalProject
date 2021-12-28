package com.example.fake_call_log

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class GenerateFakeLogActivity : AppCompatActivity() {
    private lateinit var mDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_fake_log)

        mDatabase = Firebase.database.reference

        val database = Firebase.database
        mDatabase = database.getReference("Logs")

        mDatabase.addChildEventListener(object : ChildEventListener
        {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?)
            {
                val Json = JSONObject(dataSnapshot.value.toString())
                val log = mutableListOf<String>()
                val result_array: JSONArray = Json.getJSONArray("Log")
                for(i in 0 until result_array.length())
                {
                    log.add(result_array.getString(i))
                    Log.d("arom",result_array.getString(i))
                }
                Log.d("number",result_array.getString(0))
                Log.d("Log",log.toString())
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?)
            {
                return
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot)
            {
                return
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?)
            {
                return
            }

            override fun onCancelled(databaseError: DatabaseError)
            {
                Log.w("online", "postComments:onCancelled", databaseError.toException())
                Toast.makeText(this@GenerateFakeLogActivity, "Failed to load comments.", Toast.LENGTH_SHORT).show()
            }
        })

        var dt=""
        var but = findViewById<Button>(R.id.button)

        var number=findViewById<EditText>(R.id.editTextNumber)
        number.setOnClickListener{
            var i = Intent(Intent.ACTION_PICK)
            i.type= ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(i,123)
        }

        var date=findViewById<EditText>(R.id.editTextDate)
        date.setOnClickListener {
            var c = Calendar.getInstance()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                dt="$i3/${i2+1}/$i"
                TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                    dt+=" $i:$i2 "
                    date.setText(dt)
                },c.get(Calendar.HOUR),c.get(Calendar.MINUTE),false).show()
            },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show()
        }
        var spin = findViewById<Spinner>(R.id.spinner)
        var duration=findViewById<EditText>(R.id.editTextNumber2)

        var button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val Log = mutableListOf<String>()
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

            dt = date.text.toString()
            var asdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
            var type = 1
            var call_type = spin.selectedItem.toString()
            when(call_type){
                "Incoming" ->{type=1}
                "Outgoing" ->{type=2}
                "Missed Call" ->{type=3}
                "Reject Call" ->{type=5}
            }
            try {
                var cv = ContentValues()
                cv.put(CallLog.Calls.NUMBER,number.text.toString())
                Log.add(number.text.toString())

                cv.put(CallLog.Calls.DURATION,duration.text.toString())
                Log.add(duration.text.toString())

                cv.put(CallLog.Calls.DATE,asdf.parse(dt).time)
                Log.add(dt)

                cv.put(CallLog.Calls.TYPE,type)
                Log.add(type.toString())

                contentResolver.insert(CallLog.Calls.CONTENT_URI,cv)

                Toast.makeText(this, "LogInserted", Toast.LENGTH_SHORT).show()
                val Json_result = JSONObject("""{"Log":${JSONArray(Log)}}""")
                mDatabase.child(timeStamp).setValue(Json_result.toString())
            }
            catch(Error: Exception) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
            Log.add(duration.text.toString())

        }

        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALL_LOG)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CALL_LOG),111)
        }
        else
            but.isEnabled=true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123&&resultCode== Activity.RESULT_OK){
            var contacturi : Uri = data?.data ?: return
            var proj: Array<String> = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            var rs: Cursor? = contentResolver.query(contacturi,proj,null,null,null)
            if(rs?.moveToFirst()!!){
                var number=findViewById<EditText>(R.id.editTextNumber)
                number.setText(rs.getString(0))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var but = findViewById<Button>(R.id.button)
        if(requestCode==111&& grantResults[0]== PackageManager.PERMISSION_GRANTED){
            but.isEnabled=true
        }
    }
}