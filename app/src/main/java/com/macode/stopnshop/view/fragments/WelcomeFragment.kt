package com.macode.stopnshop.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.FragmentWelcomeBinding
import com.macode.stopnshop.view.activities.SetUpActivity

class WelcomeFragment : Fragment() {
    private var binding:FragmentWelcomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        binding!!.welcomeSkipButton.setOnClickListener {
            startActivity(Intent(requireActivity(), SetUpActivity::class.java))
            requireActivity().finish()
        }

        return binding!!.root
    }

}