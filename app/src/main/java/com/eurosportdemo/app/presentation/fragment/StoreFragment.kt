package com.eurosportdemo.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.eurosportdemo.app.R
import com.eurosportdemo.app.databinding.FragmentStoreBinding
import com.eurosportdemo.app.presentation.adapter.BookListAdapter
import com.eurosportdemo.app.presentation.adapter.ListAdapterListener
import com.eurosportdemo.app.presentation.viewModel.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo

@AndroidEntryPoint
class StoreFragment : BaseFragment(), ListAdapterListener {

    private val viewModel: StoreViewModel by viewModels()
    private lateinit var binding: FragmentStoreBinding
    private lateinit var adapter: BookListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.load()
        adapter = BookListAdapter(this, getString(R.string.buy_button))
        binding.bookRecyclerView.adapter = adapter
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.load()
        }
        viewModel
            .viewState
            .subscribe { state ->
                when (state) {
                    is StoreViewModel.ViewState.ShowBookList -> {
                        adapter.updateData(state.bookList)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    is StoreViewModel.ViewState.ShowErrorMessage -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }.addTo(bag)
    }

    override fun onItemClick(position: Int) {
        viewModel.itemClicked(position)
    }

}