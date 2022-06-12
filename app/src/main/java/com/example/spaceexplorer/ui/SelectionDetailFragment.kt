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
import com.example.spaceexplorer.databinding.FragmentSelectionDetailBinding
import com.example.spaceexplorer.di.util.Injectable
import com.example.spaceexplorer.model.EditorsPickPhoto
import com.example.spaceexplorer.ui.common.ClickFavourite
import com.example.spaceexplorer.util.autoCleared
import com.example.spaceexplorer.viewmodels.EditorsPickViewModel
import kotlinx.android.synthetic.main.selection_detail_toolbar.*
import javax.inject.Inject

class SelectionDetailFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    var binding by autoCleared<FragmentSelectionDetailBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    val editorsPickViewModel: EditorsPickViewModel by viewModels {
        viewModelProviderFactory
    }

    private val args: SelectionDetailFragmentArgs by navArgs()

    //keep a reference to the editorsPickPhoto so it can be saved
    private lateinit var editorsPickPhoto: EditorsPickPhoto

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate<FragmentSelectionDetailBinding>(
            inflater,
            R.layout.fragment_selection_detail,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeLiveData()
        initViews()
        initClickListeners()
        initialiseTopAppBar()
    }

    private fun initClickListeners() {

//        binding.commentClickListener = object : ClickCommentListener {
//            override fun clickedCommentScreen() {
//                findNavController().navigate(
//                    ApodFragmentDirections.actionApodFragmentToCommentFragment(
//                        DateUtil.convertFromDate(currentAPOD!!.date), // this is the apodDate value converted into a long value - it's going to be unique for every APOD
//                        -1,
//                        currentAPOD!!.date,
//                        -1
//                    )
//                )
//            }
//        }

        binding.favouriteClickListener = object : ClickFavourite {
            override fun clickFavouriteRover() {
                throw UnsupportedOperationException()
            }

            override fun clickUnfavouriteRover() {
                throw UnsupportedOperationException()
            }

            override fun clickFavouriteAPOD() {
                throw UnsupportedOperationException()
            }

            override fun clickUnfavouriteAPOD() {
                throw UnsupportedOperationException()
            }

            override fun clickFavouriteEditorsPickPhoto() {
                binding.editorsPickPhoto?.let {
                    it.favourite = true
                    //insert into DB
                    editorsPickViewModel.insertEditorsPickPhoto(it)

                    //refresh ViewModel to get fresh LiveData (so that the UI updates)

                    //update the current UI
                    editorsPickViewModel.refreshEditorPickPhotoSingle()

                    //save the favourite Photo
                    editorsPickViewModel.setFavouriteEditorsPickPhoto(it)
                }
            }

            override fun clickUnfavouriteEditorsPickPhoto() {
                binding.editorsPickPhoto?.let {
                    it.favourite = false
                    //insert into DB
                    editorsPickViewModel.insertEditorsPickPhoto(it)

                    //refresh ViewModel to get fresh LiveData (so that the UI updates)

                    //update the current UI
                    editorsPickViewModel.refreshEditorPickPhotoSingle()

                    //save the favourite Photo
                    editorsPickViewModel.removeEditorsPickPhotoFavourite(it)
                }
            }
        }
    }

    private fun initialiseTopAppBar() {

        val toolbar = selection_detail_top_app_bar
        toolbar.setTitle("Rings of Relativity")

        selection_detail_top_app_bar.setNavigationOnClickListener {
            //navigate back to EditorsPickFragment
            findNavController().navigate(
                SelectionDetailFragmentDirections.actionSelectionDetailFragmentToEditorsPicksFragment()
            )
        }

        selection_detail_top_app_bar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save_img -> {
                    Toast.makeText(
                        requireContext(),
                        "Save button has been clicked",
                        Toast.LENGTH_LONG
                    ).show()
                    downloadImage(editorsPickPhoto)
                    true
                }
                else -> false
            }
        }
    }

    private fun downloadImage(editorsPickPhoto: EditorsPickPhoto) {

        val imageURL = editorsPickPhoto.url
        val imageFileName = editorsPickPhoto.photoId.toString()

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
                    saveImage(bitmap, imageFileName)
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

    fun verifyPermissions(): Boolean {

        val permissionExternalMemory =
            ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

        ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            val STORAGE_PERMISSIONS =
                arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            // If permission not granted then ask for permission real time.
            requestPermissions(STORAGE_PERMISSIONS, 1)
            return false
        }
        return true
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

    private fun observeLiveData() {
//        editorsPickViewModel.editorsPickPhotosLiveData.observe(viewLifecycleOwner, Observer {
//            //TODO: nullability
//            val mEditorPickPhoto = getListItem(it.data, args.editorPickPhotoId)
//            if (mEditorPickPhoto != null) {
//                binding.editorsPickPhoto = mEditorPickPhoto
//                this.editorsPickPhoto = mEditorPickPhoto
//                selection_detail_top_app_bar.title = mEditorPickPhoto.name
//            }
//        })

//        editorsPickViewModel.getEditorPickPhoto(args.date).observe(viewLifecycleOwner, Observer {
//            val editorPickPhoto = it.data
//            if (editorPickPhoto != null) {
//                binding.editorsPickPhoto = editorPickPhoto
//                this.editorsPickPhoto = editorPickPhoto
//                selection_detail_top_app_bar.title = editorPickPhoto.name
//            }
//        })

        editorsPickViewModel.getEditorPickPhoto(args.date)
        editorsPickViewModel.editorPickPhotoLiveData.observe(viewLifecycleOwner, Observer {
            val editorPickPhoto = it.data
            if (editorPickPhoto != null) {
                binding.editorsPickPhoto = editorPickPhoto
                this.editorsPickPhoto = editorPickPhoto
                selection_detail_top_app_bar.title = editorPickPhoto.name
            }
        })
    }

    private fun getListItem(list: List<EditorsPickPhoto>?, id: String): EditorsPickPhoto? {
        list?.forEach { editorsPickPhoto ->
            if (editorsPickPhoto.photoId.equals(id)) {
                return editorsPickPhoto
            }
        }

        return null
    }

    private fun initViews() {
        binding.commentBtn.setOnClickListener {
            findNavController().navigate(
                SelectionDetailFragmentDirections.actionSelectionDetailFragmentToCommentFragment(
                    -1, -1, "", -1
                )
            )
        }
    }


}