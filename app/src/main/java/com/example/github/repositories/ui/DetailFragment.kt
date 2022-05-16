package com.example.github.repositories.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.github.repositories.viewmodel.DetailViewModel
import com.example.github.repositories.viewmodel.DetailViewModelFactory
import com.example.github.repositories.R
import com.example.github.repositories.data.LocalDataStore
import com.example.github.repositories.data.RepositoryDTO
import com.squareup.picasso.Picasso


class DetailFragment(private val repository: RepositoryDTO) : Fragment() {
    private lateinit var viewModel: DetailViewModel

    private var title: TextView? = null
    private var image: ImageView? = null
    private var userImage: ImageView? = null
    private var detail: TextView? = null
    private var description: TextView? = null
    private var url: TextView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(
            this,
            DetailViewModelFactory(
                LocalDataStore(requireContext()),
                LocalBroadcastManager.getInstance(requireContext())
            )
        ).get()
    }

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
        detail?.text = String.format(
            "Created by ${repository.owner?.login}${
                viewModel.formattedCreatedAt(repository.created_at ?: "")
            }"
        )
        Picasso.get().load(repository.owner?.avatar_url).into(userImage)
        description?.text = repository.description
        url?.text = repository.html_url

        viewModel.bookmarks.observe(viewLifecycleOwner) { bookmarks ->
            image?.setImageResource(
                if (bookmarks.contains(repository.id))
                    R.drawable.baseline_bookmark_black_24
                else
                    R.drawable.baseline_bookmark_border_black_24
            )
        }

        image?.setOnClickListener {
            viewModel.bookmarkRepo(repository.id)
        }

        detail?.setOnClickListener {
            val userBackStack = "user"
            val fragmentManager = requireActivity().supportFragmentManager
            val popToUserFragment = fragmentManager.popBackStackImmediate(userBackStack, 0)
            if (!popToUserFragment) {
                fragmentManager
                    .beginTransaction()
                    .add(android.R.id.content, UserFragment(repository.owner!!))
                    .addToBackStack(userBackStack)
                    .commit()
            }
        }
        return view
    }
}