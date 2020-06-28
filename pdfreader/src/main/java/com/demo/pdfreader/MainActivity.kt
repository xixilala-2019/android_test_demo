package com.demo.pdfreader

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

const val READ_SD_CARD_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
class MainActivity : AppCompatActivity() {

    private lateinit var

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (PackageManager.PERMISSION_GRANTED
                != packageManager.checkPermission(READ_SD_CARD_PERMISSION,packageName)) {
            requestPermissions(arrayOf(READ_SD_CARD_PERMISSION),1)
        }

        pdfFilesView.
    }

}
