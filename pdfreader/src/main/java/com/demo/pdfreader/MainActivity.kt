package com.demo.pdfreader

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

const val READ_SD_CARD_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (PackageManager.PERMISSION_GRANTED
                != packageManager.checkPermission(READ_SD_CARD_PERMISSION,packageName)) {
            requestPermissions(arrayOf(READ_SD_CARD_PERMISSION),1)
        }


    }

}
