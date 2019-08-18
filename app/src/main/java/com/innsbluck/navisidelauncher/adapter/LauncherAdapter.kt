package com.innsbluck.navisidelauncher.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.innsbluck.navisidelauncher.R
import com.innsbluck.navisidelauncher.data.Action


class LauncherAdapter(val context: Context, var actions: ArrayList<Action>) : BaseAdapter() {
    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var listener: OnLauncherItemClickListener? = null

    override fun getCount(): Int {
        return actions.size
    }

    override fun getItem(position: Int): Action {
        return actions[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    var maxWidth: Int = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = layoutInflater.inflate(R.layout.launcher_item, parent, false) //表示するレイアウト取得
        val actionText = view.findViewById<TextView>(R.id.navTextView)
        actionText.setShadowLayer(
            0.5f, 8f, 8f, ContextCompat.getColor(
                context,
                R.color.action_text_shadow
            )
        )


        val action = actions[position]
        actionText.text = action.title
        actionText.setOnClickListener {
            listener?.onItemClick(action)
        }
        if (actionText.width > maxWidth) maxWidth = actionText.width

        return view
    }

    interface OnLauncherItemClickListener {
        fun onItemClick(action: Action)
    }
}