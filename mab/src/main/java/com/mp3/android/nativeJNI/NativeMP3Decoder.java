package com.mp3.android.nativeJNI;

/**
 * Created by hc on 2019.4.24.
 */
public class NativeMP3Decoder {

    static {
        System.loadLibrary("mp3decoder");
    }

    public NativeMP3Decoder() {

    }

    public native int initAudioPlayer(String file, int StartAddr);

    public native int getAudioBuf(short[] audioBuffer, int numSamples);

    public native int getAudioSamplerate();

    public native int getAudioFileSize();

    public native void rePlayAudioFile();

    public native void closeAudioFile();
}
