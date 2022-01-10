package com.example.spaceexplorer.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.R
import com.example.spaceexplorer.databinding.FavouriteListItemBinding
import com.example.spaceexplorer.model.Favourite
import kotlinx.android.synthetic.main.favourite_list_item.view.*

class FavouritesAdapter constructor(
    appExecutors: AppExecutors,
    private val dataBindingComponent: DataBindingComponent,
    private val listener: (Favourite) -> Unit
) : DataBoundListAdapter<Favourite, FavouriteListItemBinding>(appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Favourite>() {
        override fun areItemsTheSame(oldItem: Favourite, newItem: Favourite): Boolean {
            return oldItem.photo_id == newItem.photo_id
        }

        override fun areContentsTheSame(oldItem: Favourite, newItem: Favourite): Boolean {
            return oldItem == newItem
        }
    }) {

    override fun createBinding(parent: ViewGroup): FavouriteListItemBinding {
        val binding = DataBindingUtil
            .inflate<FavouriteListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.favourite_list_item,
                parent,
                false,
                dataBindingComponent
            )

        binding.root.remove_icon.setOnClickListener {
            binding.photo?.let {
                listener.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: FavouriteListItemBinding, item: Favourite) {
        binding.photo = item
    }

}

