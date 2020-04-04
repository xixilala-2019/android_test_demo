package com.demo.mab;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.chilkatsoft.*;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.ChaCha20Poly1305;
import com.mp3.android.nativeJNI.NativeMP3Decoder;
import com.mp3.android.nativeJNI.NativeMp3Encoder;
import com.mp3.android.nativeJNI.NativeTcpSocket;
import com.mp3.android.nativeJNI.NativeTcpSocket.RecvDoneNotify;
import com.ycbjie.notificationlib.NotificationUtils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener, NativeMp3Encoder.EncoderDoneNotify,RecvDoneNotify{

    static {
        System.loadLibrary("chilkat");

        // Note: If the incorrect library name is passed to System.loadLibrary,
        // then you will see the following error message at application startup:
        //"The application <your-application-name> has stopped unexpectedly. Please try again."
    }

    public static final String TAG = "MP3_ENCODER_DECODER";
    private final static int PLAY_DONE = 100;
    private static final int ENCODER_DONE = 10;
    private static final int RECV_DONE = 11;
    public static final int NUM_CHANNELS = 1;
    public static final int SAMPLE_RATE = 44100;
    public static final int BITRATE = 320;
    public static final int MODE = 1;
    public static final int QUALITY = 2;
    private AudioRecord mRecorder;
    private short[] mBuffer;
    private final String startRecordingLabel = "Start recording";
    private final String stopRecordingLabel = "Stop recording";
    // 作为TCP Client端
    private final static String localIP = "192.168.1.109";
    private final static int localPort = 7788;
    private final static String remoteIP = "192.168.1.106";
    private final static int remotePort = 7799;

    // 作为TCP Server端
//     private final static String localIP = "192.168.1.106";
//     private final static int localPort = 7799;
//     private final static String remoteIP = "192.168.1.109";
//     private final static int remotePort = 7788;

    private boolean mIsRecording = false;
    private File mRawFile;
    private File mEncodedFile;
    private String recvFile;

    private Button recordBtn;
    private Button tcpConBtn;
    private Button tcpBindBtn;
    private LinearLayout local_music_llayout;
    private Button playLocalMp3Btn;
    private Button pauseLocalMp3Btn;
    private Button stopLocalMp3Btn;
    private LinearLayout remote_music_llayout;
    private Button playRemoteMp3Btn;
    private Button pauseRemoteMp3Btn;
    private Button stopRemoteMp3Btn;

    private NativeMp3Encoder mp3Encoder;
    private NativeMP3Decoder mp3Decoder;
    private NativeTcpSocket  tcpsocket;
    private Homehandle handler = null;

    private Thread mThread;
    private short[] audioBuffer;
    private AudioTrack mAudioTrack;
    private int ret;
    private boolean mThreadFlag;
    private int playCurrentPos = 0;
    private boolean playDone = false;
    private boolean isStopPlay = false;
    private int samplerate;
    private int mAudioMinBufSize;

    private VoiceFlashView voiceFlashView;
    List<Integer> mList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lame);
        mp3Encoder = new NativeMp3Encoder(this);
        tcpsocket = new NativeTcpSocket(this,this);
        handler = new Homehandle(this);
        initRecorder();
        tcpsocket.initGlobalObject();
        mp3Encoder.initGlobalObject();
        mp3Encoder.initEncoder(NUM_CHANNELS, SAMPLE_RATE, BITRATE, MODE, QUALITY);

        recordBtn = findViewById(R.id.startRecordBtn);
        recordBtn.setText(startRecordingLabel);
        tcpConBtn = findViewById(R.id.tcpConnectBtn);
        tcpBindBtn = findViewById(R.id.tcpBindBtn);
        local_music_llayout = findViewById(R.id.local_music_llayout);
        local_music_llayout.setVisibility(View.GONE);
        playLocalMp3Btn = findViewById(R.id.playLocalBtn);
        pauseLocalMp3Btn = findViewById(R.id.pauseLocalBtn);
        stopLocalMp3Btn = findViewById(R.id.stopLocalBtn);
        remote_music_llayout = findViewById(R.id.remote_music_llayout);
        remote_music_llayout.setVisibility(View.GONE);
        playRemoteMp3Btn = findViewById(R.id.playRemoteBtn);
        pauseRemoteMp3Btn = findViewById(R.id.pauseRemoteBtn);
        stopRemoteMp3Btn = findViewById(R.id.stopRemoteBtn);

        recordBtn.setOnClickListener(this);
        tcpConBtn.setOnClickListener(this);
        tcpBindBtn.setOnClickListener(this);
        playLocalMp3Btn.setOnClickListener(this);
        pauseLocalMp3Btn.setOnClickListener(this);
        stopLocalMp3Btn.setOnClickListener(this);
        playRemoteMp3Btn.setOnClickListener(this);
        pauseRemoteMp3Btn.setOnClickListener(this);
        stopRemoteMp3Btn.setOnClickListener(this);


        chacha();


        voiceFlashView = findViewById(R.id.mVoiceView);
        voiceFlashView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.clear();
                voiceFlashView.reset();
            }
        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < 144; i++) {

                    int volume = (int) (Math.random()*100);
                    mList.add(volume);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            voiceFlashView.setDataList(mList);
                        }
                    });

                    StringBuffer buffer = new StringBuffer();
                    for (int j = 0; j < mList.size(); j++) {
                        buffer.append( mList.get(j)).append(",");
                    }
//                    Log.e("buffer = ", buffer.toString());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
        thread.start();



        // TCP Client
//        tcpConBtn.setVisibility(View.VISIBLE);
//        tcpBindBtn.setVisibility(View.GONE);

        // TCP Server
//         tcpConBtn.setVisibility(View.GONE);
//         tcpBindBtn.setVisibility(View.VISIBLE);


        findViewById(R.id.PlayLocation).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory(), "q.mp3");
                if (file != null) {
                    mp3Decoder = null;
                    mp3Decoder = new NativeMP3Decoder();
                    int i = mp3Decoder.initAudioPlayer(file.getAbsolutePath(), 0);
                    if (i == -1) {
                        Log.e(TAG , "打开文件失败");
                        return;
                    }
                    mThreadFlag = true;
                    initAudioPlayer();

                    audioBuffer = new short[1024 * 1024];

                    mThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (mThreadFlag) {
                                if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PAUSED
                                        && mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                                    // ****从libmad处获取data******/
                                    playCurrentPos = mp3Decoder.getAudioBuf(audioBuffer, mAudioMinBufSize);
                                    mAudioTrack.write(audioBuffer, 0, mAudioMinBufSize);
                                    Log.d(TAG, "====播放缓冲大小:  " + mAudioMinBufSize
                                            + "====播放的文件位置: ========" + playCurrentPos+"=========文件大小: "+mp3Decoder.getAudioFileSize());
                                    if (playCurrentPos == 0) {
                                        mAudioTrack.stop();
                                    }
                                } else {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                    mThread.start();
                    mAudioTrack.play();
                }
            }
        });

        findViewById(R.id.play).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
                    mAudioTrack.play();
                } else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    Toast.makeText(getApplicationContext(),"已经开始了" , Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.pause).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    mAudioTrack.pause();
                } else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
                    Toast.makeText(getApplicationContext(),"已经停止了" , Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.notice).setOnClickListener(this);



        initPlayFile();

        toSelfSetting(this);




    }

    private void chacha() {

        String key = "zFNuSTtAeKqO0sWhpCRLP3ouMrwvFwSo";
        String nonce = "PKct3bDeBebK";
        String plain = "The eagle has landed.";

        try {
            /*Aead aead = new ChaCha20Poly1305(key.getBytes());
            byte[] encrypt = aead.encrypt(plain.getBytes(), nonce.getBytes());
            System.out.println("加密后--" + BinUtils.toHexString(encrypt));

            String enString1 = "A2F601E1ED039A80A85F0215A2FEE95289DEC5ADE3B9563C19F7C778963FC83461F9051DB3";
            String enString =  "a2f601e1ed039a80a85f0215a2fee95289dec5ade3b9563c19f7c778963fc861f9051db4C0F3E31FFC396308087E119B8D";

            ChaCha20Poly1305 chaCha20Poly1305 = new ChaCha20Poly1305(key.getBytes());
            byte[] decrypt = chaCha20Poly1305.decrypt(BinUtils.hexStringToByteArray(enString1) *//*enString1.getBytes()*//* *//*encrypt*//* , nonce.getBytes());
//            System.out.println("揭秘后--" + BinUtils.toHexString(decrypt));
            System.out.println("揭秘后--" + new String(decrypt));
            Bytes.concat();*/

//            aead.decrypt(encrypt)

        }  catch (Exception e) {
            e.printStackTrace();
            System.out.println("一场了 --" + e.getMessage());
        }





        /*CkCrypt2 crypt = new CkCrypt2();

        // Set the encryption algorithm to chacha20
        // chacha20 is a stream cipher, and therefore no cipher mode applies.
        crypt.put_CryptAlgorithm("chacha20");

        // The key length for chacha20 is always 256-bits.
        crypt.put_KeyLength(256);

        // Note: "padding" only applies to block encryption algorithmns.
        // Since chacha20 is a stream cipher, there is no padding and the output
        // number of bytes is exactly equal to the input.

        // EncodingMode specifies the encoding of the output for
        // encryption, and the input for decryption.
        // Valid modes are (case insensitive) "Base64", "modBase64", "Base32", "Base58", "UU",
        // "QP" (for quoted-printable), "URL" (for url-encoding), "Hex",
        // "Q", "B", "url_oauth", "url_rfc1738", "url_rfc2396", and "url_rfc3986".
        crypt.put_EncodingMode("hex");

        // The inputs to ChaCha20 encryption, specified by RFC 7539, are:
        // 1) A 256-bit secret key.
        // 2) A 96-bit nonce.
        // 3) A 32-bit initial count.
        // The IV property is used to specify the chacha20 nonce.
        // For a 96-bit nonce, the IV should be 12 bytes in length.
        //
        // Note: Some implementations of chacha20, such as that used internally by SSH,
        // use a 64-bit nonce and 64-bit count.  To do chacha20 encryption in this way,
        // simply provide 8 bytes for the IV instead of 12 bytes.  Chilkat will then automatically
        // use 8 bytes (64-bits) for the count.

        // This example duplicates Test Vector #3 (for ChaCha20 encryption) from RFC 7539.
        String ivHex = "000000000000000000000002";
        crypt.SetEncodedIV(ivHex,"hex");

        crypt.put_InitialCount(42);

        String keyHex = "1c9240a5eb55d38af333888604f6b5f0473917c1402b80099dca5cbc207075c0";
        crypt.SetEncodedKey(keyHex,"hex");

        String plainText = "'Twas brillig, and the slithy toves\nDid gyre and gimble in the wabe:\nAll mimsy were the borogoves,\nAnd the mome raths outgrabe.";

        String encStr = crypt.encryptStringENC(plainText);
        Log.i(TAG, "密码 "+encStr);

        // Now decrypt:
        String decStr = crypt.decryptStringENC(encStr);
        Log.i(TAG, "密码 "+decStr);*/

    }

    private void initPlayFile() {
        findViewById(R.id.playTheFile).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory(), "q.mp3");
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r");

                    mp3Decoder = null;
                    mp3Decoder = new NativeMP3Decoder();
                    int i = mp3Decoder.initAudioPlayer(file.getAbsolutePath(), 0);
                    if (i == -1) {
                        Log.e(TAG , "打开文件失败");
                        return;
                    } else {

                        mThreadFlag = true;
                        initAudioPlayer();
                        audioBuffer = new short[1024 * 1024];

                        mThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (mThreadFlag) {
                                    if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PAUSED && mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                                        // ****从libmad处获取data******/
                                        playCurrentPos = mp3Decoder.getAudioBuf(audioBuffer, mAudioMinBufSize);
                                        mAudioTrack.write(audioBuffer, 0, mAudioMinBufSize);
                                        Log.d(TAG, "====播放缓冲大小:  " + mAudioMinBufSize + "====播放的文件位置: ========" + playCurrentPos+"=========文件大小: "+mp3Decoder.getAudioFileSize());
                                        if (playCurrentPos == 0) {
                                            mAudioTrack.stop();
                                        }
                                    } else {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
                        mThread.start();
                        mAudioTrack.play();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecorder.release();
        mRecorder = null;
        mp3Encoder.destroyEncoder();
        mp3Encoder.destroyGlobalObject();
        if(mAudioTrack != null){
            mAudioTrack.stop();
            mAudioTrack.release();// 关闭并释放资源
        }
        mThreadFlag = false;// 音频线程停止
        if(mp3Decoder != null){
            mp3Decoder.closeAudioFile();
        }
        tcpsocket.destroyGlobalObject();
        tcpsocket.destroyTcpSocket();
        System.exit(0);
    }

    private void initRecorder() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mBuffer = new short[bufferSize];
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
    }

    private void initAudioPlayer() {
        samplerate = mp3Decoder.getAudioSamplerate();
        Log.d("zhongjihao", "==========samplerate = " + samplerate);
        samplerate = samplerate / 2;
        // 声音文件一秒钟buffer的大小
        mAudioMinBufSize = AudioTrack.getMinBufferSize(samplerate,
                AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, // 指定在流的类型
                // STREAM_ALARM：警告声
                // STREAM_MUSCI：音乐声，例如music等
                // STREAM_RING：铃声
                // STREAM_SYSTEM：系统声音
                // STREAM_VOCIE_CALL：电话声音

                samplerate,// 设置音频数据的采样率
                AudioFormat.CHANNEL_CONFIGURATION_STEREO,// 设置输出声道为双声道立体声
                AudioFormat.ENCODING_PCM_16BIT,// 设置音频数据块是8位还是16位
                mAudioMinBufSize, AudioTrack.MODE_STREAM);// 设置模式类型，在这里设置为流类型
        // AudioTrack中有MODE_STATIC和MODE_STREAM两种分类。
        // STREAM方式表示由用户通过write方式把数据一次一次得写到audiotrack中。
        // 这种方式的缺点就是JAVA层和Native层不断地交换数据，效率损失较大。
        // 而STATIC方式表示是一开始创建的时候，就把音频数据放到一个固定的buffer，然后直接传给audiotrack，
        // 后续就不用一次次得write了。AudioTrack会自己播放这个buffer中的数据。
        // 这种方法对于铃声等体积较小的文件比较合适。
    }


    private void startBufferedWrite(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream output = null;
                try {
                    output = new DataOutputStream(new BufferedOutputStream(
                            new FileOutputStream(file)));
                    while (mIsRecording) {
                        int readSize = mRecorder.read(mBuffer, 0,
                                mBuffer.length);
                        for (int i = 0; i < readSize; i++) {
                            output.writeShort(mBuffer[i]);
                        }
                    }
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            Toast.makeText(MainActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                Toast.makeText(MainActivity.this,
                                        e.getMessage(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private File getFile(final String suffix) {
        Time time = new Time();
        time.setToNow();
        return new File(Environment.getExternalStorageDirectory(),
                time.format("%Y%m%d%H%M%S") + "." + suffix);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        switch (vid) {
            case R.id.startRecordBtn:
                if (!mIsRecording) {
                    recordBtn.setText(stopRecordingLabel);
                    mIsRecording = true;
                    mRecorder.startRecording();
                    mRawFile = getFile("raw");
                    startBufferedWrite(mRawFile);
                } else {
                    recordBtn.setText(startRecordingLabel);
                    mRecorder.stop();
                    mIsRecording = false;
                    mEncodedFile = getFile("mp3"); /// 录音完成后编码
                    int result = mp3Encoder.encodeFile(mRawFile.getAbsolutePath(),
                            mEncodedFile.getAbsolutePath());
                    if (result == 0) {
                        Toast.makeText(MainActivity.this,
                                "Encoded to " + mEncodedFile.getName(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.tcpConnectBtn:
                tcpsocket.tcpConnect(remoteIP, remotePort);
                break;
            case R.id.tcpBindBtn:
                tcpsocket.tcpBind(localIP, localPort);
                tcpsocket.setRecvDir(Environment
                        .getExternalStorageDirectory().getAbsolutePath());
                break;
            case R.id.playLocalBtn:
                if(isStopPlay){
                    ret = mp3Decoder.initAudioPlayer(mEncodedFile.getAbsolutePath(), 0);
                    isStopPlay = false;
                }
                if (ret == -1) {
                    Log.e("zhongjihao", "==========Couldn't open file " + mEncodedFile.getAbsolutePath());
                    Toast.makeText(this, "Couldn't open file " + mEncodedFile.getAbsolutePath(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED) {
                        if (playDone) {
                            mp3Decoder.rePlayAudioFile();
                        }
                        mAudioTrack.play();
                        playDone = false;
                    } else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
                        mAudioTrack.play();
                    } else {
                        Toast.makeText(getApplicationContext(), "Already in play",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.pauseLocalBtn:
            case R.id.pauseRemoteBtn:
                if (ret == -1) {
                    Log.e("zhongjihao", "========Couldn't open file " + mEncodedFile.getAbsolutePath());
                    Toast.makeText(this, "Couldn't open file " + mEncodedFile.getAbsolutePath(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                        mAudioTrack.pause();
                    } else {
                        Toast.makeText(getApplicationContext(), "Already pause",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.stopLocalBtn:
            case R.id.stopRemoteBtn:
                if (ret == -1) {
                    Log.e("zhongjihao", "========Couldn't open file " + mEncodedFile.getAbsolutePath());
                    Toast.makeText(this, "Couldn't open file " + mEncodedFile.getAbsolutePath(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING
                            ||mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
                        isStopPlay = true;
                        mAudioTrack.stop();
                        mp3Decoder.closeAudioFile();
                    } else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED) {
                        Toast.makeText(getApplicationContext(), "Already stop",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.playRemoteBtn:
                if(isStopPlay){
                    ret = mp3Decoder.initAudioPlayer(recvFile, 0);
                    isStopPlay = false;
                }
                if (ret == -1) {
                    Log.e("zhongjihao", "==========Couldn't open file " + recvFile);
                    Toast.makeText(this, "Couldn't open file " + recvFile,
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED) {
                        if (playDone) {
                            mp3Decoder.rePlayAudioFile();
                        }
                        mAudioTrack.play();
                        playDone = false;
                    } else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
                        mAudioTrack.play();
                    } else {
                        Toast.makeText(getApplicationContext(), "Already in play",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.notice:
               /* if (isShow) {
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                    mNotificationManager.cancel(1234);
                    mNotificationManager.cancelAll();

                } else
                    addNotice(MainActivity.this);*/
                isShow = !isShow;
                delayNotice();
                break;
        }

    }

    private void delayNotice() {

        playLocalMp3Btn.postDelayed(new Runnable() {
            @Override
            public void run() {
                NotificationUtils utils = new NotificationUtils(App.getInstance());
                utils.sendNotification(1, "title", "content", R.mipmap.ic_launcher);
            }
        }, 2*1000);
    }

    @Override
    public void encoderNotify() {
        Log.d("MainActivity", "========encoderNotify()=======");
        if(mp3Decoder != null){
            mp3Decoder.closeAudioFile();
        }
        playDone = false;
        isStopPlay = false;
        handler.sendEmptyMessage(ENCODER_DONE);

        //编码完后,先不要启动发送线程
//        tcpsocket.setSendFilePath(mEncodedFile.getAbsolutePath());
//        tcpsocket.startSendThread();
    }

    private static class Homehandle extends Handler {
        private WeakReference<MainActivity> wref;

        public Homehandle(MainActivity act) {
            wref = new WeakReference<MainActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            final MainActivity act = wref.get();
            if (act == null) {
                return;
            }
            switch (msg.what) {
                case ENCODER_DONE:
                    Toast.makeText(act, "录音文件已编码完毕!", Toast.LENGTH_SHORT).show();
                    act.local_music_llayout.setVisibility(View.VISIBLE);
                    if(act.mp3Decoder == null){
                        act.mp3Decoder = new NativeMP3Decoder();
                        act.ret = act.mp3Decoder.initAudioPlayer(act.mEncodedFile.getAbsolutePath(), 0);
                        if (act.ret == -1) {
                            Log.e("zhongjihao", "====Couldn't open file '" + act.mEncodedFile.getAbsolutePath());
                        } else {
                            act.mThreadFlag = true;
                            act.initAudioPlayer();
                            act.audioBuffer = new short[1024 * 1024];
                            act.mThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (act.mThreadFlag) {
                                        if (act.mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PAUSED
                                                && act.mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                                            // ****从libmad处获取data******/
                                            act.playCurrentPos = act.mp3Decoder.getAudioBuf(
                                                    act.audioBuffer, act.mAudioMinBufSize);
                                            act.mAudioTrack.write(act.audioBuffer, 0, act.mAudioMinBufSize);
                                            Log.d("", "====播放缓冲大小:  " + act.mAudioMinBufSize
                                                    + "====播放的文件位置: ========" + act.playCurrentPos+"=========文件大小: "+act.mp3Decoder.getAudioFileSize());
                                            if (act.playCurrentPos == 0) {
                                                act.mAudioTrack.stop();
                                                act.handler.sendEmptyMessage(PLAY_DONE);
                                                act.playDone = true;
                                            }
                                        } else {
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            });
                            act.mThread.start();
                        }
                    }else{
                        act.ret = act.mp3Decoder.initAudioPlayer(act.mEncodedFile.getAbsolutePath(), 0);
                        act.initAudioPlayer();
                    }
                    break;
                case RECV_DONE:
                    Toast.makeText(act, "收到新的语音消息!", Toast.LENGTH_SHORT).show();
                    act.remote_music_llayout.setVisibility(View.VISIBLE);
                    if(act.mp3Decoder == null){
                        act.mp3Decoder = new NativeMP3Decoder();
                        act.ret = act.mp3Decoder.initAudioPlayer(act.recvFile, 0);
                        if (act.ret == -1) {
                            Log.e("zhongjihao", "====Couldn't open file '" + act.recvFile);
                        } else {
                            act.mThreadFlag = true;
                            act.initAudioPlayer();
                            act.audioBuffer = new short[1024 * 1024];
                            act.mThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (act.mThreadFlag) {
                                        if (act.mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PAUSED
                                                && act.mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                                            // ****从libmad处获取data******/
                                            act.playCurrentPos = act.mp3Decoder.getAudioBuf(
                                                    act.audioBuffer, act.mAudioMinBufSize);
                                            act.mAudioTrack.write(act.audioBuffer, 0, act.mAudioMinBufSize);
                                            Log.d("", "====播放缓冲大小:  " + act.mAudioMinBufSize
                                                    + "====播放的文件位置: ========" + act.playCurrentPos);
                                            if (act.playCurrentPos == act.mp3Decoder.getAudioFileSize()) {
                                                act.mAudioTrack.stop();
                                                act.playCurrentPos = 0;
                                                act.handler.sendEmptyMessage(PLAY_DONE);
                                                act.playDone = true;
                                            }
                                        } else {
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            });
                            act.mThread.start();
                        }
                    }else{
                        act.ret = act.mp3Decoder.initAudioPlayer(act.recvFile, 0);
                    }
                    break;
                case PLAY_DONE:
                    Toast.makeText(act, "play done", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void audioRecvDone(String audioFile) {
        Log.d("MainActivity", "========audioRecvDone()=======");
        recvFile = audioFile;
        handler.sendEmptyMessage(RECV_DONE);
    }

    private String tag = "com.demo.mab";
    private boolean isShow = false;
    public void addNotice(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String channelID = "story";
        NotificationChannel channelBody;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channelBody = new NotificationChannel(channelID, channelID, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channelBody);
        }

        RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification);
        mRemoteViews.setTextViewText(R.id.artist,"artist");
        mRemoteViews.setTextViewText(R.id.title, "title");
        mRemoteViews.setImageViewResource(R.id.image, R.mipmap.ic_launcher_round);

        Intent intent = new Intent(tag);
        intent.putExtra("key", "32221");
        intent.setPackage(tag);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setChannelId(channelID)
                .setPriority(12)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(mRemoteViews);
        Notification notificationCompat = mBuilder.build();

        mNotificationManager.notify(tag,1234, notificationCompat);

        /*File file = ImageUtils.loadAudioImage(FileManager.getNameFromUrl(story.getSpeaker_avatar_url()));
        if (file != null && file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null)
                mRemoteViews.setImageViewBitmap(R.id.image, bitmap);
            else
                mRemoteViews.setImageViewResource(R.id.image, R.mipmap.ic_audio_default_speaker);
        } else {

            Glide.with(context).asBitmap()
                    .load(!TextUtils.isEmpty(story.getSpeaker_avatar_url()) ? FileManager.getHttpUrl(story.getSpeaker_avatar_url()).replace("\\", "") : "")
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap header, @Nullable Transition<? super Bitmap> transition) {
                            mRemoteViews.setImageViewBitmap(R.id.image, header);
                        }
                    });
        }*/
    }

    private static PendingIntent startApp(Context context) {
        Intent intent = new Intent (context, Main2Activity.class) ;
        return  PendingIntent.getActivity(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }



    public static void toSelfSetting(Context context) {

        Intent mIntent = new Intent();
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            mIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        context.startActivity(mIntent);
    }
   /* public static */

    public class AutoReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("123")) {
                NotificationUtils utils = new NotificationUtils(context);
                utils.sendNotification(1, "title", "content", R.mipmap.ic_launcher);
            }
        }
    }
}
