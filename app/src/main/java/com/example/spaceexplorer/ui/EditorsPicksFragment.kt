package com.example.spaceexplorer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.R
import com.example.spaceexplorer.adapters.EditorsPickAdapter
import com.example.spaceexplorer.binding.FragmentDataBindingComponent
import com.example.spaceexplorer.databinding.FragmentEditorsPicksBinding
import com.example.spaceexplorer.di.util.Injectable
import com.example.spaceexplorer.util.autoCleared
import com.example.spaceexplorer.viewmodels.EditorsPickViewModel
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

        initAdapter()
        observeLiveData()
    }

    private fun observeLiveData() {

        editorsPickViewModel.getEditorsPicks()
        editorsPickViewModel._editorsPickLiveData.observe(viewLifecycleOwner, Observer {
            mAdapter.submitList(it)
        })
    }

    private fun initAdapter() {
        mAdapter = EditorsPickAdapter(
            appExecutors,
            dataBindingComponent
        )

//        scrollListener = object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                val lastPosition = layoutManager.findLastVisibleItemPosition()
//                if (lastPosition == mAdapter.itemCount - 1) {
//                    listViewModel.loadNextPage("Opportunity")
//                }
//            }
//        }

//        binding.recyclerviewFeed.addOnScrollListener(scrollListener)

//        listViewModel.getLoadMoreStatus().observe(viewLifecycleOwner, Observer { loadingMore ->
//            if (loadingMore == null) {
//                binding.loadingMore = false
//            } else {
//                binding.loadingMore = loadingMore.isRunning
//                val error = loadingMore.errorMessageIfNotHandled
//                if (error != null) {
//                    Snackbar.make(binding.loadMoreBar, error, Snackbar.LENGTH_LONG).show()
//                }
//            }
//        })

        binding.editorsPickList.adapter = mAdapter
    }

}