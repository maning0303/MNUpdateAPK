package com.maning.updatelibrary.utils;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;

/**
 * author : maning
 * time   : 2018/06/04
 * desc   :
 * version: 1.0
 */
public class ActResultRequest {
    private OnActResultEventDispatcherFragment fragment;

    public ActResultRequest(Activity activity) {
        fragment = getEventDispatchFragment(activity);
    }

    private OnActResultEventDispatcherFragment getEventDispatchFragment(Activity activity) {
        final FragmentManager fragmentManager = activity.getFragmentManager();

        OnActResultEventDispatcherFragment fragment = findEventDispatchFragment(fragmentManager);
        if (fragment == null) {
            fragment = new OnActResultEventDispatcherFragment();
            fragmentManager
                    .beginTransaction()
                    .add(fragment, OnActResultEventDispatcherFragment.TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return fragment;
    }

    private OnActResultEventDispatcherFragment findEventDispatchFragment(FragmentManager manager) {
        return (OnActResultEventDispatcherFragment) manager.findFragmentByTag(OnActResultEventDispatcherFragment.TAG);
    }

    public void startForResult(Intent intent, ActForResultCallback callback) {
        fragment.startForResult(intent, callback);
    }

}
