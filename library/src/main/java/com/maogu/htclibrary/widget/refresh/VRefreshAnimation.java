package com.maogu.htclibrary.widget.refresh;

import android.widget.ImageView;

import com.maogu.htclibrary.R;
import com.maogu.htclibrary.util.rx.RxUtil;
import com.maogu.htclibrary.util.rx.SubscriberAdapter;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;

/**
 * @author wangchengm
 *         刷新头部动画
 */
class VRefreshAnimation {
    private boolean isAnimation;
    private ImageView mIvRefresh;
    private Subscription mSubscription;
    private int mIndex;
    private int[] mAnimations = {
            R.mipmap.icon_loading_1, R.mipmap.icon_loading_2, R.mipmap.icon_loading_3, R.mipmap.icon_loading_4,
            R.mipmap.icon_loading_1, R.mipmap.icon_loading_2, R.mipmap.icon_loading_3, R.mipmap.icon_loading_4,
            R.mipmap.icon_loading_1, R.mipmap.icon_loading_2, R.mipmap.icon_loading_3, R.mipmap.icon_loading_4,
            R.mipmap.icon_loading_1, R.mipmap.icon_loading_2, R.mipmap.icon_loading_3, R.mipmap.icon_loading_4,
            R.mipmap.icon_loading_1, R.mipmap.icon_loading_2, R.mipmap.icon_loading_3, R.mipmap.icon_loading_4,
            R.mipmap.icon_loading_1, R.mipmap.icon_loading_2, R.mipmap.icon_loading_3, R.mipmap.icon_loading_4,
            R.mipmap.icon_loading_1, R.mipmap.icon_loading_2, R.mipmap.icon_loading_3, R.mipmap.icon_loading_4,
            R.mipmap.icon_loading_1, R.mipmap.icon_loading_2, R.mipmap.icon_loading_3, R.mipmap.icon_loading_4,
            R.mipmap.icon_loading_1, R.mipmap.icon_loading_2, R.mipmap.icon_loading_3, R.mipmap.icon_loading_4,
            R.mipmap.icon_loading_1, R.mipmap.icon_loading_2, R.mipmap.icon_loading_3, R.mipmap.icon_loading_4,
            R.mipmap.icon_loading_1, R.mipmap.icon_loading_2, R.mipmap.icon_loading_3, R.mipmap.icon_loading_4
    };

    VRefreshAnimation(ImageView ivRefresh) {
        mIvRefresh = ivRefresh;
    }

    void startAnimation() {
        start();
    }

    void stopAnimation() {
        if (null != mSubscription && !mSubscription.isUnsubscribed()) {
            isAnimation = false;
            mSubscription.unsubscribe();
        }
    }

    private void start() {
        if (isAnimation) {
            return;
        }
        mSubscription = Observable.interval(60, TimeUnit.MILLISECONDS)
                .compose(RxUtil.<Long>ioMain())
                .retry().onBackpressureDrop()
                .subscribe(new SubscriberAdapter<Long>() {
                               @Override
                               public void onStart() {
                                   super.onStart();
                                   isAnimation = true;
                               }

                               @Override
                               public void onNext(Long o) {
                                   mIvRefresh.setImageResource(mAnimations[mIndex % mAnimations.length]);
                                   mIndex++;
                               }
                           }
                );
    }
}

