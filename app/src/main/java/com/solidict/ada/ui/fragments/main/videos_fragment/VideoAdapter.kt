package com.solidict.ada.ui.fragments.main.videos_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.solidict.ada.R
import com.solidict.ada.databinding.VideoRecyclerViewFooterBinding
import com.solidict.ada.databinding.VideoRecyclerViewItemBinding
import com.solidict.ada.model.video.Video
import com.solidict.ada.util.getCurrentDateFormat

class VideoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class VideoViewHolder(val binding: VideoRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(video: Video) {
            binding.videoRecyclerViewWeek.text = video.title
            binding.videoRecyclerViewDate.text = video.created.getCurrentDateFormat()
            // TODO: 7/30/2021 testden sonra bu hisseni sil
            binding.videoRecyclerViewRetryButton.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    onItemClickListener?.let {
                        it(video)
                    }
                }
            }

            when (
                video.status
            ) {
                PROCESSING -> {
                    binding.videoRecyclerViewStatusIcon.setImageResource(R.drawable.ic_time_custom)
                    binding.videoRecyclerViewStatusTextView.text =
                        binding.root.context.getString(R.string.video_status_progressing)
                }
                WARN -> {
                    binding.videoRecyclerViewStatusIcon.setImageResource(R.drawable.ic_error)
                    with(binding.videoRecyclerViewStatusTextView) {
                        text = binding.root.context.getString(R.string.video_status_error)
                        setTextColor(binding.root.context.getColor(R.color.purple_700))
                    }
                    with(binding.videoRecyclerViewRetryButton) {
                        // TODO: 7/30/2021 test ucun her birini visible edecem

                    }
                }
                else -> {
                    binding.videoRecyclerViewStatusIcon.setImageResource(R.drawable.ic_success)
                    binding.videoRecyclerViewStatusTextView.text =
                        binding.root.context.getString(R.string.video_status_success)
                }
            }
        }
    }

    // footer
    inner class FooterViewHolder(private val binding: VideoRecyclerViewFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.videoRecyclerViewFooterButton.setOnClickListener {
                onFooterItemClickListener?.invoke()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == differ.currentList.size) {
            return FOOTER_VIEW_TYPE
        }
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == ITEM_VIEW_TYPE) VideoViewHolder(
            VideoRecyclerViewItemBinding.inflate(
                inflater, parent, false
            )
        ) else {
            FooterViewHolder(
                VideoRecyclerViewFooterBinding.inflate(
                    inflater, parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VideoViewHolder) {
            holder.itemView.apply {
                holder.bind(differ.currentList[position])
            }
        } else if (holder is FooterViewHolder) {
            holder.itemView.apply {
                holder.bind()
            }
        }
    }

    override fun getItemCount() = differ.currentList.size + 1

    val differ = AsyncListDiffer(this, differCallBack)

    companion object {
        private val differCallBack = object : DiffUtil.ItemCallback<Video>() {
            override fun areItemsTheSame(oldItem: Video, newItem: Video) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Video, newItem: Video) =
                oldItem == newItem
        }

        private const val PROCESSING = "PROCESSING"
        private const val WARN = "INAPPROPRIATE"

        private const val ITEM_VIEW_TYPE = 0
        private const val FOOTER_VIEW_TYPE = 1
    }

    private var onItemClickListener: ((Video) -> Unit)? = null
    private var onFooterItemClickListener: (() -> Unit)? = null

    fun setOnItemClickListener(listener: (Video) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnFooterItemClickListener(listener: () -> Unit) {
        onFooterItemClickListener = listener
    }
}



