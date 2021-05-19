package com.eurosportdemo.app.presentation.adapter

import com.eurosportdemo.app.domain.model.Book
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eurosportdemo.app.databinding.CellBookBinding

class BookListAdapter(private val listener: ListAdapterListener, private val buttonLabel: String): RecyclerView.Adapter<BookViewHolder>() {

    private var bookList: List<Book> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemBinding = CellBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        holder.itemBinding.bookTitleTextView.text = book.title
        Glide
            .with(holder.itemView.context)
            .load(book.cover)
            .centerCrop()
            .into(holder.itemBinding.bookCoverImageView)
        holder.itemBinding.bookCellActionButton.text = buttonLabel
        holder.itemBinding.bookCellActionButton.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    fun updateData(bookList: List<Book>) {
        this.bookList = bookList
        notifyDataSetChanged()
    }

}

class BookViewHolder(val itemBinding: CellBookBinding) : RecyclerView.ViewHolder(itemBinding.root)

interface ListAdapterListener {
    fun onItemClick(position: Int)
}