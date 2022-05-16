package com.example.github.repositories

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.example.github.repositories.data.BOOKMARK_EVENT
import com.example.github.repositories.data.LocalDataStore
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

    private var adapter: RepositoryAdapter? = null

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewModel.getBookmarks()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this, UserViewModelFactory(user.login, LocalDataStore(requireContext()))).get()
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

        viewModel.user.observe(viewLifecycleOwner) {
            it?.twitter_username?.let { twitterHandle ->
                if (twitterHandle.isNotEmpty())
                    detail?.text = "Twitter handle: $twitterHandle"
            }

            it?.repos_url?.let { reposUrl ->
                viewModel.fetchRepositories(reposUrl)
            }
        }
        viewModel.repositories.observe(viewLifecycleOwner) {
            adapter = RepositoryAdapter(it.toMutableList(), requireActivity(),
                viewModel.bookmarks.value?: listOf())
            list?.adapter = adapter
        }

        viewModel.showProgress.observe(viewLifecycleOwner) {
            progressBar?.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.error))
                    .setMessage(it)
                    .setCancelable(false)
                    .setPositiveButton(
                        getString(R.string.retry)
                    ) { _, _ ->
                        viewModel.fetchUser()
                    }
                    .show()
                viewModel.resetError()
            }
        }

        viewModel.bookmarks.observe(viewLifecycleOwner) {
            adapter?.updateBookmarks(it)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mReceiver,
            IntentFilter(BOOKMARK_EVENT)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mReceiver)
    }
}