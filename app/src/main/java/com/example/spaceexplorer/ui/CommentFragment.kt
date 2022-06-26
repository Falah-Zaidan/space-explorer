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
import com.example.spaceexplorer.viewmodels.CommentViewModel
import kotlinx.android.synthetic.main.comments_toolbar.*
import javax.inject.Inject

class CommentFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    val commentViewModel: CommentViewModel by viewModels {
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

        //TODO: sort this out properly
//        binding.lifecycleOwner = viewLifecycleOwner

//        if (args.callingFragmentType.equals("APODFragment")) {
//            binding.apodWithComments = commentViewModel.getAPODWithComments(args.apodId)
//        } else if (args.callingFragmentType.equals("SelectionDetailFragment")) {
//            binding.apodWithComments = commentViewModel.getAPODWithComments(args.apodId)
//        }

    }

    private fun setUpList() {

        commentAdapter = CommentAdapter(
            appExecutors,
            databindingComponent,
            Constants.getCurrentLoggedInUsername()!!
        ) {
            //TODO: set-up delete comment functionality properly
//            listViewModel.deleteComment(it)
//            commentViewModel.deleteComment(it)
        }

        val layoutManager = LinearLayoutManager(context)
        binding.commentList.layoutManager = layoutManager

        binding.commentList.adapter = commentAdapter
    }

    private fun observeLiveData() {

        //observe the right LiveData based on the SelectFragmentType
        if (args.callingFragmentType.equals("APODFragment")) {
            commentViewModel.getAPODWithComments(args.apodId)
                .observe(viewLifecycleOwner, Observer {
                    if (it.data != null) {
                        commentAdapter.submitList(it.data.comments)
                    }
                })
        } else if (args.callingFragmentType.equals("SelectionDetailFragment") && !args.editorsPickPhotoId.equals(
                ""
            )
        ) {
            commentViewModel.getSelectionDetailWithComments(args.editorsPickPhotoId)
                .observe(viewLifecycleOwner, Observer {
                    if (it.data != null) {
                        commentAdapter.submitList(it.data.comments)
                    }
                })
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val marsRoverArg = args.marsRoverPhotoId
                val apodArg = args.apodId
                val apodDateArg = args.apodDate
                val editorsPickArg = args.editorsPickPhotoId

                if (marsRoverArg.toInt() != -1 && apodArg.toInt() == -1 && editorsPickArg.equals("")) {
//                    findNavController().navigate(
//                        CommentFragmentDirections.actionCommentFragmentToDetailFragment(marsRoverArg)
//                    )
                } else if (marsRoverArg.toInt() == -1 && apodArg.toInt() != -1 && editorsPickArg.equals(
                        ""
                    )
                ) {
                    findNavController().navigate(
                        CommentFragmentDirections.actionCommentFragmentToApodFragment(apodDateArg)
                    )
                } else if (marsRoverArg.toInt() == -1 && apodArg.toInt() == -1 && editorsPickArg.equals(
                        ""
                    )
                ) {
                    findNavController().navigate(
                        CommentFragmentDirections.actionCommentFragmentToEditorsPicksFragment()
                    )
                }
            }
        })
    }

    private fun initialiseTopAppBar() {

        val marsRoverArg = args.marsRoverPhotoId
        val apodArg = args.apodId
        val apodDateArg = args.apodDate
        val editorsPickArg = args.editorsPickPhotoId

        //when the user presses back on the Comment Screen - how to decide which Fragment he goes back to:
        rover_list_top_app_bar.setNavigationOnClickListener {
            if (marsRoverArg.toInt() != -1 && apodArg.toInt() == -1) {
                //navigate back to MarsRoverDetailFragment, passing in the marsRoverArg
//                findNavController().navigate(
//                    CommentFragmentDirections.actionCommentFragmentToDetailFragment(marsRoverArg)
//                )
            } else if (marsRoverArg.toInt() == -1 && apodArg.toInt() != -1) {
                //navigate back to ApodFragment
                findNavController().navigate(
                    CommentFragmentDirections.actionCommentFragmentToApodFragment(apodDateArg)
                )
            } else if (marsRoverArg.toInt() == -1 && apodArg.toInt() == -1 && editorsPickArg.toInt() == -1) {
                findNavController().navigate(
                    CommentFragmentDirections.actionCommentFragmentToEditorsPicksFragment()
                )
            }
        }

        rover_list_top_app_bar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_comment -> {
                    // Handle favorite icon press
                    findNavController().navigate(
                        CommentFragmentDirections.actionCommentFragmentToCreateCommentFragment(
                            apodId = args.apodId,
                            marsRoverPhotoId = args.marsRoverPhotoId,
                            apodDate = args.apodDate,
                            callingFragmentType = args.callingFragmentType,
                            editorsPickPhotoId = args.editorsPickPhotoId
                        )
                    )
                    true
                }
                else -> false
            }
        }
    }

}