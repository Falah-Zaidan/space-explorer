package com.example.spaceexplorer.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.R
import com.example.spaceexplorer.adapters.FavouritesAdapter
import com.example.spaceexplorer.binding.FragmentDataBindingComponent
import com.example.spaceexplorer.cache.model.APOD
import com.example.spaceexplorer.databinding.FragmentFavouriteBinding
import com.example.spaceexplorer.di.util.Injectable
import com.example.spaceexplorer.model.Favourite
import com.example.spaceexplorer.model.MarsRoverPhoto
import com.example.spaceexplorer.ui.common.RetryCallback
import com.example.spaceexplorer.util.Constants
import com.example.spaceexplorer.util.autoCleared
import com.example.spaceexplorer.viewmodels.FavouriteViewModel
import kotlinx.android.synthetic.main.favourite_toolbar.*
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import javax.inject.Inject

class FavouriteFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    val favouriteViewModel: FavouriteViewModel by viewModels {
        viewModelProviderFactory
    }

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentFavouriteBinding>()

    var mAdapter by autoCleared<FavouritesAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentFavouriteBinding>(
            inflater,
            R.layout.fragment_favourite,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initDataBindingLayout()
        setUpList()
        observeLiveData()

    }

    private fun initDataBindingLayout() {
        binding.favouritePhotos = favouriteViewModel.getFavourites()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.callback = object : RetryCallback {
            override fun retry() {
            }
        }
    }

    private fun observeLiveData() {
        favouriteViewModel.getFavourites().observe(viewLifecycleOwner, Observer {
            mAdapter.submitList(it.data)
        })

        val headerLayout = navigation_view_fragment_favourite.getHeaderView(0)

        val requestOptions = RequestOptions
            .placeholderOf(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        val username = Constants.getCurrentLoggedInUsername()
        val profilePicture = Constants.getCurrentLoggedinProfilePicture()

        if (headerLayout != null) {
            headerLayout.user_name.text = username

            Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(profilePicture)
                .into(headerLayout.profile_picture)
        }
    }

    private fun setUpList() {
        mAdapter = FavouritesAdapter(
            appExecutors,
            dataBindingComponent,
            remove_item_listener =
            { favouritePhoto: Favourite ->
                favouriteViewModel.deleteFavourite(favouritePhoto.photo_id)

                //insert the new MarsRoverPhoto through the favouriteViewModel (with the 'favourite' flag set to false)
                //this should overwrite the older (obsolete) one
                if (!favouritePhoto.rover_name.equals("abc")) {
                    val newMarsRoverPhoto = MarsRoverPhoto(
                        favouritePhoto.photo_id,
                        favouritePhoto.photo_url,
                        favouritePhoto.earth_date,
                        MarsRoverPhoto.Rover(
                            favouritePhoto.rover_name
                        ),
                        MarsRoverPhoto.Camera(
                            favouritePhoto.camera_name
                        )
                    )
                    newMarsRoverPhoto.favourite = false

                    favouriteViewModel.insertMarsRoverPhoto(newMarsRoverPhoto)
                } else {
                    val newAPODPhoto = APOD(
                        favouritePhoto.photo_id,
                        favouritePhoto.earth_date,
                        favouritePhoto.explanation,
                        favouritePhoto.photo_url
                    )

                    newAPODPhoto.favourite = false

                    favouriteViewModel.insertAPOD(
                        newAPODPhoto
                    )
                }
            },
            image_clicked_listener =
            { favouritePhoto: Favourite ->
                findNavController().navigate(

                    if (favouritePhoto.rover_name == "abc") {
                        FavouriteFragmentDirections.actionFavouriteFragmentToApodFragment(
                            favouritePhoto.earth_date
                        )
                    } else {
                        FavouriteFragmentDirections.actionFavouriteFragmentToDetailFragment(
                            favouritePhoto.photo_id, "favourite"
                        )
                    }
                    //Favourite -> DetailFragment

                    //Favourite -> APODFragment
//                    FavouriteFragmentDirections.actionFavouriteFragmentToApodFragment(
//                        favouritePhoto.earth_date
//                    )
                )
                //do something with the id of the favouritePhoto that the user has clicked
                //Navigate to the particular APODFragment or MarsRoverDetail Fragment with the arguments provided
            })

        binding.favouriteList.adapter = mAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        favourite_top_app_bar.setNavigationOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        navigation_view_fragment_favourite.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            menuItem.isChecked = true
            drawerLayout.closeDrawer(Gravity.LEFT)
            if (menuItem.itemId == R.id.logout) {
                findNavController().navigate(
                    FavouriteFragmentDirections.actionFavouriteFragmentToLoginFragment()
                )
            }
            true
        }
    }

}