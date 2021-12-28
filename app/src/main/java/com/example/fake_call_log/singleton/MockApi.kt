package com.example.fake_call_log.singleton;
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONException
import org.json.JSONObject

object MockApi {
private var queue: RequestQueue?=null
private var url= "https://615863b35167ba00174bbad8.mockapi.io/Location"

        fun Queue(context: Context){
        if(queue==null)
        queue=Volley.newRequestQueue(context)
        }

        fun postData(attitude:String,longitude:String){

        val jsonObjectRequest = JsonObjectRequest(
        Request.Method.POST, url, JSONObject("{\"attitude\":${attitude},\"longitude\":${longitude}}"),
        { response -> println(response) },
        { error -> error.printStackTrace() })
        queue!!.add(jsonObjectRequest)
        }
        fun getData(mMap:GoogleMap){
        val jsonArrayRequest = JsonArrayRequest(
        url,
        { response ->
        for (i in 0 until response.length()) {
        try {
        val jsonObject = response.getJSONObject(i)
        val attitude: Double = jsonObject.getDouble("attitude")
        val longitude: Double = jsonObject.getDouble("longitude")
        val latLng:LatLng= LatLng(attitude,longitude)
        mMap.addMarker(MarkerOptions().position(latLng).title("old"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))

        Log.d("Results",response.toString())

        } catch (e: JSONException) {
        e.printStackTrace()
        }
        }
        }) { error ->
        Log.e("Volley", error.toString())
        }
        queue!!.add(jsonArrayRequest)
        }
        fun delete(id:Int){
        val URL_D= "https://615863b35167ba00174bbad8.mockapi.io/Location/$id"
        val jsonObjectRequest = JsonObjectRequest(
        Request.Method.DELETE, URL_D, null,
        { response -> println(response) },
        { error -> error.printStackTrace() })
        queue!!.add(jsonObjectRequest)
        }
        }
