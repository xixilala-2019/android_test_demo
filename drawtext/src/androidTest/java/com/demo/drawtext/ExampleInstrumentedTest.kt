package com.demo.drawtext

import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.demo.drawtext.utils.PhoneInfoUtils.getIMEIReflect

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val tm = appContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val did = tm.deviceId
        var imei0 = ""
        try {
            //imei0 = tm.getImei(0)
        } catch (e:Exception) {
            e.printStackTrace()
        }
 

        val imei1 = getIMEIReflect(appContext)

        Log.e("-------------------", "did = $did  imei0=$imei0  imei1=$imei1    ")
    }
}
