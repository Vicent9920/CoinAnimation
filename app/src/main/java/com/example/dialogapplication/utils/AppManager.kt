package com.example.dialogapplication.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.Stack

object AppManager {
    private var mActivities: Stack<Activity> = Stack()

    /**
     * 添加Activity到堆栈
     */
    fun attach(activity: Activity) {
        mActivities.add(activity)
    }

    /**
     * 将指定Activity移除
     */
    fun detach(activity: Activity) {
        mActivities.remove(activity)
    }

    /**
     * Get current activity (the last into Stack)
     */
    fun currentActivity(): Activity {
        return mActivities.lastElement()
    }

    /**
     * Finish current activity (the last into Stack)
     */
    fun finishActivity() {
        val a = mActivities.lastElement()
        a.finish()
    }

    /**
     * Finish the input activity
     */
    fun finishActivity(activity: Activity?) {
        var size = -1
        for (i in mActivities.indices) {
            val a = mActivities[i]
            if (a == activity) {
                size = i
                break
            }
        }
        if (size != -1) {
            mActivities.removeAt(size)
            activity?.finish()
        }
    }

    /**
     * finish the activity of afferent class
     */
    fun finishActivity(cls: Class<*>) {
        for (i in mActivities.indices) {
            val a = mActivities[i]
            if (a.javaClass.canonicalName == cls.canonicalName) {
                mActivities.removeAt(i)
                a.finish()
                break
            }
        }
    }

    /**
     * Judge the input Activity is live or die
     */
    fun isLive(activity: Activity): Boolean {
        return mActivities.contains(activity)
    }

    /**
     * Judge the Activity instance of the input class is live or die
     * (the Activity can have more than one object)
     */
    fun isLive(cls: Class<*>): Boolean {
        mActivities.forEach {
            if (it.javaClass.canonicalName == cls.canonicalName)
                return true
        }
        return false
    }

    /**
     * Finish all activity of th mActivities and make mActivities clear
     */
    fun finishAllActivity() {
        mActivities.forEach {
            it?.finish()
        }
        mActivities.clear()
    }

    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                attach(activity)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                detach(activity)
            }

        })
    }
}