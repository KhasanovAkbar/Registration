package pdp.uz.caremaandgallery.ui

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.material.bottomsheet.BottomSheetDialog
import pdp.uz.caremaandgallery.R
import pdp.uz.caremaandgallery.adapters.RvAdapter
import pdp.uz.caremaandgallery.databinding.BottomUserBinding
import pdp.uz.caremaandgallery.databinding.FragmentUsersBinding
import pdp.uz.caremaandgallery.db.MyDbHelper
import pdp.uz.caremaandgallery.models.User
import pdp.uz.caremaandgallery.utils.OnCallListener
import pdp.uz.caremaandgallery.utils.Utility
import java.io.File
import java.lang.String


class UsersFragment : Fragment(), OnCallListener<User> {
    lateinit var rvAdapter: RvAdapter
    lateinit var myDbHelper: MyDbHelper
    private var onCallListener: OnCallListener<User>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentUsersBinding.inflate(layoutInflater)
        myDbHelper = MyDbHelper(requireContext())
        val allUsers = myDbHelper.getAllUsers()

        rvAdapter = RvAdapter(allUsers, object : RvAdapter.OnMyItemClick {
            override fun onItemClick(user: User, position: Int) {
                val bottomSheetDialog = BottomSheetDialog(requireContext(), getTheme())
                val bottomUserBinding =
                    BottomUserBinding.inflate(LayoutInflater.from(context), null, false)
                bottomUserBinding.root.setBackgroundColor(Color.TRANSPARENT)
                if (user.image.equals(null)) {
                    bottomUserBinding.image.setImageResource(R.drawable.ic_baseline_person_24)

                } else {
                    bottomUserBinding.image.setImageURI(Uri.fromFile(File(user.image)))
                }
                bottomUserBinding.nameTv.text = user.name
                bottomUserBinding.countryTv.text = "${user.address}, ${user.country}"
                bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                bottomSheetDialog.setContentView(bottomUserBinding.root)
                bottomSheetDialog.show()

                bottomUserBinding.callBtn.setOnClickListener {
                    permission()
                    if (onCallListener != null) {
                        onCallListener!!.onCall(user)
                    }
                }

                bottomUserBinding.smsBtn.setOnClickListener {
                    permission()
                    val uri = Uri.parse(String.format("smsto:%s", user.phoneNumber))
                    val smsIntent = Intent(Intent.ACTION_SENDTO, uri)
                    smsIntent.putExtra("sms_body", "")
                    startActivityForResult(smsIntent, 1)
                }

            }

        })

        object : OnCallListener<User> {
            override fun onCall(t: User) {
                Utility.makeCall(requireContext(), t.phoneNumber!!)
            }

        }.also { onCallListener = it }
        binding.rv.adapter = rvAdapter
        return binding.root
    }

    private fun permission() {
        askPermission(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS
        ) {
        }.onDeclined { e ->
            if (e.hasDenied()) {
                AlertDialog.Builder(requireContext())
                    .setMessage("Permission must be granted in order to display contacts information")
                    .setPositiveButton("yes") { dialog, which ->
                        e.askAgain()
                    }
                    .setNegativeButton("no") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
            if (e.hasForeverDenied()) {
                e.goToSettings()
            }
        }
    }

    override fun onCall(t: User) {
        Utility.makeCall(requireContext(), t.phoneNumber!!)
    }


    fun getTheme(): Int = R.style.BottomSheetDialogTheme
}