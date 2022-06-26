package com.example.spaceexplorer.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.spaceexplorer.R
import com.example.spaceexplorer.binding.FragmentDataBindingComponent
import com.example.spaceexplorer.cache.model.APOD
import com.example.spaceexplorer.cache.model.CurrentAPOD
import com.example.spaceexplorer.databinding.FragmentApodBinding
import com.example.spaceexplorer.di.util.Injectable
import com.example.spaceexplorer.ui.common.*
import com.example.spaceexplorer.util.Constants
import com.example.spaceexplorer.util.DateUtil.Companion.convertFromDate
import com.example.spaceexplorer.util.DateUtil.Companion.convertToDate
import com.example.spaceexplorer.util.autoCleared
import com.example.spaceexplorer.viewmodels.ListViewModel
import com.example.spaceexplorer.viewmodels.LoginViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.apod_toolbar.*
import kotlinx.android.synthetic.main.fragment_apod.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.util.*
import javax.inject.Inject


class ApodFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    val listViewModel: ListViewModel by viewModels {
        viewModelProviderFactory
    }

    val loginViewModel: LoginViewModel by viewModels {
        viewModelProviderFactory
    }

    var databindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<FragmentApodBinding>()
    private val args: ApodFragmentArgs by navArgs()

    //NOTE: can't really call string resouces from a static context - you always need context - otherwise it will crash app...

    var datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText(Constants.date_picker_text)
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build()

    var currentAPOD: APOD? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentApodBinding>(
            inflater,
            R.layout.fragment_apod,
            container,
            false,
            databindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initDataBindingLayout()
        initClickListeners()
        initialiseTopAppBar()
        initDatePicker()

        observeAPODLiveData()
        setupDrawerProfilePicture()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //do nothing (you don't want to go back to other main screens)
            }
        })
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        activity.bottom_navigation.visibility = View.VISIBLE

    }

    private fun initDataBindingLayout() {
        binding.lifecycleOwner = viewLifecycleOwner

        //2 cases:
        //User first enters the app - should show the latest date
        //Can be done in the ViewModel (setting a static date)
        //User navigates from 'Favourite' item - should show the picture from that particular date
        //Can be done alongside the above if we check for navArgs - if they are empty then we go for the default (ViewModel) value
        //We also need to save that value to the Database (since it is now the currentAPOD)
        val query_key = args.apodDate
        if (query_key != "" && query_key != "void") {
            listViewModel.setApodDate(query_key)
            binding.apodPhotoLiveData = listViewModel.apod
        } else {
            binding.apodPhotoLiveData = listViewModel.apod
        }

        binding.retryCallback = object : RetryCallback {
            override fun retry() {
                listViewModel.getCurrentAPOD()
            }
        }

    }

    private fun observeAPODLiveData() {

        val dateArg = args.apodDate
        Log.d("dateArg", dateArg)

        if (args.apodDate == "" || args.apodDate == "void") {
//            var currentDate = convertToDate(Calendar.getInstance().time.time)
//            listViewModel.setApodDate(currentDate)
            listViewModel.getCurrentAPOD()
            listViewModel.currentAPODLiveData.observe(viewLifecycleOwner, Observer {
                listViewModel.setApodDate(it.data?.date)
            })
        }

        listViewModel.apod.observe(viewLifecycleOwner, Observer {
            //if items are the same, don't update the UI - since there is nothing to update - and will just make the screen flicker
            if (it.data != null) {
                if (!areItemsTheSame(it.data, currentAPOD)) {
                    binding.apodPhoto = it.data
                    setCurrentApod(it.data)
                    listViewModel.setCurrentAPOD(
                        CurrentAPOD(
                            it.data.date
                        )
                    )
                } else {
                    binding.apodPhoto = currentAPOD
                }
            }
        })

    }

    private fun setupDrawerProfilePicture() {
        val headerLayout = navigation_view_fragment_apod.getHeaderView(0)

        val requestOptions = RequestOptions
            .placeholderOf(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        val username = Constants.getCurrentLoggedInUsername()
        val profilePicture = Constants.getCurrentLoggedinProfilePicture()

        if (headerLayout != null) {
            headerLayout.user_name.text = username

            Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(profilePicture).into(headerLayout.profile_picture)
        }
    }

    private fun setCurrentApod(apod: APOD) {
        val favourite = apod.favourite
        val date = apod.date
        val explanation = apod.explanation
        val id = apod.id
        val hdURL = apod.hdURL

        val newApod = APOD(
            id,
            date,
            explanation,
            hdURL
        )
        newApod.favourite = favourite

        currentAPOD = newApod

    }

    private fun initClickListeners() {
        binding.clickListener = object : ClickButtonListener {
            override fun clickedDatePicker() {
                displayCalendar()
            }
        }

        binding.commentClickListener = object : ClickCommentListener {
            override fun clickedCommentScreen() {
                findNavController().navigate(
                    ApodFragmentDirections.actionApodFragmentToCommentFragment(
                        convertFromDate(currentAPOD!!.date), // this is the apodDate value converted into a long value - it's going to be unique for every APOD
                        -1,
                        currentAPOD!!.date,
                        "",
                        "APODFragment"
                    )
                )
            }
        }

        binding.favouriteClickListener = object : ClickFavourite {
            override fun clickFavouriteRover() {
                throw UnsupportedOperationException()
            }

            override fun clickUnfavouriteRover() {
                throw UnsupportedOperationException()
            }

            override fun clickFavouriteAPOD() {
                binding.apodPhoto?.let {
                    it.favourite = true
                    listViewModel.insertAPOD(it)
                    listViewModel.refresh()
                    listViewModel.setFavouriteAPODPhoto(it)
                }
            }

            override fun clickUnfavouriteAPOD() {
                binding.apodPhoto?.let {
                    it.favourite = false
                    listViewModel.insertAPOD(it)
                    listViewModel.refresh()
                    listViewModel.removeFavouriteAPODPhoto(it)
                }
            }

            override fun clickFavouriteEditorsPickPhoto() {
                throw UnsupportedOperationException()
            }

            override fun clickUnfavouriteEditorsPickPhoto() {
                throw UnsupportedOperationException()
            }
        }

        binding.navigateClickListener = object : NavigateClickListener {
            override fun clickForward() {
                listViewModel.nextAPOD()
            }

            override fun clickBackward() {
                listViewModel.previousAPOD()
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        apod_top_app_bar.setNavigationOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        navigation_view_fragment_apod.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            menuItem.isChecked = true
            drawerLayout.closeDrawer(Gravity.LEFT)
            if (menuItem.itemId == R.id.logout) {
                //navigate back to loginFragment
                findNavController().navigate(
                    ApodFragmentDirections.actionApodFragmentToLoginFragment()
                )
            }
            true
        }
    }

    private fun areItemsTheSame(apod: APOD, currentAPODValue: APOD?): Boolean {

        if (currentAPODValue != null) {
            if (apod == currentAPODValue) {
                if (apod.favourite == currentAPODValue.favourite) {
                    return true
                }
                return false
            }
            return false
        }
        return false
    }

    private fun initDatePicker() {

        val currentDateTime = Calendar.getInstance()
        val currentDate = convertToDate(currentDateTime.time.time)

        val apodDateBounds = Pair.create(
            convertFromDate(Constants.apod_start_bound),
            convertFromDate(currentDate)
        )

        datePicker.addOnPositiveButtonClickListener {

            if (it < apodDateBounds.first!! || it > apodDateBounds.second!!) {
                Toast.makeText(context, getString(R.string.toast_invalid), Toast.LENGTH_SHORT)
                    .show()
            } else {
                val selectedDate = convertToDate(it)
                listViewModel.setApodDate(selectedDate)
                listViewModel.setCurrentAPOD(CurrentAPOD(selectedDate))
            }

        }
    }

    private fun initialiseTopAppBar() {
        apod_top_app_bar.setNavigationOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        apod_top_app_bar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.apod_calendar -> {
                    // Handle favorite icon press
                    displayCalendar()
                    true
                }
                R.id.save_img -> {

                    downloadImage(currentAPOD!!)
                    true
                }
                else -> false
            }
        }

    }

    fun displayCalendar() {
        datePicker.show(parentFragmentManager, getString(R.string.date_picker_show))
    }

    private fun downloadImage(apod: APOD) {

        val imageURL = apod.hdURL
        val imageFileName = apod.id.toString()

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

}