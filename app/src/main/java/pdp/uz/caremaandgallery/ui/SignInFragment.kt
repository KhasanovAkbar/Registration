package pdp.uz.caremaandgallery.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.material.bottomsheet.BottomSheetDialog
import pdp.uz.caremaandgallery.BuildConfig
import pdp.uz.caremaandgallery.R
import pdp.uz.caremaandgallery.databinding.BotoomDialogBinding
import pdp.uz.caremaandgallery.databinding.FragmentSignInBinding
import pdp.uz.caremaandgallery.db.MyDbHelper
import pdp.uz.caremaandgallery.models.User
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SignInFragment : Fragment() {
    private lateinit var myDbHelper: MyDbHelper
    private lateinit var binding: FragmentSignInBinding
    private var COD_REQUEST = 1
    private lateinit var photoUri: Uri
    private lateinit var countryList: ArrayList<String>
    private lateinit var currentPhotoPath: String
    private var imagePath: String? = null
    private var isClick = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        myDbHelper = MyDbHelper(requireContext())
        loadData()
        val adapter =
            ArrayAdapter(container!!.context, android.R.layout.simple_spinner_item, countryList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.countrySpinner.adapter = adapter

        binding.image.setOnClickListener {
            permission()
        }


        val imageFile = createImageFile()
        photoUri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, imageFile)


        binding.registerBtn.setOnClickListener {

            val name = binding.nameEt.text.toString()
            val phoneNumber = binding.phoneNumberEt.text.toString()
            val country = countryList[binding.countrySpinner.selectedItemPosition]
            val address = binding.addressEt.text.toString()
            val password = binding.passwordEt.text.toString()

            if (name.isNotBlank() && phoneNumber.isNotBlank() && address.isNotBlank() && password.isNotBlank()) {

                val allUsers = myDbHelper.getAllUsers()
                var temp = 0
                for (i in 0 until allUsers.size) {
                    if (allUsers[i].password != password || allUsers[i].phoneNumber != phoneNumber) {
                        temp++
                    }
                }
                if (temp == allUsers.size) {
                    val user = User(name, phoneNumber, country, address, password, imagePath)
                    myDbHelper.insertUser(user)
                    findNavController().navigate(R.id.action_signInFragment_to_logInFragment)
                } else {
                    info("User available")
                }
            } else {
                info("Fields are empty")
            }
        }
        return binding.root
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (isClick == 0) {
            if (requestCode == COD_REQUEST && resultCode == Activity.RESULT_OK) {
                val uri = data?.data ?: return
                binding.image.setImageURI(uri)
                val openInputStream = activity?.contentResolver?.openInputStream(uri)
                val file = File(
                    activity?.filesDir,
                    "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
                )
                val fileOutputStream = FileOutputStream(file)
                openInputStream?.copyTo(fileOutputStream)
                openInputStream?.close()
                fileOutputStream.close()
                val absolutePath = file.absolutePath
                imagePath = absolutePath
            }
            isClick = -1
        } else if (isClick == 1) {
            if (::currentPhotoPath.isInitialized) {
                binding.image.setImageURI(Uri.fromFile(File(currentPhotoPath)))
                val openInputStream =
                    activity?.contentResolver?.openInputStream(Uri.fromFile(File(currentPhotoPath)))
                val file = File(
                    activity?.filesDir,
                    "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
                )
                val fileOutputStream = FileOutputStream(file)
                openInputStream?.copyTo(fileOutputStream)
                openInputStream?.close()
                fileOutputStream.close()
                val absolutePath = file.absolutePath
                imagePath = absolutePath
            }
            isClick = -1
        }
    }

    private val getTakeImageContent =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                binding.image.setImageURI(photoUri)
                val openInputStream = activity?.contentResolver?.openInputStream(photoUri)
                val file = File(
                    activity?.filesDir,
                    "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
                )
                val fileOutputStream = FileOutputStream(file)
                openInputStream?.copyTo(fileOutputStream)
                openInputStream?.close()
                fileOutputStream.close()
                val absolutePath = file.absolutePath
                imagePath = absolutePath
            }
        }

    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri ?: return@registerForActivityResult
            if (uri.equals(null)) {
                binding.image.setImageResource(R.drawable.ic_baseline_person_24)
            } else {
                binding.image.setImageURI(uri)
            }
            val openInputStream = activity?.contentResolver?.openInputStream(uri)
            val file = File(
                activity?.filesDir,
                "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
            )
            val fileOutputStream = FileOutputStream(file)
            openInputStream?.copyTo(fileOutputStream)
            openInputStream?.close()
            fileOutputStream.close()
            val absolutePath = file.absolutePath
            imagePath = absolutePath

        }

    private fun info(t: String) {
        Toast.makeText(requireContext(), t, Toast.LENGTH_SHORT).show()
    }

    private fun pickImageFromNewGallery() {
        getImageContent.launch("image/*")
    }

    private fun pickImageFromOldGallery() {
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }, COD_REQUEST)
    }

    private fun cameraNew() {
        getTakeImageContent.launch(photoUri)
    }

    private fun cameraOld() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile = createImageFile()
                photoFile.also {
                    val phoneUri =
                        FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, phoneUri)
                    startActivityForResult(takePictureIntent, COD_REQUEST)
                    isClick  = 1
                }

            }
        }
    }

    private fun permission() {
        askPermission(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        ) {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val bottomView =
                BotoomDialogBinding.inflate(LayoutInflater.from(context), null, false)
            bottomView.camera.setOnClickListener {
                isClick = 1
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    cameraNew()
                } else {
                    cameraOld()
                }
                bottomSheetDialog.dismiss()
            }
            bottomView.storage.setOnClickListener {
                isClick = 0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    pickImageFromNewGallery()
                } else {
                    pickImageFromOldGallery()
                }
                bottomSheetDialog.dismiss()

            }
            bottomSheetDialog.setContentView(bottomView.root)
            bottomSheetDialog.show()
        }.onDeclined { e ->
            if (e.hasDenied()) {
                info("Denied")
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
                info("Denied")
                e.goToSettings()
            }
        }
    }

    private fun loadData() {
        countryList =
            arrayListOf("Uzbekistan", "Russia", "Korea", "Japan", "USA", "US", "Egypt")
    }

}