package com.demo.testvideoprocess.util

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Environment
import android.util.Log
import java.io.File
import java.nio.ByteBuffer

/**
 * Created by hc on 2020.4.4.
 */
class ProcessVideo  {

    val VIDEO_MIME = "video/"
    val AUDIO_MIME = "audio/"

    fun getExternalStroragePath():String {
        val path = Environment.getExternalStorageDirectory()
        for (file in path.list()) {
            Log.e("file","file = ${file}")
        }
        return path.absolutePath
    }

    fun getVideoFile(): String {
        return File(getExternalStroragePath(), "abc.mp4").absolutePath
    }

    fun getSavedVideoFile():File {
        return File(getExternalStroragePath(), "newabc.mp4")
    }

    fun processExtractor () {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(getVideoFile())

        var videoTrackIndex = -1
        var audioTrackIndex = -1
        var frameRate = 0

        val trackCount = mediaExtractor.trackCount

        val mediaMuxer = MediaMuxer(getSavedVideoFile().absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        for (index in 0 until trackCount) {

            val trackFormat = mediaExtractor.getTrackFormat(index)

            val mime = trackFormat.getString(MediaFormat.KEY_MIME)
            if (mime.startsWith(VIDEO_MIME)) {
                frameRate = trackFormat.getInteger(MediaFormat.KEY_FRAME_RATE)
                mediaExtractor.selectTrack(index)
                videoTrackIndex = mediaMuxer.addTrack(trackFormat)
                mediaMuxer.start()
                break
            }
            if (mime.startsWith(AUDIO_MIME)) {
                audioTrackIndex = index
                continue
            }

            Log.e("","mime = $mime")
        }

        if (videoTrackIndex == -1 && audioTrackIndex == -1) {
            mediaExtractor.release()
            return
        }

//        extractorVideo(mediaExtractor, mediaMuxer, videoTrackIndex)

        val buff = ByteBuffer.allocate(1024 * 1024)
        val bufferInfo = MediaCodec.BufferInfo()
        bufferInfo.presentationTimeUs = 0
        val tt = 1000 * 1000L
        while (true) {
            val sampleSize = mediaExtractor.readSampleData(buff, 0)
            if (sampleSize < 0) {
                break
            }
            bufferInfo.offset = 0
            bufferInfo.size = sampleSize
            bufferInfo.presentationTimeUs += tt / frameRate
            // MediaCodec.BUFFER_FLAG_SYNC_FRAME 中间绿
            // mediaExtractor.sampleFlags 开头绿
            bufferInfo.flags = mediaExtractor.sampleFlags
            mediaMuxer.writeSampleData(videoTrackIndex, buff, bufferInfo)

            mediaExtractor.advance()
        }

        mediaExtractor.release()

        mediaMuxer.stop()
        mediaMuxer.release()
    }

}