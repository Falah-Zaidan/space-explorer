package com.example.spaceexplorer.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.util.Pair
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.R
import com.example.spaceexplorer.adapters.PhotoAdapter
import com.example.spaceexplorer.binding.FragmentDataBindingComponent
import com.example.spaceexplorer.databinding.FragmentCuriosityRoverBinding
import com.example.spaceexplorer.di.util.Injectable
import com.example.spaceexplorer.ui.common.ClickButtonListener
import com.example.spaceexplorer.ui.common.ClickRoverListener
import com.example.spaceexplorer.ui.common.RetryCallback
import com.example.spaceexplorer.util.Constants
import com.example.spaceexplorer.util.DateUtil
import com.example.spaceexplorer.util.autoCleared
import com.example.spaceexplorer.viewmodels.ListViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_rover_list.drawerLayout
import kotlinx.android.synthetic.main.fragment_spirit_rover.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.rover_list_toolbar.*
import javax.inject.Inject

class CuriosityRoverFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    val listViewModel: ListViewModel by viewModels {
        viewModelProviderFactory
    }

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    lateinit var mAdapter: PhotoAdapter

    var binding by autoCleared<FragmentCuriosityRoverBinding>()

    var datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText(Constants.date_picker_text)
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build()

    var scrollListener by autoCleared<RecyclerView.OnScrollListener>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentCuriosityRoverBinding>(
            inflater,
            R.layout.fragment_curiosity_rover,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initDataBindingLayout()
        initClickListeners()

        setupList()
        initialiseTopAppBar()
        initDatePicker()
        observeLiveData()
        setupDrawerProfilePicture()

    }

    private fun initClickListeners() {
        binding.roverSelectionLayout.clickListener = object : ClickRoverListener {
            override fun clickCuriosity() {
                //navigate to CuriosityFragment
                //do nothing
            }

            override fun clickOpportunity() {
                //navigate to OpportunityFragment
                if (findNavController().currentDestination?.id == R.id.curiosityRoverFragment) {
                    findNavController().navigate(
                        CuriosityRoverFragmentDirections.actionCuriosityRoverFragmentToOpportunityRoverFragment()
                    )
                }
            }

            override fun clickSpirit() {
                //navigate to SpiritFragment
                if (findNavController().currentDestination?.id == R.id.curiosityRoverFragment) {
                    findNavController().navigate(
                        CuriosityRoverFragmentDirections.actionCuriosityRoverFragmentToSpiritRoverFragment()
                    )
                }
            }
        }

        binding.clickListener = object : ClickButtonListener {

            override fun clickedDatePicker() {
                displayCalendar()
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rover_list_top_app_bar.setNavigationOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        navigation_view_fragment_rover_list_photos.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            menuItem.isChecked = true
            drawerLayout.closeDrawer(Gravity.LEFT)
            if (menuItem.itemId == R.id.logout) {
                findNavController().navigate(
                    CuriosityRoverFragmentDirections.actionCuriosityRoverFragmentToLoginFragment()
                )
            }
            true
        }
    }

    private fun initDataBindingLayout() {
        binding.photos = listViewModel.curiosityPhotos
        binding.lifecycleOwner = viewLifecycleOwner
        binding.callback = object : RetryCallback {
            override fun retry() {
                listViewModel.retry()
            }
        }
    }

    private fun initDatePicker() {

        val spiritDateBounds = Pair.create(
            DateUtil.convertFromDate(Constants.spirit_start_bound),
            DateUtil.convertFromDate(Constants.spirit_end_bound)
        )

        datePicker.addOnPositiveButtonClickListener() {
            if (it < spiritDateBounds.first!! || it > spiritDateBounds.second!!) {
                Toast.makeText(
                    context,
                    getString(R.string.toast_invalid),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val selectedDate = DateUtil.convertToDate(it)
                listViewModel.setSpiritDate(selectedDate)
            }
        }
    }

    private fun observeLiveData() {
        listViewModel.curiosityPhotos.observe(viewLifecycleOwner, Observer {
            mAdapter.submitList(it.data)
        })
    }

    private fun setupDrawerProfilePicture() {
        val headerLayout = navigation_view_fragment_rover_list_photos.getHeaderView(0)

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

    private fun initialiseTopAppBar() {
        rover_list_top_app_bar.setNavigationOnClickListener {

        }

        rover_list_top_app_bar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.calendar -> {
                    displayCalendar()
                    true
                }
                else -> false
            }
        }
    }

    fun displayCalendar() {
        datePicker.show(parentFragmentManager, getString(R.string.date_picker_show))
    }

    private fun setupList() {

        mAdapter = PhotoAdapter(
            appExecutors,
            dataBindingComponent
        ) { post ->
            findNavController().navigate(
                CuriosityRoverFragmentDirections.actionCuriosityRoverFragmentToDetailFragment(post.id, "Curiosity")
            )
        }
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == mAdapter.itemCount - 1) {
                    listViewModel.loadNextPage("Curiosity")
                }
            }
        }

        binding.recyclerviewFeed.addOnScrollListener(scrollListener)

        listViewModel.getLoadMoreStatus().observe(viewLifecycleOwner, Observer { loadingMore ->
            if (loadingMore == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = loadingMore.isRunning
                val error = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
                }
            }
        })

        binding.recyclerviewFeed.adapter = mAdapter
    }

}