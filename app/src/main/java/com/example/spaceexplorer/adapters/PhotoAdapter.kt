package com.example.spaceexplorer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.R
import com.example.spaceexplorer.databinding.RoverListItemBinding
import com.example.spaceexplorer.model.MarsRoverPhoto

class PhotoAdapter constructor(
    appExecutors: AppExecutors,
    private val dataBindingComponent: DataBindingComponent,
    private val listener: (MarsRoverPhoto) -> Unit
) : DataBoundListAdapter<MarsRoverPhoto, RoverListItemBinding>(appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<MarsRoverPhoto>() {
        override fun areItemsTheSame(oldItem: MarsRoverPhoto, newItem: MarsRoverPhoto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MarsRoverPhoto, newItem: MarsRoverPhoto): Boolean {
            return oldItem == newItem
        }
    }) {

    override fun createBinding(parent: ViewGroup): RoverListItemBinding {
        val binding = DataBindingUtil
            .inflate<RoverListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.rover_list_item,
                parent,
                false,
                dataBindingComponent
            )

        binding.root.setOnClickListener {
            binding.photo?.let {
                listener.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: RoverListItemBinding, item: MarsRoverPhoto) {
        binding.photo = item
    }

}

