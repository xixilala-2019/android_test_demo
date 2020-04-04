package com.demo.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by hc on 2019.1.30.
 */
public class RxActivity extends Activity {

    private EditText et;
    private TextView tv;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_rx);
        initView();

        mCompositeDisposable
                .add(Observable.interval(0, 3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver(1)));
    }

    private DisposableObserver getObserver(final int id) {
        return new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };
    }


    private void initView() {
        et = (EditText) findViewById(R.id.et);
        tv = (TextView) findViewById(R.id.tv);
    }
}
