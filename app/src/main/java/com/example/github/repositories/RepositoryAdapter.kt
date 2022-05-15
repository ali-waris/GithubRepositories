package com.example.github.repositories

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.github.repositories.data.LocalDataStore
import com.example.github.repositories.data.RepositoryDTO

class RepositoryAdapter(
    val list: List<RepositoryDTO>,
    val activity: FragmentActivity
) : RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position)
    }

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        private val container: View = itemView.findViewById(R.id.news_container)
        private val titleTxt: TextView = itemView.findViewById(R.id.title)
        private val imageVw: ImageView = itemView.findViewById(R.id.image)
        private val descriptionTxt: TextView = itemView.findViewById(R.id.description)
        private val authorTxt: TextView = itemView.findViewById(R.id.author)

        fun bindData(position: Int) {
            val item = list[adapterPosition]
            titleTxt.text = String.format("#${position + 1}: ${item.full_name?.uppercase()}")
            item.description?.let { desc ->
                descriptionTxt.text = if (desc.length > 150)
                    desc.take(150).plus("...") else desc
            }
            authorTxt.text = item.owner?.login
            imageVw.setImageResource(
                if (LocalDataStore.instance.getBookmarks().contains(item))
                    R.drawable.baseline_bookmark_black_24
                else
                    R.drawable.baseline_bookmark_border_black_24
            )
            container.setOnClickListener {
                activity.supportFragmentManager
                    .beginTransaction()
                    .replace(android.R.id.content, DetailFragment(item))
                    .addToBackStack("detail")
                    .commit()
            }
        }
    }
}