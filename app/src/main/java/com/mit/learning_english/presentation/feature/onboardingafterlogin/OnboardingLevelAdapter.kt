package com.mit.learning_english.presentation.feature.onboardingafterlogin

import android.R.attr.text
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mit.learning_english.R
import com.mit.learning_english.databinding.ItemOnboardingLevelBinding
import com.mit.learning_english.domain.model.LearningLevel
import kotlin.math.roundToInt

/**
 * Adapter cho RecyclerView hiển thị danh sách các cấp độ học tập trong màn hình onboarding.
 */
class OnboardingLevelAdapter(
    private val onLevelSelected: (Int) -> Unit
) : ListAdapter<LearningLevel, OnboardingLevelAdapter.VH>(Diff) {

    private var selectedId: Int? = null

    /**
     * Cập nhật danh sách cấp độ mới và ID cấp độ đang được chọn.
     * Sử dụng payload để tối ưu việc cập nhật lại các item.
     */
    fun submitLevels(levels: List<LearningLevel>, selected: Int?) {
        val selectionChanged = selectedId != selected
        selectedId = selected
        when {
            currentList != levels -> submitList(levels)
            selectionChanged -> notifyItemRangeChanged(0, itemCount, PAYLOAD_SELECTION)
            else -> Unit
        }
    }

    /**
     * Tạo ViewHolder mới bằng cách inflate layout ItemOnboardingLevelBinding.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemOnboardingLevelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding, onLevelSelected) { selectedId }
    }

    /**
     * Gán dữ liệu cấp độ học tập tại vị trí tương ứng vào ViewHolder.
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), position)
    }

    /**
     * Gán dữ liệu với Payload hỗ trợ tối ưu hóa, chỉ cập nhật trạng thái chọn nếu có yêu cầu.
     */
    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_SELECTION)) {
            holder.bindSelection(getItem(position), position)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    /**
     * ViewHolder đại diện cho giao diện một mục cấp độ học tập.
     */
    class VH(
        private val binding: ItemOnboardingLevelBinding,
        private val onSelect: (Int) -> Unit,
        private val selectedId: () -> Int?
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Liên kết thông tin tên cấp độ, mô tả của cấp độ và thiết lập sự kiện click chọn.
         */
        fun bind(level: LearningLevel, position: Int) {
            binding.tvLevelName.text = level.name
            val desc = level.description.trim()
            binding.tvLevelDescription.isVisible = desc.isNotEmpty()
            binding.tvLevelDescription.text = desc
            bindSelection(level, position)
            binding.root.setOnClickListener { onSelect(level.id) }
        }

        /**
         * Cập nhật trạng thái chọn (RadioButton, màu viền stroke, độ dày viền) của item hiện tại.
         */
        fun bindSelection(level: LearningLevel, position: Int) {
            val selected = level.id == selectedId()
            binding.rbLevel.isChecked = selected
            val density = binding.root.resources.displayMetrics.density
            binding.root.strokeWidth = if (selected) {
                (1f * density).roundToInt().coerceAtLeast(1)
            } else {
                0
            }
            binding.root.strokeColor = ContextCompat.getColor(
                binding.root.context,
                R.color.primary
            )
            (binding.root.layoutParams as? ViewGroup.MarginLayoutParams)?.let { lp ->
                val defaultTop = (16f * density).roundToInt()
                lp.topMargin = if (position == 0) 0 else defaultTop
            }
        }
    }

    /**
     * Callback DiffUtil dùng để so sánh các phần tử trong danh sách cấp độ.
     */
    private object Diff : DiffUtil.ItemCallback<LearningLevel>() {
        override fun areItemsTheSame(oldItem: LearningLevel, newItem: LearningLevel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: LearningLevel, newItem: LearningLevel): Boolean =
            oldItem == newItem
    }

    companion object {
        private val PAYLOAD_SELECTION = Any()
    }
}
