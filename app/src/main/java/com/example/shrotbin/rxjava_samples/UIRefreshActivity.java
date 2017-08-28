package com.example.shrotbin.rxjava_samples;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class UIRefreshActivity extends AppCompatActivity {

    private TextView mResult, mError;
    private Button mStartRequest;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uirefresh);
        mStartRequest = (Button) findViewById(R.id.start_request);

        mResult = (TextView) findViewById(R.id.result);
        mError = (TextView) findViewById(R.id.request_error);
        mStartRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable observable = Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        for (int i = 0; i < 100; i++) {
                            if (i % 20 == 0) {
                                try {
                                    Thread.sleep(500);
                                } catch (Exception e1) {
                                    if (e.isDisposed()) {
                                        e.onError(e1);
                                    }
                                }
                                e.onNext(i);
                            }
                        }
                        e.onComplete();
                    }
                });
                DisposableObserver disposableObserver = new DisposableObserver() {
                    @Override
                    public void onNext(@NonNull Object o) {
                        mResult.setText("value" + o);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mError.setText(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mResult.setText("complete");
                    }
                };
                observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
                mCompositeDisposable.add(disposableObserver);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
