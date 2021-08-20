package pdp.uz.caremaandgallery.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import pdp.uz.caremaandgallery.R
import pdp.uz.caremaandgallery.databinding.FragmentLogInBinding
import pdp.uz.caremaandgallery.db.MyDbHelper

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LogInFragment : Fragment() {
    lateinit var myDbHelper: MyDbHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLogInBinding.inflate(layoutInflater)
        myDbHelper = MyDbHelper(container!!.context)
        val allUsers = myDbHelper.getAllUsers()

        binding.singIn.setOnClickListener {
            findNavController().navigate(R.id.signInFragment)
        }


        binding.loginBtn.setOnClickListener {
            val phoneNumber = binding.phoneNumberEt.text.toString()
            val password = binding.passwordEt.text.toString()
            if (phoneNumber.isNotBlank() && password.isNotBlank()) {
                var count = 0
                for (i in 0 until allUsers.size) {
                    if (allUsers[i].phoneNumber == phoneNumber && allUsers[i].password == password) {
                        findNavController().navigate(R.id.usersFragment)
                    }
                    else count++
                }

                if (count == allUsers.size) {
                    Toast.makeText(
                        requireContext(),
                        "Phone number or password incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(requireContext(), "Fields are empty", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}