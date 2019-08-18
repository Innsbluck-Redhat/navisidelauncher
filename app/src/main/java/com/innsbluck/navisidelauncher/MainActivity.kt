package com.innsbluck.navisidelauncher

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    lateinit var mAdapter: ActionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkOverlayPermission() && !LauncherService.isRunning(this)) {
            startService(Intent(this, LauncherService::class.java))
        }
        val toggleButton: Button = findViewById(R.id.toggle_button)
        toggleButton.text = if(LauncherService.isRunning(this)) "STOP SERVICE" else "START SERVICE"
        toggleButton.setOnClickListener {
            if (LauncherService.isRunning(this)) {
                toggleButton.text = "START SERVICE"
                stopService(Intent(this, LauncherService::class.java))
            } else {
                toggleButton.text = "STOP SERVICE"
                startService(Intent(this, LauncherService::class.java))
            }
        }

        var actionsList: RecyclerView = findViewById(R.id.actionsList)
        val actionsPref = ActionsPref()
        if (actionsPref.actions == null) {
            actionsPref.actions = arrayListOf(
                Action("Twitter", "com.twitter.android"),
                Action("Chrome", "com.android.chrome")
            )
            actionsPref.save()
        }

        mAdapter = ActionsAdapter(this, actionsPref.actions)
        actionsList.adapter = mAdapter
        actionsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        val newActionButton: Button = findViewById(R.id.new_action_button)
        newActionButton.setOnClickListener {
            val pref = ActionsPref()
            pref.addAction(Action("New Action", ""))
            pref.save()

            mAdapter.setActions(pref.actions)

            if (LauncherService.isRunning(this)) {
                stopService(Intent(this, LauncherService::class.java))
                startService(Intent(this, LauncherService::class.java))
            }
        }

        val settingFragment = SettingFragment()
        supportFragmentManager.beginTransaction().replace(R.id.setting_fragment_container, settingFragment).commit()
    }

    override fun onResume() {
        super.onResume()

        val actionsPref = ActionsPref()
        mAdapter.setActions(actionsPref.actions)

        if (checkOverlayPermission()) {
            if (LauncherService.isRunning(this)) {
                stopService(Intent(this, LauncherService::class.java))
                startService(Intent(this, LauncherService::class.java))
            }
        } else {
            requestOverlayPermission()
        }
    }

    fun checkOverlayPermission(): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
            return true
        }
        return Settings.canDrawOverlays(this)
    }

    fun requestOverlayPermission() {
        Toast.makeText(this, "「他のアプリに重ねて表示」の権限を許可してください。", Toast.LENGTH_LONG).show()

        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${packageName}"))
        this.startActivityForResult(intent, REQUEST_SYSTEM_OVERLAY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_SYSTEM_OVERLAY -> if (checkOverlayPermission()) {
                startService(Intent(this, LauncherService::class.java))
            } else {
                requestOverlayPermission()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    val REQUEST_SYSTEM_OVERLAY = 0
}
