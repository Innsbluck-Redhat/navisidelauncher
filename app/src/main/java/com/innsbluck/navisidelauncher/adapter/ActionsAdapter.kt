package com.innsbluck.navisidelauncher.adapter

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.innsbluck.navisidelauncher.R
import com.innsbluck.navisidelauncher.activity.ActionEditActivity
import com.innsbluck.navisidelauncher.data.Action
import com.innsbluck.navisidelauncher.preference.ActionsPref
import com.innsbluck.navisidelauncher.service.LauncherService
import java.util.*

class ActionsAdapter(val context: Context, private var actions: ArrayList<Action>) :
    RecyclerView.Adapter<ActionsAdapter.ItemViewHolder>() {

    override fun getItemCount(): Int {
        return actions.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.action_item, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ItemViewHolder, position: Int) {
        val action = actions.get(position)
        val info = if (action.appPackage.isEmpty()) context.getString(R.string.action_no_title) else action.appPackage
        viewHolder.actionText.text = action.title + "($info)"

        viewHolder.rootView.setOnClickListener {
            val intent = Intent(context, ActionEditActivity::class.java)
            intent.putExtra("action", action)
            intent.putExtra("position", position)
            context.startActivity(intent)
        }
        viewHolder.rootView.setOnLongClickListener {
            val newActions = actions
            newActions.removeAt(position)
            setActions(newActions)

            val pref = ActionsPref()
            pref.actions = newActions
            pref.save()

            if (isServiceRunning(LauncherService::class.java)) {
                context.stopService(Intent(context, LauncherService::class.java))
                context.startService(Intent(context, LauncherService::class.java))
            }

            true
        }
    }

    fun setActions(actions: ArrayList<Action>) {
        this.actions = actions
        notifyDataSetChanged()
    }

    class ItemViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
        val actionText = rootView.findViewById<TextView>(R.id.action)
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
