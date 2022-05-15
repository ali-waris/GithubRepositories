package com.example.github.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import com.example.github.repositories.data.OwnerDTO
import com.squareup.picasso.Picasso

class UserFragment(private val user: OwnerDTO) : Fragment() {

    private lateinit var viewModel: UserViewModel

    private var title: TextView? = null
    private var image: ImageView? = null
    private var detail: TextView? = null
    private var url: TextView? = null
    private var list: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        title = view.findViewById(R.id.title)
        image = view.findViewById(R.id.image)
        detail = view.findViewById(R.id.detail)
        url = view.findViewById(R.id.url)
        list = view.findViewById(R.id.list)
        progressBar = view.findViewById(R.id.progress_bar)

        title?.text = user.login
        Picasso.get().load(user.avatar_url.toUri()).into(image)

        viewModel.fetchUser(user.login)
        viewModel.user.observe(viewLifecycleOwner) {
            it.twitter_username?.let { twitterHandle ->
                if (twitterHandle.isNotEmpty())
                    detail?.text = "Twitter handle: $twitterHandle"
            }
            viewModel.fetchRepositories(it.repos_url!!)
        }
        viewModel.repositories.observe(viewLifecycleOwner) {
            list?.adapter = RepositoryAdapter(it.toMutableList(), requireActivity())
        }

        viewModel.showProgress.observe(viewLifecycleOwner) {
            progressBar?.visibility = if (it) View.VISIBLE else View.GONE
        }
        return view
    }
}