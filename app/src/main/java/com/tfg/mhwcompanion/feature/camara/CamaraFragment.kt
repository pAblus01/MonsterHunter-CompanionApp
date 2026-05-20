package com.tfg.mhwcompanion.feature.camara

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tfg.mhwcompanion.R
import com.tfg.mhwcompanion.data.repository.RepositoryProvider
import com.tfg.mhwcompanion.databinding.FragmentCamaraBinding
import kotlinx.coroutines.launch
import java.io.File

class CamaraFragment : Fragment() {

    private var _binding: FragmentCamaraBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CamaraViewModel by viewModels {
        CamaraViewModel.factory(RepositoryProvider.armorRecognitionRepository(requireContext()))
    }

    private var imageCapture: ImageCapture? = null
    private var currentPhotoUri: Uri? = null

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onCameraPermissionChanged(granted)
        if (granted) {
            startCameraPreview()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val capturedUri = currentPhotoUri
        if (success && capturedUri != null) {
            viewModel.onImageCaptured(capturedUri)
            decodeBitmap(capturedUri)?.let(viewModel::analyzeCapturedImage)
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.onImageCaptured(uri)
            decodeBitmap(uri)?.let(viewModel::analyzeCapturedImage)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCamaraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sectionTitle.text = getString(R.string.camara_title)
        binding.sectionDescription.text = getString(R.string.camara_description)
        binding.resultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.captureButton.setOnClickListener { capturePhoto() }
        binding.galleryButton.setOnClickListener { pickImageLauncher.launch("image/*") }

        observeUiState()
        ensureCameraPermissionAndPreview()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (_binding == null) return@collect

                    binding.progressBar.isVisible = state.isLoading
                    binding.permissionHint.isVisible = !state.hasCameraPermission
                    binding.statusText.text = state.errorMessage ?: state.statusMessage ?: getString(R.string.camara_ready)
                    binding.previewImage.isVisible = state.capturedImageUri != null
                    binding.previewImage.setImageURI(state.capturedImageUri)
                    binding.emptyResultsText.isVisible = !state.isLoading && state.detectedArmors.isEmpty()
                    binding.emptyResultsText.text = state.errorMessage ?: getString(R.string.camara_results_empty)
                    binding.resultsRecyclerView.adapter = CamaraResultAdapter(state.detectedArmors)
                }
            }
        }
    }

    private fun ensureCameraPermissionAndPreview() {
        val isGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        viewModel.onCameraPermissionChanged(isGranted)
        if (isGranted) {
            startCameraPreview()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCameraPreview() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun capturePhoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
            return
        }

        val outputFile = File.createTempFile(
            "armor_capture_",
            ".jpg",
            requireContext().cacheDir
        )
        val outputUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            outputFile
        )
        currentPhotoUri = outputUri
        takePictureLauncher.launch(outputUri)
    }

    private fun decodeBitmap(uri: Uri): Bitmap? {
        return runCatching {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }
        }.getOrNull()
    }

    override fun onDestroyView() {
        binding.resultsRecyclerView.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
