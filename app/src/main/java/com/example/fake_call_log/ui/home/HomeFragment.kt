package com.example.fake_call_log.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fake_call_log.R
import com.example.fake_call_log.helper.MyAdapter
import com.example.fake_call_log.helper.SpacingAdapter
import com.example.fake_call_log.model.log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

class HomeFragment : Fragment() {
        companion object {
            @JvmStatic
            lateinit var contactAdapter: MyAdapter
        }
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_home, container, false)
            initRecycleView(view)
            addDataSet()
            return view
        }

        private fun addDataSet() {
            val database = Firebase.database
            val itemRef = database.getReference("Logs")

            val valueEventListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        val all_json = JSONObject(dataSnapshot.value.toString())

                        val json_result = all_json.getJSONObject(ds.key!!)

                        val log = json_result.getJSONArray("Log")

                        val number = log.getString(0)
                        val duration = log.getString(1)
                        val typeInt = log.getString(3)
                        var type = ""
                        when (typeInt) {
                            "1" -> {
                                type = "Incoming"
                            }
                            "2" -> {
                                type = "Outgoing"
                            }
                            "3" -> {
                                type = "Missed Call"
                            }
                            "5" -> {
                                type = "Reject Call"
                            }
                        }
                        val newLog = log(number, duration, type)
                        contactAdapter.add(newLog)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("error", databaseError.message)
                }
            }
            itemRef.addListenerForSingleValueEvent(valueEventListener)
        }

        private fun initRecycleView(view: View) {
            val recycleView = view.findViewById<RecyclerView>(R.id.contact_recycler_view)
            recycleView.layoutManager = LinearLayoutManager(this@HomeFragment.context)
            val padding = SpacingAdapter(30)
            recycleView.addItemDecoration(padding)
            contactAdapter = MyAdapter()

            recycleView.adapter = contactAdapter
        }
}