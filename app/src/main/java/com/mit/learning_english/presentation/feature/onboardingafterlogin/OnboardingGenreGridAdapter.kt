package com.mit.learning_english.presentation.feature.onboardingafterlogin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemOnboardingGenreGridBinding
import com.mit.learning_english.domain.model.Genre
import com.mit.learning_english.presentation.extensions.loadImage
import kotlin.math.roundToInt

/**
 * Adapter cho RecyclerView hiển thị danh sách các thể loại sách dưới dạng Grid trong quá trình onboarding.
 */
class OnboardingGenreGridAdapter(
    private val onGenreToggle: (Int) -> Unit
) : ListAdapter<Genre, OnboardingGenreGridAdapter.VH>(Diff) {

    private var selectedIds: Set<Int> = emptySet()

    /**
     * Cập nhật danh sách thể loại sách mới cùng tập hợp các ID thể loại đang được chọn.
     */
    fun submitGenres(list: List<Genre>, selected: Set<Int>) {
        val selectionChanged = selectedIds != selected
        selectedIds = selected
        when {
            currentList != list -> submitList(list)
            selectionChanged -> notifyDataSetChanged()
            else -> Unit
        }
    }

    /**
     * Tạo ViewHolder mới bằng cách inflate layout ItemOnboardingGenreGridBinding.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemOnboardingGenreGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding, onGenreToggle) { selectedIds }
    }

    /**
     * Gán dữ liệu thể loại sách tại vị trí tương ứng vào ViewHolder.
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder đại diện cho giao diện một mục thể loại sách.
     */
    class VH(
        private val binding: ItemOnboardingGenreGridBinding,
        private val onToggle: (Int) -> Unit,
        private val selectedIds: () -> Set<Int>
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Liên kết dữ liệu thể loại (tên, hình ảnh) và thay đổi trạng thái viền (stroke)
         * dựa trên việc thể loại đó có được chọn hay không. Thiết lập sự kiện click.
         */
        fun bind(genre: Genre) {
            binding.tvGenreName.text = genre.name
            binding.imgGenre.loadImage(genre.thumbnail)
            val selected = genre.id in selectedIds()
            val density = binding.root.resources.displayMetrics.density
            binding.root.strokeWidth = if (selected) (2f * density).roundToInt().coerceAtLeast(1) else 0
            binding.root.strokeColor = ContextCompat.getColor(
                binding.root.context,
                R.color.primary
            )
            binding.root.setOnClickListener { onToggle(genre.id) }
        }
    }

    /**
     * Callback DiffUtil dùng để so sánh các phần tử trong danh sách thể loại nhằm tối ưu hóa việc cập nhật RecyclerView.
     */
    private object Diff : DiffUtil.ItemCallback<Genre>() {
        override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean =
            oldItem == newItem
    }
}
