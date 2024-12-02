package com.example.playlistmaker.media.ui.playlists.edit

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream

class EditPlaylistFragment : Fragment() {

    private val viewModel: EditPlaylistViewModel by viewModel { parametersOf(playlistId) }
    private var playlistId: Long = 0

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private var isEditText = false
    private var selectedImageUri: Uri? = null
    private var permissionGrantedAction: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = arguments?.getLong("playlistId") ?: 0

        binding.toolbarText.text = "Редактировать"
        binding.createPlaylistButton.text = "Сохранить"

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri

                Glide.with(this)
                    .load(uri)
                    .transform(CenterCrop(), RoundedCorners(20))
                    .into(binding.placeholderImageLarge)

                binding.placeholderImageSmall.isVisible = false
                isEditText = true
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        binding.editNamePlaylist.editText?.addTextChangedListener { s ->
            binding.createPlaylistButton.isEnabled = !s.isNullOrBlank()
            isEditText = !s.isNullOrBlank()
        }

        binding.editContextPlaylist.editText?.addTextChangedListener { s ->
            isEditText = !s.isNullOrBlank()
        }

        binding.placeholderImageLarge.setOnClickListener {
            if (checkStoragePermissions()) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                requestStoragePermissions {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
        }

        binding.createPlaylistButton.setOnClickListener {
            val name = binding.editNamePlaylist.editText?.text.toString()
            val description = binding.editContextPlaylist.editText?.text.toString()

            lifecycleScope.launch {
                val coverImagePath = saveImageToPrivateStorage(selectedImageUri)
                viewModel.updatePlaylist(name, description, coverImagePath)
                findNavController().navigateUp()
            }
        }

        viewModel.playlist.observe(viewLifecycleOwner, Observer { playlist ->
            playlist?.let {
                binding.editNamePlaylist.editText?.setText(it.name)
                binding.editContextPlaylist.editText?.setText(it.description)
                if (it.coverImagePath != null) {
                    Glide.with(this)
                        .load(it.coverImagePath)
                        .transform(CenterCrop(), RoundedCorners(20))
                        .into(binding.placeholderImageLarge)
                    binding.placeholderImageSmall.isVisible = false
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isEditText = false
    }

    private fun checkStoragePermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermissions(onPermissionGranted: () -> Unit) {
        permissionGrantedAction = onPermissionGranted
        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGrantedAction?.invoke()
        } else {
            Toast.makeText(requireContext(), "Доступ к хранилищу не предоставлен", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun saveImageToPrivateStorage(uri: Uri?): String? {
        return withContext(Dispatchers.IO) {
            uri?.let {
                val inputStream = requireContext().contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val filename = "playlist_cover_${System.currentTimeMillis()}.jpg"
                val file = File(requireContext().filesDir, filename)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }
                file.absolutePath
            } ?: viewModel.playlist.value?.coverImagePath
        }
    }

    companion object {
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1001
    }
}