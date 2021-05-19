package com.eurosportdemo.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.eurosportdemo.app.R
import com.eurosportdemo.app.databinding.FragmentBasketBinding
import com.eurosportdemo.app.presentation.adapter.BookListAdapter
import com.eurosportdemo.app.presentation.adapter.ListAdapterListener
import com.eurosportdemo.app.presentation.viewModel.BasketViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo

@AndroidEntryPoint
class BasketFragment: BaseFragment(), ListAdapterListener {

    private val viewModel: BasketViewModel by viewModels()
    private lateinit var binding: FragmentBasketBinding
    private lateinit var adapter: BookListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBasketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.load()
        adapter = BookListAdapter(this, getString(R.string.remove_button))
        binding.bookRecyclerView.adapter = adapter
        viewModel
            .viewState
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state ->
            when(state) {
                is BasketViewModel.ViewState.ShowBookList -> {
                    adapter.updateData(state.bookList)
                    binding.bookRecyclerView.visibility = View.VISIBLE
                    binding.offerLabel.visibility = View.VISIBLE
                    binding.noDataLabel.visibility = View.GONE
                    binding.offerLabel.text = getString(R.string.price_label, state.originalPrice, state.offerPrice)
                }
                is BasketViewModel.ViewState.ShowErrorMessage -> {
                    binding.bookRecyclerView.visibility = View.GONE
                    binding.offerLabel.visibility = View.GONE
                    binding.noDataLabel.visibility = View.VISIBLE
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                is BasketViewModel.ViewState.ShowNoData -> {
                    binding.bookRecyclerView.visibility = View.GONE
                    binding.offerLabel.visibility = View.GONE
                    binding.noDataLabel.visibility = View.VISIBLE
                }
            }
        }.addTo(bag)
    }

    override fun onItemClick(position: Int) {
        viewModel.itemClicked(position)
    }
}