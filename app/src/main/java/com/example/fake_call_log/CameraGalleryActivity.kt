package com.example.fake_call_log

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class CameraGalleryActivity : AppCompatActivity() {
    private val IMAGE_CHOOSE = 1000
    private val REQUEST_IMAGE_CAPTURE = 1001

    private val PERMISSION_CODE_GALERY = 1002
    private val PERMISSION_CODE_CAMERA = 1003
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_gallery)
        val choosePhoto = findViewById<Button>(R.id.btnChoosePhoto)
        val takePhoto = findViewById<Button>(R.id.btnTakePhoto)

        choosePhoto.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
            {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE_GALERY)
            }
            else
            {
                chooseImageGallery()
            }
        }

        takePhoto.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED)
            {
                val permissions = arrayOf(Manifest.permission.CAMERA)
                requestPermissions(permissions, PERMISSION_CODE_CAMERA)
            }
            else
            {
                takePhoto()
            }
        }
    }

    private fun chooseImageGallery()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)
    }

    private fun takePhoto()
    {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_CODE_GALERY ->
            {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    chooseImageGallery()

                else
                    Toast.makeText(this,"Permission denied for galery", Toast.LENGTH_SHORT).show()
            }

            PERMISSION_CODE_CAMERA ->
            {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    takePhoto()

                else
                    Toast.makeText(this,"Permission denied for camera", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        val image = findViewById<ImageView>(R.id.viewImage)

        if(requestCode == IMAGE_CHOOSE && resultCode == Activity.RESULT_OK)
        {
            image.setImageURI(data?.data)
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
        {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            image.setImageBitmap(imageBitmap)
        }

    }
}