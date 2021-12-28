package com.example.fake_call_log.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.fake_call_log.CameraGalleryActivity
import com.example.fake_call_log.GenerateFakeLogActivity
import com.example.fake_call_log.MapsActivity
import com.example.fake_call_log.R

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val logButton = view.findViewById<Button>(R.id.fakeCallButton)
        logButton.setOnClickListener{
            val intent = Intent(view.context, GenerateFakeLogActivity::class.java)
            startActivity(intent)
        }

        val cameraGalleryButton = view.findViewById<Button>(R.id.cameraGalleryButton)
        cameraGalleryButton.setOnClickListener{
            val intent = Intent(view.context, CameraGalleryActivity::class.java)
            startActivity(intent)
        }

        val mapButton = view.findViewById<Button>(R.id.mapButton)
        mapButton.setOnClickListener{
            val intent = Intent(view.context, MapsActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}