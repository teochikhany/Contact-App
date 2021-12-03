package com.example.projectfinal.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.projectfinal.R
import androidmads.library.qrgenearator.QRGContents

import androidmads.library.qrgenearator.QRGEncoder




class QrCodeActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)

        val inputValue = intent.getStringExtra("json_contact")

        val qrgEncoder = QRGEncoder(inputValue, null, QRGContents.Type.TEXT, 2000)

        val bitmap = qrgEncoder.encodeAsBitmap();
        val qrImage = findViewById<ImageView>(R.id.qr_code_image)
        qrImage.setImageBitmap(bitmap);
    }
}