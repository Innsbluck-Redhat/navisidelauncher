package com.innsbluck.navisidelauncher

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.widget.Button
import android.content.pm.ResolveInfo
import android.content.Intent
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class ActionEditActivity : AppCompatActivity() {

    private lateinit var mApplicationAdapter: ApplicationAdapter
    private lateinit var mAction: Action
    private var mPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_edit)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAction = intent.getSerializableExtra("action") as Action
        mPosition = intent.getIntExtra("position", -1)
        if (mPosition == -1) {
            finish()
        }

        val titleEdit = findViewById<TextInputEditText>(R.id.title_edit)
        val packageEdit = findViewById<TextInputEditText>(R.id.package_edit)
        val saveButton = findViewById<Button>(R.id.save_button)

        titleEdit.setText(mAction.title)
        packageEdit.setText(mAction.appPackage)

        titleEdit.requestFocus()

        saveButton.setOnClickListener {
            val newAction = Action(titleEdit.text.toString(), packageEdit.text.toString())

            val pref = ActionsPref()
            val actions = pref.actions
            actions.set(mPosition, newAction)
            pref.actions = actions
            pref.save()

            finish()
        }

        val applicationList = findViewById<RecyclerView>(R.id.application_list)
        applicationList.layoutManager = LinearLayoutManager(this)

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resInfos = packageManager.queryIntentActivities(intent, 0)
        //using hashset so that there will be no duplicate packages,
        //if no duplicate packages then there will be no duplicate apps
        val packageNames = HashSet<String>(0)
        val appInfos = ArrayList<AppInfoWithName>(0)

        //getting package names and adding them to the hashset
        for (resolveInfo in resInfos) {
            packageNames.add(resolveInfo.activityInfo.packageName)
        }

        //now we have unique packages in the hashset, so get their application infos
        //and add them to the arraylist
        for (packageName in packageNames) {
            try {
                val info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                appInfos.add(AppInfoWithName(
                    info,
                    packageManager.getApplicationLabel(info).toString(),
                    packageName
                ))
            } catch (e: PackageManager.NameNotFoundException) {
            }

        }

        appInfos.sortWith(compareBy { it.applicationLabel })
        mApplicationAdapter = ApplicationAdapter(this, appInfos)
        mApplicationAdapter.listener = object : ApplicationAdapter.OnItemClickListener {
            override fun onItemClick(appInfo: AppInfoWithName) {
                titleEdit.setText(appInfo.applicationLabel)
                packageEdit.setText(appInfo.packageName)
            }
        }
        applicationList.adapter = mApplicationAdapter


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
