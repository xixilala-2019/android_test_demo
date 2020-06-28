package com.demo.testappprocess

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        requestPermission(this)

        fab.setOnClickListener { view ->
            run {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG) .setAction("Action", null).show()
                getAppProcess()
                Test.getPkgUsageStats()

                startService(Intent(this@MainActivity, GetAppRunningTimeService::class.java))
            }
        }
    }

    override fun onDestroy() {
        Log.e("MainActivity","onDestroy")
        stopService(Intent(this@MainActivity, GetAppRunningTimeService::class.java))
        super.onDestroy()
    }

    fun requestPermission(activity: Activity) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//        activity.startActivityForResult(intent, 1000)
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

    fun log(vararg  log:String) {
        val buffer = StringBuilder()
        for (s in log) {
            if ( s!= null)
                buffer.append(s)
        }
        Log.e("process log",buffer.toString())
    }

    private fun getAppProcess() {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager;
        val appTasks = am.getRunningTasks(1)
        for (app in appTasks) {

            log(app.topActivity.toString(),"\n", app.numRunning.toString())
        }
    }


}
