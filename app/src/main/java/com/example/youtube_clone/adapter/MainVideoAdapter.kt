package com.example.youtube_clone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.youtube_clone.R
import com.example.youtube_clone.model.VideoModel
import org.w3c.dom.Text

class MainVideoAdapter(val onItemClicked: (String, String) -> Unit): ListAdapter<VideoModel, MainVideoAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        fun bind(videoModel: VideoModel) {
            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
            val subTitleTextView = view.findViewById<TextView>(R.id.subTitleTextView)
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)

            titleTextView.text = videoModel.title
            subTitleTextView.text = videoModel.subtitle
            Glide.with(thumbnailImageView)
                .load(videoModel.thumb)
                .centerCrop()
                .into(thumbnailImageView)

            view.setOnClickListener {
                onItemClicked(videoModel.sources, videoModel.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<VideoModel>() {
            override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}