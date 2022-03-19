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
    private val remove_item_listener: (Favourite) -> Unit,
    private val image_clicked_listener: (Favourite) -> Unit
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

        //click listener to remove photos
        //click listener is set on the remove_icon (and not the whole view)
        binding.root.remove_icon.setOnClickListener {
            binding.photo?.let {
                remove_item_listener.invoke(it)
            }
        }

        //click listener to view the photos/be redirected to the post
        //the click listener is set on the image (and not the whole view i.e. 'root')*
        binding.root.item_image.setOnClickListener{
            binding.photo?.let {
                image_clicked_listener.invoke(it)
            }
        }

        return binding
    }

    override fun bind(binding: FavouriteListItemBinding, item: Favourite) {
        binding.photo = item
    }

}

