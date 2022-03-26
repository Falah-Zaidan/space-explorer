package com.example.spaceexplorer.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.spaceexplorer.R
import com.example.spaceexplorer.binding.FragmentDataBindingComponent
import com.example.spaceexplorer.databinding.FragmentRoverDetailBinding
import com.example.spaceexplorer.di.util.Injectable
import com.example.spaceexplorer.model.MarsRoverPhoto
import com.example.spaceexplorer.ui.common.ClickFavourite
import com.example.spaceexplorer.ui.common.RetryCallback
import com.example.spaceexplorer.util.Constants
import com.example.spaceexplorer.util.autoCleared
import com.example.spaceexplorer.viewmodels.ListViewModel
import kotlinx.android.synthetic.main.rover_detail_toolbar.*
import javax.inject.Inject

class RoverImageDetailFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    val listViewModel: ListViewModel by viewModels {
        viewModelProviderFactory
    }

    var databindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentRoverDetailBinding>()

    private val args: RoverImageDetailFragmentArgs by navArgs()

    var marsRoverPhotoId: Long? = -1

    var currentMarsRoverPhoto: MarsRoverPhoto? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentRoverDetailBinding>(
            inflater,
            R.layout.fragment_rover_detail,
            container,
            false,
            databindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initDataBindingLayout()
        initialiseTopAppBar()
        initClickListeners()
        observeLiveData()

    }

    private fun initDataBindingLayout() {
        val query_key = args.itemKey
        binding.lifecycleOwner = viewLifecycleOwner
        binding.photoLiveData = listViewModel.getPhoto(query_key)

        binding.retryCallback = object : RetryCallback {
            override fun retry() {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBackToCorrectFragment()
            }
        })
    }


    private fun initClickListeners() {
        binding.favouriteClickListener = object : ClickFavourite {
            override fun clickFavouriteRover() {
                binding.photo?.let {
                    it.favourite = true
                    listViewModel.insertMarsRoverPhoto(it)
                    //use the ViewModel to insert a FavouritePhoto
                    listViewModel.setFavouriteMarsRoverPhoto(it) // passing in the id
                }
            }

            override fun clickUnfavouriteRover() {
                binding.photo?.let {
                    it.favourite = false
                    listViewModel.insertMarsRoverPhoto(it)
                    //use the ViewModel to remove a FavouritePhoto
                    listViewModel.removeFavouriteMarsRoverPhoto(it)
                }
            }

            override fun clickFavouriteAPOD() {
                throw UnsupportedOperationException()
            }

            override fun clickUnfavouriteAPOD() {
                throw UnsupportedOperationException()
            }

        }
    }

    private fun observeLiveData() {
        val query_key = args.itemKey
        listViewModel.getPhoto(query_key).observe(viewLifecycleOwner, Observer {

            binding.photo = it.data
            currentMarsRoverPhoto = it.data
            marsRoverPhotoId = it.data!!.id.toLong()
        })
    }

    private fun initialiseTopAppBar() {
        rover_detail_top_app_bar.setNavigationOnClickListener {
            navigateBackToCorrectFragment()
        }

        rover_detail_top_app_bar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save_img -> {
                    downloadImage(currentMarsRoverPhoto!!)
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateBackToCorrectFragment() {
        when (args.roverFragment) {
            "Spirit" -> {
                findNavController().navigate(
                    RoverImageDetailFragmentDirections.actionDetailFragmentToSpiritRoverFragment()
                )
            }
            "Opportunity" -> {
                findNavController().navigate(
                    RoverImageDetailFragmentDirections.actionDetailFragmentToOpportunityRoverFragment()
                )
            }
            "Curiosity" -> {
                findNavController().navigate(
                    RoverImageDetailFragmentDirections.actionDetailFragmentToCuriosityRoverFragment()
                )
            }
        }
    }

    private fun downloadImage(currentMarsRoverPhoto: MarsRoverPhoto) {

        val imageURL = currentMarsRoverPhoto.image_href
        val imageFileName = currentMarsRoverPhoto.id.toString()

        if (!verifyPermissions()) {
            return
        }

        Glide.with(this)
            .load(imageURL)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    val bitmap = (resource as BitmapDrawable).bitmap
                    Toast.makeText(
                        context!!,
                        getString(R.string.toast_saving_image),
                        Toast.LENGTH_SHORT
                    ).show()
                    saveImage(bitmap, imageFileName)//, dir, fileName)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    Toast.makeText(
                        context!!,
                        getString(R.string.toast_saving_image_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun saveImage(
        image: Bitmap,
        imageFileName: String
    ) {

        MediaStore.Images.Media.insertImage(
            context!!.contentResolver,
            image,
            imageFileName,
            "Image of $imageFileName"
        )

        Toast.makeText(context!!, getString(R.string.toast_image_saved), Toast.LENGTH_SHORT).show()
    }

    fun verifyPermissions(): Boolean {

        val permissionExternalMemory =
            ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            val STORAGE_PERMISSIONS = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            // If permission not granted then ask for permission real time.
            requestPermissions(STORAGE_PERMISSIONS, Constants.storagePermissionRequestCode)
            return false
        }
        return true
    }

}