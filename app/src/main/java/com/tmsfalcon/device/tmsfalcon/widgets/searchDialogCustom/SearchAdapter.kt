package com.tmsfalcon.device.tmsfalcon.widgets.searchDialogCustom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tmsfalcon.device.tmsfalcon.R
import kotlinx.android.synthetic.main.search_item_custom.view.*

class SearchAdapter(var onSearchItemSelected: OnSearchItemSelected, private var list: ArrayList<SearchListItem>) : RecyclerView.Adapter<SearchAdapter.SearchAdapterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item_custom, parent, false)
        return SearchAdapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SearchAdapterViewHolder, position: Int) {
        val searchListItem = list[position]
        holder.bind(searchListItem, onSearchItemSelected, position)
    }

    class SearchAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(searchListItem: SearchListItem, onSearchItemSelected: OnSearchItemSelected, position: Int) {
            itemView.text1.text = searchListItem.title
            itemView.parent_searchview.setOnClickListener {
                onSearchItemSelected.onClick(position = position, searchListItem = searchListItem)
            }
        }
    }
}