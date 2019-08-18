package com.innsbluck.navisidelauncher.activity

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
import com.innsbluck.navisidelauncher.R
import com.innsbluck.navisidelauncher.adapter.ActionsAdapter
import com.innsbluck.navisidelauncher.data.Action
import com.innsbluck.navisidelauncher.fragment.SettingFragment
import com.innsbluck.navisidelauncher.preference.ActionsPref
import com.innsbluck.navisidelauncher.service.LauncherService


class MainActivity : AppCompatActivity() {
    lateinit var mAdapter: ActionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkOverlayPermission() && !LauncherService.isRunning(this)) {
            startService(Intent(this, LauncherService::class.java))
        }
        val toggleButton: Button = findViewById(R.id.toggle_button)
        toggleButton.setText(
            if (LauncherService.isRunning(this)) R.string.start_service else R.string.stop_service
        )

        toggleButton.setOnClickListener {
            if (LauncherService.isRunning(this)) {
                toggleButton.setText(R.string.start_service)
                stopService(Intent(this, LauncherService::class.java))
            } else {
                toggleButton.setText(R.string.stop_service)
                startService(Intent(this, LauncherService::class.java))
            }
        }

        var actionsList: RecyclerView = findViewById(R.id.actionsList)
        val actionsPref = ActionsPref()
        if (actionsPref.actions == null) {
            //デフォルトのアクション
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
            pref.addAction(Action(getString(R.string.new_action_title), getString(R.string.new_action_package)))
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
        Toast.makeText(this, getString(R.string.request_overlay_permission), Toast.LENGTH_LONG).show()

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
