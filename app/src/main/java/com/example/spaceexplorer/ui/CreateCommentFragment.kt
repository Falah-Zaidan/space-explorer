package com.example.spaceexplorer.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.R
import com.example.spaceexplorer.binding.FragmentDataBindingComponent
import com.example.spaceexplorer.cache.model.APOD
import com.example.spaceexplorer.cache.model.Comment
import com.example.spaceexplorer.cache.model.User
import com.example.spaceexplorer.databinding.FragmentCreateCommentBinding
import com.example.spaceexplorer.di.util.Injectable
import com.example.spaceexplorer.ui.common.CommentClickListener
import com.example.spaceexplorer.util.autoCleared
import com.example.spaceexplorer.viewmodels.ListViewModel
import com.example.spaceexplorer.viewmodels.LoginViewModel
import javax.inject.Inject
import kotlin.random.Random.Default.nextInt

class CreateCommentFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    val listViewModel: ListViewModel by viewModels {
        viewModelProviderFactory
    }

    val loginViewModel: LoginViewModel by viewModels {
        viewModelProviderFactory
    }

    var databindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<FragmentCreateCommentBinding>()

    val args: CreateCommentFragmentArgs by navArgs()

    var currentAPOD: APOD? = null
    var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentCreateCommentBinding>(
            inflater,
            R.layout.fragment_create_comment,
            container,
            false,
            databindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        initObservers()

        listViewModel.apod.observe(viewLifecycleOwner, Observer {
            if (it.data != null) {
                setCurrentApod(it.data)
            }
        })

        loginViewModel.userLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                setUserData(it)
            }
        })

        binding.commentClickListener = object : CommentClickListener {

            override fun submitComment() {

                val inputText = binding.postdescriptioninput.text.toString()

                if (inputText.length > 300) {
                    Toast.makeText(
                        context,
                        getString(R.string.toast_create_comment_excess_text),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                //save the comment, to Back-end (and then through SSOT, be able to retrieve it from DB when data is fetched)

                val apod = APOD(
                    args.apodId,
                    currentAPOD!!.date,
                    currentAPOD!!.explanation,
                    currentAPOD!!.hdURL
                )

                apod.fetchedFromDjangoService = currentAPOD!!.fetchedFromDjangoService

                //get the current APOD through the ViewModel (this will be the one stored in DB)
                if (user != null) {
                    listViewModel.insertAPODComment(
                        apod,
                        Comment(
                            userCreatorId = nextInt(), //userCreatorId
                            apodId = args.apodId, //apodId
                            marsRoverPhotoId = args.marsRoverPhotoId, //marsRoverPhotoID
                            comment = inputText, //comment
                            dateTime = nextInt().toString(), //datetime
                            likes = nextInt(), //likes
                            author_name = user!!.name,
                            profile_picture = user!!.profilePictureUrl
                        )
                    )
                }

                findNavController().navigate(
                    CreateCommentFragmentDirections.actionCreateCommentFragmentToCommentFragment(
                        args.apodId,
                        args.marsRoverPhotoId,
                        args.apodDate,
                        -1
                    )
                )
            }
        }

        binding.postdescriptioninput.setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                true
            } else {
                false
            }
        }

        binding.postdescriptioninput.setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                true
            } else {
                false
            }

        }

    }

    private fun setUserData(mUser: User) {
        this.user = mUser
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

    fun initObservers() {
        //observe apod LiveData
        listViewModel.apod.observe(viewLifecycleOwner, Observer {
            if (it.data != null) {
                currentAPOD = it.data
            }
        })
    }
}