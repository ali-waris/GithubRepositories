package com.example.github.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.github.repositories.data.LocalDataStore
import com.example.github.repositories.data.RepositoryDTO
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment(private val repository: RepositoryDTO) : Fragment() {

    private var title: TextView? = null
    private var image: ImageView? = null
    private var userImage: ImageView? = null
    private var detail: TextView? = null
    private var description: TextView? = null
    private var url: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        title = view.findViewById(R.id.title)
        image = view.findViewById(R.id.image)
        userImage = view.findViewById(R.id.user_image)
        detail = view.findViewById(R.id.detail)
        description = view.findViewById(R.id.description)
        url = view.findViewById(R.id.url)

        title?.text = repository.name
        detail?.text = String.format("Created by ${repository.owner?.login}${formattedCreatedAt(repository.created_at ?: "")}")
        Picasso.get().load(repository.owner?.avatar_url).into(userImage)
        description?.text = repository.description
        url?.text = repository.html_url

        image?.setImageResource(
            if (LocalDataStore.instance.getBookmarks().contains(repository))
                R.drawable.baseline_bookmark_black_24
            else
                R.drawable.baseline_bookmark_border_black_24
        )
        image?.setOnClickListener {
            val isBookmarked = LocalDataStore.instance.getBookmarks().contains(repository)
            LocalDataStore.instance.bookmarkRepo(repository, !isBookmarked)
            image!!.setImageResource(if (!isBookmarked) R.drawable.baseline_bookmark_black_24 else R.drawable.baseline_bookmark_border_black_24)
        }
        detail?.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, UserFragment(repository.owner!!))
                .addToBackStack("user")
                .commit()
        }
        return view
    }

    private fun formattedCreatedAt(createdAt:String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val output = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault())
            val d: Date? = sdf.parse(createdAt)
            if (d != null) ", at ${output.format(d)}" else ""
        } catch (e: Exception) { "" }
    }
}