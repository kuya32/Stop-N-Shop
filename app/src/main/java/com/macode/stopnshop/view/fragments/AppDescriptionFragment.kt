package com.macode.stopnshop.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.FragmentAppDescriptionBinding
import com.macode.stopnshop.view.activities.SetUpActivity

class AppDescriptionFragment : Fragment() {

    private var binding: FragmentAppDescriptionBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppDescriptionBinding.inflate(inflater, container, false)

        binding!!.appSkipButton.setOnClickListener {
            startActivity(Intent(requireActivity(), SetUpActivity::class.java))
            requireActivity().finish()
        }

        return binding!!.root
    }

}