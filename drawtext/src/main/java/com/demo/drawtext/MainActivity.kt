package com.demo.drawtext

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.demo.drawtext.utils.PhoneInfoUtils
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        Logger.addLogAdapter(AndroidLogAdapter())

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val f1 = FirstFragment()
        val f2 = SecondFragment()
        val f3 = ThirdFragment()
        val f4 = RulerFragment()
        val f5 = FourthFragment()
        val list = ArrayList<Fragment>()
        list.add(f1)
        list.add(f2)
        list.add(f3)
        list.add(f4)
        list.add(f5)
        val adapter = TPagerAdaper(list,supportFragmentManager)

        viewPager.adapter = adapter

        viewPager.currentItem = list.size-1

        val imei1 = PhoneInfoUtils.getIMEIReflect(this)

        Log.e("-------------------", " imei1=$imei1    ")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
