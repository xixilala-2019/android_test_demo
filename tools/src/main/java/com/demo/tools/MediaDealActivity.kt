package com.demo.tools

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_media_deal.*
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * Created by hc on 2019.8.9.
 */
class MediaDealActivity :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_media_deal)

        val holder = surfaceView.holder

        val surface = holder.surface


        var path = Environment.getExternalStorageDirectory().absolutePath + "/333.mp4";
        val viewPlayer = VideoPlayer(surface, path)



        viewPlayer.setCallBack(IPlayerCallBack { width, height, time ->


        })
        viewPlayer.play()
    }



    class MyRunnable(var surface: Surface): Runnable {

        private val TIMEOUT_US: Long = 10000
        private var isPlaying = false

        private val TAG = "mediaDecode ";

        override fun run() {
            initMediaExtrator(surface )
        }

        private fun initMediaExtrator(surface: Surface?) {


            var mediaExtractor = MediaExtractor()

            try {
//                mediaExtractor.setDataSource(path)

                var videoTrackIndex:Int ?= null
                videoTrackIndex = selectTrack(mediaExtractor, false)

                mediaExtractor.selectTrack(videoTrackIndex)

                val mediaFormat = mediaExtractor.getTrackFormat(videoTrackIndex)
//            val width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH)
//            val height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT)
//            val duration = mediaFormat.getLong(MediaFormat.KEY_DURATION)

                val mediaCodeC = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME))
                mediaCodeC.configure(mediaFormat,  surface,null, 0)


                mediaCodeC.start()

                isPlaying = true;
                val videoBufferInfo = MediaCodec.BufferInfo();
                val inputBuffers = mediaCodeC.inputBuffers
                var isVideoEOS = false
                val startMs = System.currentTimeMillis()
                while (!Thread.interrupted()) {
                    if (!isPlaying) {
                        continue
                    }

                    if (!isVideoEOS) {
                        isVideoEOS = putBuffer2Coder(mediaExtractor, mediaCodeC, inputBuffers)
                    }
                    val outputBufferIndex = mediaCodeC.dequeueOutputBuffer(videoBufferInfo, TIMEOUT_US)

                    when(outputBufferIndex) {
                        MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> Log.e(TAG, "format change")
                        MediaCodec.INFO_TRY_AGAIN_LATER -> Log.e(TAG, "time out")
                        MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> Log.e(TAG, "output buffers changed")
                        else -> {
                            delayRender(videoBufferInfo, startMs)
                            mediaCodeC.releaseOutputBuffer(outputBufferIndex, true)
                        }
                    }

                    var flag1= MediaCodec.BUFFER_FLAG_END_OF_STREAM

                    if ( (videoBufferInfo.flags and  MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.e(TAG, "buffer steam en")
                        break
                    }
                    mediaCodeC.stop()
                    mediaCodeC.release()
                    mediaExtractor.release()
                }


            } catch (e:Exception) {
                e.printStackTrace()
            }
        }

        fun delayRender(audioBufferInfo: MediaCodec.BufferInfo, startMs: Long) {

            while (audioBufferInfo.presentationTimeUs / 1000 > System.currentTimeMillis()-startMs) {
                Thread.sleep(10)
            }

        }

        private fun selectTrack(extractor: MediaExtractor, audio: Boolean): Int {
            val numTracks = extractor.trackCount
            for (i in 0 until numTracks) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (audio) {
                    if (mime.startsWith("audio/")) {
                        return i
                    }
                } else {
                    if (mime.startsWith("video/")) {
                        return i
                    }
                }
            }
            return -5
        }

        fun putBuffer2Coder(extractor : MediaExtractor, codeC:MediaCodec, inputBuffers: Array<ByteBuffer>):Boolean {
            var isMediaEos = false
            val inputBufferIndex = codeC.dequeueInputBuffer(TIMEOUT_US)
            if (inputBufferIndex >= 0) {
                val byte = inputBuffers[inputBufferIndex]
                val sampleData  = extractor.readSampleData(byte, 0)
                if (sampleData < 0) {
                    codeC.queueInputBuffer(inputBufferIndex, 0,0,0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                    isMediaEos = true
                } else {
                    codeC.queueInputBuffer(inputBufferIndex, 0, sampleData, extractor.sampleTime, 0)
                }
            }
            return isMediaEos
        }


    }





}