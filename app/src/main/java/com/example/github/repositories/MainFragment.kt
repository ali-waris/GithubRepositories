package com.example.github.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.github.repositories.data.MAX_RECORDS

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    private var swipeRefresh: SwipeRefreshLayout? = null
    private var recyclerview: RecyclerView? = null
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
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        swipeRefresh?.setOnRefreshListener { viewModel.fetchItems() }

        recyclerview = view.findViewById(R.id.news_list)
        recyclerview?.layoutManager = LinearLayoutManager(context)

        progressBar = view.findViewById(R.id.progress_bar)

        viewModel.repositories.observe(viewLifecycleOwner) {
            val adapter = RepositoryAdapter(it.take(MAX_RECORDS).toMutableList(), requireActivity())
            recyclerview?.adapter = adapter
            swipeRefresh?.isRefreshing = false
        }

        viewModel.showProgress.observe(viewLifecycleOwner) {
            progressBar?.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                swipeRefresh?.isRefreshing = false
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.resetError()
            }
        }
        return view
    }
}