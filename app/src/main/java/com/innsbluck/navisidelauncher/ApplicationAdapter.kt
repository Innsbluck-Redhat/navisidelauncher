package com.innsbluck.navisidelauncher

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class ApplicationAdapter(val context: Context, val packages: MutableList<AppInfoWithName>) :
    RecyclerView.Adapter<ApplicationAdapter.ItemViewHolder>() {

    val packageManager = context.packageManager
    val layoutInflater = LayoutInflater.from(context)
    var listener: OnItemClickListener? = null

    override fun getItemCount(): Int {
        return packages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ItemViewHolder {
        val view = layoutInflater.inflate(R.layout.application_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val appInfo = packages[position]

        holder.appIcon.setImageDrawable(appInfo.appInfo?.loadIcon(packageManager))
        holder.appNameText.text = appInfo.applicationLabel
        holder.appPackageText.text = appInfo.packageName

        holder.rootView.setOnClickListener {
            listener?.onItemClick(appInfo)
        }

    }

    class ItemViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
        val appNameText = rootView.findViewById<TextView>(R.id.name)
        val appPackageText = rootView.findViewById<TextView>(R.id.package_name)
        val appIcon = rootView.findViewById<ImageView>(R.id.icon)
    }

    interface OnItemClickListener {
        fun onItemClick(appInfo: AppInfoWithName)
    }
}
