package com.innsbluck.navisidelauncher.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.innsbluck.navisidelauncher.R
import com.innsbluck.navisidelauncher.preference.SettingPref
import com.innsbluck.navisidelauncher.service.LauncherService
import com.innsbluck.navisidelauncher.util.ServiceUtil

class SettingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        val vgSpinner = view.findViewById<Spinner>(R.id.v_gravity_spinner)
        vgSpinner.adapter =
            ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, listOf("TOP", "BOTTOM"))
        vgSpinner.setSelection(if (SettingPref().isStackFromBottom) 1 else 0)
        vgSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?, position: Int, id: Long
            ) {
                val stackFromBottom: Boolean = when (position) {
                    0 -> false
                    1 -> true
                    else -> false
                }

                val settingPref = SettingPref()
                settingPref.isStackFromBottom = stackFromBottom
                settingPref.save()

                if (ServiceUtil.isRunning(context, LauncherService::class.java)) {
                    context?.stopService(Intent(context, LauncherService::class.java))
                    context?.startService(Intent(context, LauncherService::class.java))
                }

            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        }
        return view
    }
}
