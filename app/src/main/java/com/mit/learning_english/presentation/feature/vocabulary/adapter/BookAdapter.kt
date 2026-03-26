//package com.mit.learning_english.presentation.feature.vocabulary.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.paging.PagingDataAdapter
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.mit.learning_english.data.remote.dto.BookRecommendResponse
//import com.mit.learning_english.databinding.ItemBookBinding
//
//class BookAdapter : PagingDataAdapter<BookRecommendResponse, BookAdapter.BookViewHolder>(DIFF_CALLBACK) {
//
//    inner class BookViewHolder(private val binding: ItemBookBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: BookRecommendResponse) {
//            binding.tvTitle.text = item.title
//            binding.tvAuthor.text = item.authorsName
//            binding.tvGenre.text = item.genresName
//            binding.tvLanguage.text = item.language
//            Glide.with(binding.root.context)
//                .load(item.coverUrl)
//                .placeholder(android.R.drawable.ic_menu_gallery)
//                .error(android.R.drawable.ic_menu_gallery)
//                .centerCrop()
//                .into(binding.ivCover)
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
//        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return BookViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
//        getItem(position)?.let { holder.bind(it) }
//    }
//
//    companion object {
//        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BookRecommendResponse>() {
//            override fun areItemsTheSame(
//                oldItem: BookRecommendResponse,
//                newItem: BookRecommendResponse
//            ) = oldItem.id == newItem.id
//
//            override fun areContentsTheSame(
//                oldItem: BookRecommendResponse,
//                newItem: BookRecommendResponse
//            ) = oldItem == newItem
//        }
//    }
//}
