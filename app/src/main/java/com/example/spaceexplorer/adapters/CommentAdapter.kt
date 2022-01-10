package com.example.spaceexplorer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.R
import com.example.spaceexplorer.cache.model.Comment
import com.example.spaceexplorer.databinding.CommentItemBinding
import com.example.spaceexplorer.ui.common.CommentItemListener
import com.example.spaceexplorer.util.Constants

class CommentAdapter constructor(
    appExecutors: AppExecutors,
    private val dataBindingComponent: DataBindingComponent,
    private val currentLoggedInUserName: String,
    private val listener: (Comment) -> Unit
) : DataBoundListAdapter<Comment, CommentItemBinding>(appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.apodId == newItem.apodId
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }) {

    override fun createBinding(parent: ViewGroup): CommentItemBinding {
        val binding = DataBindingUtil
            .inflate<CommentItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.comment_item,
                parent,
                false,
                dataBindingComponent
            )

        //set the current logged in user
        binding.user = currentLoggedInUserName

        binding.commentClickListener = object : CommentItemListener {
            override fun clickLikedComment() {
                binding.comment?.let {
                }
                binding.favourite = true
            }

            override fun clickUnlikedComment() {
                binding.comment?.let {
                }
                binding.favourite = false
            }
        }

        binding.deleteImage.setOnClickListener {
            binding.comment.let {
                if (it?.author_name.equals(Constants.getCurrentLoggedInUsername())) {
                    listener.invoke(it!!)
                }
            }
        }

        return binding
    }

    override fun bind(binding: CommentItemBinding, item: Comment) {
        binding.comment = item
    }

}

