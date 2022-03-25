package com.example.spaceexplorer.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.spaceexplorer.R
import com.example.spaceexplorer.binding.FragmentDataBindingComponent
import com.example.spaceexplorer.databinding.FragmentOpportunityBinding
import com.example.spaceexplorer.databinding.FragmentSpiritRoverBinding
import com.example.spaceexplorer.di.util.Injectable
import com.example.spaceexplorer.util.autoCleared
import javax.inject.Inject

class SpiritRoverFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    var databindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentSpiritRoverBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate<FragmentSpiritRoverBinding>(
            inflater,
            R.layout.fragment_spirit_rover,
            container,
            false,
            databindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

}