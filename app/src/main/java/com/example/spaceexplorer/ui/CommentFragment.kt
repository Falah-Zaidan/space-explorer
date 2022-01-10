package com.example.spaceexplorer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.R
import com.example.spaceexplorer.adapters.CommentAdapter
import com.example.spaceexplorer.binding.FragmentDataBindingComponent
import com.example.spaceexplorer.databinding.FragmentCommentsBinding
import com.example.spaceexplorer.di.util.Injectable
import com.example.spaceexplorer.util.Constants
import com.example.spaceexplorer.util.autoCleared
import com.example.spaceexplorer.viewmodels.ListViewModel
import kotlinx.android.synthetic.main.comments_toolbar.*
import javax.inject.Inject

class CommentFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    val listViewModel: ListViewModel by viewModels {
        viewModelProviderFactory
    }

    var databindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var commentAdapter by autoCleared<CommentAdapter>()

    var binding by autoCleared<FragmentCommentsBinding>()

    val args: CommentFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentCommentsBinding>(
            inflater,
            R.layout.fragment_comments,
            container,
            false,
            databindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataBindingLayout()
        setUpList()
        initialiseTopAppBar()

        observeLiveData()

    }

    private fun initDataBindingLayout() {
        binding.apodWithComments = listViewModel.getAPODWithComments(args.apodId)
        binding.lifecycleOwner = viewLifecycleOwner

    }

    private fun setUpList() {

        commentAdapter = CommentAdapter(
            appExecutors,
            databindingComponent,
            Constants.getCurrentLoggedInUsername()!!
        ) {
            listViewModel.deleteComment(it)
        }

        val layoutManager = LinearLayoutManager(context)
        binding.commentList.layoutManager = layoutManager

        binding.commentList.adapter = commentAdapter
    }

    private fun observeLiveData() {

        listViewModel.getAPODWithComments(args.apodId)
            .observe(viewLifecycleOwner, Observer {
                if (it.data != null) {
                    commentAdapter.submitList(it.data.comments)
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val marsRoverArg = args.marsRoverPhotoId
                val apodArg = args.apodId
                val apodDateArg = args.apodDate

                if (marsRoverArg.toInt() != -1 && apodArg.toInt() == -1) {
                    findNavController().navigate(
                        CommentFragmentDirections.actionCommentFragmentToDetailFragment(marsRoverArg)
                    )
                } else if (marsRoverArg.toInt() == -1 && apodArg.toInt() != -1) {
                    findNavController().navigate(
                        CommentFragmentDirections.actionCommentFragmentToApodFragment(apodDateArg)
                    )
                }
            }
        })
    }

    private fun initialiseTopAppBar() {

        val marsRoverArg = args.marsRoverPhotoId
        val apodArg = args.apodId
        val apodDateArg = args.apodDate

        //when the user presses back on the Comment Screen - how to decide which Fragment he goes back to:
        rover_list_top_app_bar.setNavigationOnClickListener {
            if (marsRoverArg.toInt() != -1 && apodArg.toInt() == -1) {
                //navigate back to MarsRoverDetailFragment, passing in the marsRoverArg
                findNavController().navigate(
                    CommentFragmentDirections.actionCommentFragmentToDetailFragment(marsRoverArg)
                )
            } else if (marsRoverArg.toInt() == -1 && apodArg.toInt() != -1) {
                //navigate back to ApodFragment
                findNavController().navigate(
                    CommentFragmentDirections.actionCommentFragmentToApodFragment(apodDateArg)
                )
            }

        }

        rover_list_top_app_bar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_comment -> {
                    // Handle favorite icon press
                    findNavController().navigate(
                        CommentFragmentDirections.actionCommentFragmentToCreateCommentFragment(
                            args.apodId,
                            args.marsRoverPhotoId,
                            args.apodDate
                        )
                    )
                    true
                }
                else -> false
            }
        }
    }

}