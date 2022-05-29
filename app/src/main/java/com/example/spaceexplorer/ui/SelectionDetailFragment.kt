package com.example.spaceexplorer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.spaceexplorer.R
import com.example.spaceexplorer.binding.FragmentDataBindingComponent
import com.example.spaceexplorer.databinding.FragmentSelectionDetailBinding
import com.example.spaceexplorer.di.util.Injectable
import com.example.spaceexplorer.model.EditorsPickPhoto
import com.example.spaceexplorer.util.autoCleared
import com.example.spaceexplorer.viewmodels.EditorsPickViewModel
import kotlinx.android.synthetic.main.calendar_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_selection_detail.*
import kotlinx.android.synthetic.main.selection_detail_toolbar.*
import kotlinx.android.synthetic.main.selection_detail_toolbar.view.*
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
        val toolbar = selection_detail_top_app_bar
        toolbar.setTitle("Rings of Relativity")
    }

    private fun observeLiveData() {
        editorsPickViewModel.getEditorsPicks()
        editorsPickViewModel._editorsPickLiveData.observe(viewLifecycleOwner, Observer {
            val editorPickPhoto = getListItem(it, args.editorPickPhotoId)
            if (editorPickPhoto != null) {
                binding.photo = editorPickPhoto
                selection_detail_top_app_bar.title = editorPickPhoto.name
            }
        })
    }

    private fun getListItem(list: List<EditorsPickPhoto>, id: String): EditorsPickPhoto? {
        list.forEach { editorsPickPhoto ->
            if (editorsPickPhoto.photoId.equals(id)) {
                return editorsPickPhoto
            }
        }

        return null
    }


}