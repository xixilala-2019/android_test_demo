package com.demo.drawtext

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.demo.drawtext.aop.CheckLogin
import com.demo.drawtext.view.TestProgressView
import kotlinx.android.synthetic.main.fragment_first.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
//            progressView.setTProgress(90)
            TLog.info("注解执行了前")
            setProgress()
        }

        val pView = view.findViewById<TestProgressView>(R.id.progressView)
        pView.setOnClickListener{
            TLog.info("222222")
            pView.startNewProgress(90)
        }

    }

    @CheckLogin
    private fun setProgress() {
        progressView.startNewProgress(90)
    }
}
