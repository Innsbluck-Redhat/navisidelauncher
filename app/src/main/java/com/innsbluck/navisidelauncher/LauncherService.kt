package com.innsbluck.navisidelauncher

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ListView
import android.widget.Toast


class LauncherService : Service() {
    private val ANIM_DURATION: Long = 200
    private var isAnimating: Boolean = false
    private var opened: Boolean = false
    private lateinit var mShadowView: View
    private lateinit var mLauncherList: ListView
    private var mView: DetectorView? = null
    var x21: Float = 0f
    var x22: Float = 0f
    val MIN_DISTANCE = 70

    lateinit var mAdapter: LauncherAdapter

    companion object {
        @JvmStatic
        fun isRunning(context : Context): Boolean {
            return ServiceUtil.isRunning(context, LauncherService::class.java)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private lateinit var actionsPref: ActionsPref
    private lateinit var settingPref: SettingPref

    override fun onCreate() {
        super.onCreate()

        actionsPref = ActionsPref()
        settingPref = SettingPref()

        val shadowParams = WindowManager.LayoutParams()
        shadowParams.width = WindowManager.LayoutParams.MATCH_PARENT
        shadowParams.height = WindowManager.LayoutParams.MATCH_PARENT
        shadowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        shadowParams.format = PixelFormat.RGBA_8888

        val launcherParams = WindowManager.LayoutParams()
        launcherParams.width = WindowManager.LayoutParams.MATCH_PARENT
        launcherParams.height = WindowManager.LayoutParams.MATCH_PARENT
        launcherParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        launcherParams.format = PixelFormat.RGBA_8888
        launcherParams.gravity = Gravity.END
        launcherParams.verticalMargin = 36f

        val params = WindowManager.LayoutParams()
        params.width = 50
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        params.format = PixelFormat.RGBA_8888
        params.gravity = Gravity.RIGHT or Gravity.BOTTOM

        if (Build.VERSION.SDK_INT >= 26) {
            shadowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            launcherParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            shadowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
            launcherParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }

        mShadowView = View(this)
        mShadowView.setBackgroundColor(ContextCompat.getColor(this, R.color.launcher_background))
        mShadowView.alpha = 0f

        mLauncherList = ListView(this)
        mLauncherList.clipToPadding = false
        mLauncherList.setPadding(0, DpUtil.convertDp2Px(36f, this).toInt(), 0, DpUtil.convertDp2Px(36f, this).toInt())
        mLauncherList.setSelector(android.R.color.transparent)
        mLauncherList.divider = null
        mLauncherList.dividerHeight = 16
        //mLauncherList.setBackgroundColor(ContextCompat.getColor(this, R.color.launcher_background))
        mLauncherList.visibility = GONE
        mShadowView.visibility = GONE
        /*val animation = AnimationUtils.loadAnimation(this, R.anim.item_exit_anim)
        animation.duration = 1
        mLauncherList.startAnimation(animation)*/
        /*val dataArray = arrayListOf(
            Action("Camera", "com.sec.android.app.camera"),
            Action("Spotify", "com.spotify.music"),
            Action("Close", "")
        )*/
        val dataArray = actionsPref.actions

        mAdapter = LauncherAdapter(this, dataArray)
        mLauncherList.adapter = mAdapter/*

        Handler().postDelayed({
            println(adapter.maxWidth)
            for (position in 0 until dataArray.size - 1) {
                val text = getViewByPosition(position, mLauncherList).findViewById<TextView>(R.id.navTextView)
                println(position.toString() + ">" + text.toString())
                val params1 = text.layoutParams
                params1.width = adapter.maxWidth
                text.layoutParams = params1
            }
            adapter.notifyDataSetChanged()
        }, 3000)*/


        mAdapter.listener = (object : LauncherAdapter.OnLauncherItemClickListener {
            override fun onItemClick(action: Action) {
                if (action.appPackage.isNotEmpty()) {
                    val intent = packageManager.getLaunchIntentForPackage(action.appPackage)
                    if (intent != null)
                        startActivity(intent)
                    else
                        Toast.makeText(
                            this@LauncherService,
                            "selected app is not installed",
                            Toast.LENGTH_SHORT
                        ).show()
                }
                if (!isAnimating) closeLauncher()
            }
        })
        mLauncherList.isStackFromBottom = settingPref.isStackFromBottom
        mLauncherList.setOnTouchListener { view, event ->
            if (!isAnimating && event.action == MotionEvent.ACTION_UP && opened)
                closeLauncher()
            false
        }
        mView = DetectorView(this)
        //mView?.setBackgroundColor(ContextCompat.getColor(this, R.color.detect_area))
        mView?.setOnTouchListener(OnTouchListener { view, event ->
            if (isAnimating) return@OnTouchListener false
            val absX = event.x + view.x
            val absY = event.y + view.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x21 = absX
                }
                MotionEvent.ACTION_UP -> {
                    x22 = absX
                    val deltaX = x22 - x21
                    if (deltaX < MIN_DISTANCE) {
                        //Toast.makeText(this, "swipe detected. speed=$deltaX", Toast.LENGTH_SHORT).show()
                        if (!opened) openLauncher()
                    } else {
                        // consider as something else - a screen tap for example
                    }
                }
            }
            return@OnTouchListener false
        })
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.addView(mShadowView, shadowParams)
        wm.addView(mLauncherList, launcherParams)
        wm.addView(mView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(mView)
        mView = null
        (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(mLauncherList)
    }

    fun openLauncher() {
        opened = true
        mLauncherList.visibility = VISIBLE
        mShadowView.visibility = VISIBLE
        mLauncherList.clearAnimation()
        val pvhX = PropertyValuesHolder.ofFloat(TRANSLATION_X, mLauncherList.width.toFloat(), 0f)
        val pvhA = PropertyValuesHolder.ofFloat(ALPHA, 0f, 1f)
        val anim = ObjectAnimator.ofPropertyValuesHolder(mLauncherList, pvhX, pvhA)
        anim.duration = ANIM_DURATION
        anim.interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.accelerate_decelerate)
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isAnimating = false
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                isAnimating = true
            }
        })
        anim.start()
        mShadowView.animate().alphaBy(0f).alpha(1f).setDuration(ANIM_DURATION).start()
    }

    fun closeLauncher() {
        opened = false
        mLauncherList.visibility = VISIBLE
        val pvhX = PropertyValuesHolder.ofFloat(TRANSLATION_X, 0f, mLauncherList.width.toFloat())
        val pvhA = PropertyValuesHolder.ofFloat(ALPHA, 1f, 0f)
        val anim = ObjectAnimator.ofPropertyValuesHolder(mLauncherList, pvhX, pvhA)
        anim.duration = ANIM_DURATION
        anim.interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.accelerate_decelerate)
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                mLauncherList.visibility = GONE
                mShadowView.visibility = GONE
                isAnimating = false
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                isAnimating = true
            }
        })
        anim.start()
        mShadowView.animate().alphaBy(1f).alpha(0f).setDuration(ANIM_DURATION).start()
    }
}