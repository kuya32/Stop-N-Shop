package com.macode.stopnshop.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.macode.stopnshop.databinding.FragmentHeadsUpBinding
import com.macode.stopnshop.view.activities.SetUpActivity

class HeadsUpFragment : Fragment() {

    private var binding: FragmentHeadsUpBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHeadsUpBinding.inflate(inflater, container, false)

        binding!!.headsUpSkipButton.setOnClickListener {
            startActivity(Intent(requireActivity(), SetUpActivity::class.java))
            requireActivity().finish()
        }

        return binding!!.root
    }

}