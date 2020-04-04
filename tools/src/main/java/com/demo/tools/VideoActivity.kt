package com.demo.tools

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.util.Util
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYMediaPlayerListener
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.content_video.*
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory



class VideoActivity : AppCompatActivity() {

    var videoUrl = "https://res.exexm.com/cw_145225549855002"

//    var changePlayer:AppCompatButton?= null
//    var playOrPause:AppCompatButton?= null

    var orientationUtils : OrientationUtils?= null

    var isPlay: Boolean = false
    var isPause: Boolean = false
    var lockScreen_:Boolean = false;
    var isUseSystemPlayer :Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        setSupportActionBar(toolbar)

        initPlayer()

        fab.setOnClickListener {
            player?.startPlayLogic()
        }

        initListener()
    }

    private fun initListener() {
        PlayerFactory.setPlayManager(SystemPlayerManager::class.java)
        playOrPause.setOnClickListener {
            player.startButton.performClick()
        }

        changePlayer.setOnClickListener {

            val nowIsPlaying = player.isInPlayingState
            if (nowIsPlaying)
                playOrPause.performClick()

            if (!isUseSystemPlayer) {
                PlayerFactory.setPlayManager(SystemPlayerManager::class.java)
            } else {
                PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
            }

            Log.e("--", "---" + PlayerFactory.getPlayManager()::class.java.simpleName)

            if (nowIsPlaying) {
                playOrPause.performClick()
            }
            isUseSystemPlayer = !isUseSystemPlayer
        }
        orientationUtils?.isRotateWithSystem = true
        orientationUtils?.isEnable = false
        lockScreen.setOnClickListener {
            lockScreen_ = !lockScreen_
            orientationUtils?.isEnable = lockScreen_
            val gsyBuilder = GSYVideoOptionBuilder()
            gsyBuilder
                    .setIsTouchWiget(true)
                    .setRotateViewAuto(lockScreen_)
                    .setLockLand(false)
                    .setAutoFullWithSize(true)
                    .setNeedLockFull(true)
                    .setUrl(videoUrl)
                    .setCacheWithPlay(true)
                    .setVideoTitle("测试video")
                    .setVideoAllCallBack(object : GSYSampleCallBack() {
                        override fun onPrepared(url: String?, vararg objects: Any) {
                            super.onPrepared(url, *objects)
                            //开始播放了才能旋转和全屏
                            orientationUtils?.setEnable(true)
                            isPlay = true
                        }

                        override fun onQuitFullscreen(url: String?, vararg objects: Any) {
                            super.onQuitFullscreen(url, *objects)
                            if (orientationUtils != null) {
                                orientationUtils?.backToProtVideo()
                            }
                        }
                    })
                    .build(player)
        }

    }

    private fun initPlayer() {
        val exoPlay: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this)

        val factory:DataSource.Factory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "tools"));
        val mediaSource:MediaSource = ProgressiveMediaSource.Factory(factory).createMediaSource(Uri.parse(videoUrl))
        exoPlayer.player = exoPlay

        exoPlay.prepare(mediaSource)


        orientationUtils = OrientationUtils(this, player)
        orientationUtils?.isEnable = false

        player?.setUpLazy(videoUrl, true,  null, null, "我是视频播放器")
        player?.titleTextView?.visibility  = GONE

        player?.backButton?.visibility  = GONE
        player?.fullscreenButton?.setOnClickListener {
            player?.startWindowFullscreen(this, false, true)
        }
//        player?.playTag = "title"
        player?.playPosition = 0
        player?.isAutoFullWithSize = true
        player?.isReleaseWhenLossAudio = false
        player?.isShowFullAnimation = true
        player?.setIsTouchWiget(false)


        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setImageResource(R.mipmap.ic_launcher_round)

        val gsyBuilder = GSYVideoOptionBuilder()
        gsyBuilder.setThumbImageView(imageView)
        gsyBuilder
                .setIsTouchWiget(true)
                .setRotateViewAuto(lockScreen_)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setNeedLockFull(true)
                .setUrl(videoUrl)
                .setCacheWithPlay(true)
                .setVideoTitle("测试video")
                .setVideoAllCallBack(object : GSYSampleCallBack() {
                    override fun onPrepared(url: String?, vararg objects: Any) {
                        super.onPrepared(url, *objects)
                        //开始播放了才能旋转和全屏
                        orientationUtils?.setEnable(true)
                        isPlay = true
                    }

                    override fun onQuitFullscreen(url: String?, vararg objects: Any) {
                        super.onQuitFullscreen(url, *objects)
                        if (orientationUtils != null) {
                            orientationUtils?.backToProtVideo()
                        }
                    }
                })
                .build(player)
    }



    override fun onResume() {
        super.onResume()

       player?.onVideoResume()
        isPause = false
    }

    override fun onPause() {
        super.onPause()
        player?.onVideoPause()
        isPause = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay)
            player?.release()
        orientationUtils?.releaseListener()
    }

    override fun onBackPressed() {
        orientationUtils?.backToProtVideo()
        if (GSYVideoManager.backFromWindowFull(this))
            return
        super.onBackPressed()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isPlay && !isPause) {
            player?.onConfigurationChanged(this, newConfig, orientationUtils, true, true)
        }
    }

}
