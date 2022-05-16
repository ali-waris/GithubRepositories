package com.example.github.repositories.ui

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
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.github.repositories.viewmodel.MainViewModel
import com.example.github.repositories.viewmodel.MainViewModelFactory
import com.example.github.repositories.R
import com.example.github.repositories.data.BOOKMARK_EVENT
import com.example.github.repositories.data.LocalDataStore
import com.example.github.repositories.data.MAX_RECORDS

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    private var swipeRefresh: SwipeRefreshLayout? = null
    private var recyclerview: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    private var adapter: RepositoryAdapter? = null

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewModel.getBookmarks()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this, MainViewModelFactory(LocalDataStore(requireContext()))).get()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        swipeRefresh?.setOnRefreshListener { viewModel.fetchItems(false) }

        recyclerview = view.findViewById(R.id.news_list)
        recyclerview?.layoutManager = LinearLayoutManager(context)

        progressBar = view.findViewById(R.id.progress_bar)

        viewModel.repositories.observe(viewLifecycleOwner) {
            adapter = RepositoryAdapter(it.take(MAX_RECORDS).toMutableList(), requireActivity(),
            viewModel.bookmarks.value?: listOf())
            recyclerview?.adapter = adapter
            swipeRefresh?.isRefreshing = false
        }

        viewModel.showProgress.observe(viewLifecycleOwner) {
            progressBar?.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                swipeRefresh?.isRefreshing = false
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.error))
                    .setMessage(it)
                    .setCancelable(false)
                    .setPositiveButton(
                        getString(R.string.retry)
                    ) { _, _ ->
                        viewModel.fetchItems()
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