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
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.R
import com.example.spaceexplorer.adapters.EditorsPickAdapter
import com.example.spaceexplorer.binding.FragmentDataBindingComponent
import com.example.spaceexplorer.databinding.FragmentEditorsPicksBinding
import com.example.spaceexplorer.di.util.Injectable
import com.example.spaceexplorer.ui.common.RetryCallback
import com.example.spaceexplorer.util.autoCleared
import com.example.spaceexplorer.viewmodels.EditorsPickViewModel
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.selection_toolbar.*
import javax.inject.Inject

class EditorsPicksFragment : Fragment(), Injectable {

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    var binding by autoCleared<FragmentEditorsPicksBinding>()

    lateinit var mAdapter: EditorsPickAdapter

    val editorsPickViewModel: EditorsPickViewModel by viewModels {
        viewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentEditorsPicksBinding>(
            inflater,
            R.layout.fragment_editors_picks,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataBindingLayout()
        initAdapter()
        observeLiveData()
    }

    private fun observeLiveData() {

        editorsPickViewModel.editorsPickPhotosLiveData.observe(viewLifecycleOwner, Observer {
            mAdapter.submitList(it.data)
        })

    }

    private fun initDataBindingLayout() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.editorsPickPhotos = editorsPickViewModel.editorsPickPhotosLiveData

        binding.retryCallback = object : RetryCallback {
            override fun retry() {
                editorsPickViewModel.refresh()
            }
        }

    }

    private fun initAdapter() {
        mAdapter = EditorsPickAdapter(
            appExecutors,
            dataBindingComponent
        ) {
            findNavController().navigate(
                EditorsPicksFragmentDirections.actionEditorsPicksFragmentToSelectionDetailFragment(
                    it.date,
                    it.photoId
                )
            )
        }

        binding.editorsPickList.adapter = mAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        selection_top_app_bar.setNavigationOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }
    }

}