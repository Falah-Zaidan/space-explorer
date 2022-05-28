package com.example.spaceexplorer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.R
import com.example.spaceexplorer.databinding.EditorsPickListItemBinding
import com.example.spaceexplorer.model.EditorsPickPhoto

class EditorsPickAdapter constructor(
    appExecutors: AppExecutors,
    private val dataBindingComponent: DataBindingComponent
) : DataBoundListAdapter<EditorsPickPhoto, EditorsPickListItemBinding>(appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<EditorsPickPhoto>() {
        override fun areItemsTheSame(
            oldItem: EditorsPickPhoto,
            newItem: EditorsPickPhoto
        ): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(
            oldItem: EditorsPickPhoto,
            newItem: EditorsPickPhoto
        ): Boolean {
            return oldItem == newItem
        }
    }) {

    override fun createBinding(parent: ViewGroup): EditorsPickListItemBinding {
        val binding = DataBindingUtil
            .inflate<EditorsPickListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.editors_pick_list_item,
                parent,
                false,
                dataBindingComponent
            )

        return binding
    }

    override fun bind(binding: EditorsPickListItemBinding, item: EditorsPickPhoto) {
        binding.photo = item
    }
}

