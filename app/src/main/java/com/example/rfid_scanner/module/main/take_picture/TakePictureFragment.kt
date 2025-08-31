package com.example.rfid_scanner.module.main.take_picture

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import com.example.rfid_scanner.databinding.FragmentTakePictureBinding
import com.example.rfid_scanner.service.InteractiveSegmentationHelper
import com.example.rfid_scanner.utils.generic.fragment.BaseFragment
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.createBitmap


class TakePictureFragment : BaseFragment<FragmentTakePictureBinding, TakePictureViewModel>(), InteractiveSegmentationHelper.InteractiveSegmentationListener{

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTakePictureBinding.inflate(inflater, container, false)

    override fun getViewModelClass() = TakePictureViewModel::class.java

    private lateinit var interactiveSegmentationHelper: InteractiveSegmentationHelper
    private var isAllFabsVisible = false
    private var pictureUri: Uri? = null
    private var isModeOne = true

    private var inputBitmap: Bitmap? = null

    // Launch camera to receive new image for segmentation
    // Set image in View, start segmentation helper
    // Update UI
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess && pictureUri != null) {
                val bitmap = pictureUri!!.toBitmap()
                inputBitmap = bitmap
                binding.imgSegmentation.setImageBitmap(bitmap)
                interactiveSegmentationHelper.setInputImage(bitmap)
            }

            if (isAllFabsVisible) {
                fabsStateChange(false)
                isAllFabsVisible = false
            }
//            binding.tvDescription.visibility =
//                if (isSuccess) View.GONE else View.VISIBLE
        }

    // Open user gallery to select a photo for segmentation
    // Set image in View, start segmentation helper
    // Update UI
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.toBitmap()?.let { bitmap ->
                inputBitmap = bitmap
                binding.imgSegmentation.setImageBitmap(bitmap)
                interactiveSegmentationHelper.setInputImage(
                    bitmap
                )
            }

            if (isAllFabsVisible) {
                fabsStateChange(false)
                isAllFabsVisible = false
            }
//            binding.tvDescription.visibility =
//                if (it != null) View.GONE else View.VISIBLE
        }

    override fun setUpViews() {
        interactiveSegmentationHelper = InteractiveSegmentationHelper(
            requireActivity(),
            this
        )

        binding.clearFilter.setOnClickListener {
            binding.overlapView.clearAll()
            interactiveSegmentationHelper.points.clear()
        }

        binding.saveToFile.setOnClickListener {
//            Log.d("12345","${binding.overlapView.finalBitmap?.width},${binding.overlapView.finalBitmap?.height}")
//            Log.d("12345","${inputBitmap?.width},${inputBitmap?.height}")

            if (inputBitmap != null && binding.overlapView.finalBitmap != null) {
                val finalResult = overlayBitmaps(inputBitmap!!, binding.overlapView.finalBitmap!!)
                saveBitmapToGallery(requireActivity(), finalResult)
                showToast("Telah tersimpan")
            } else {
                showToast("Penyimpanan gagal, image harus ada dan harus sudah dicrop")
            }
        }

        binding.mode.setOnClickListener {
            isModeOne = !isModeOne
            if (isModeOne) {
                binding.tvModeText.text = "Mode 1 titik"
            } else {
                binding.tvModeText.text = "Mode banyak titik"
            }
            binding.overlapView.clearAll()
            interactiveSegmentationHelper.points.clear()
        }

        fabsStateChange(false)
        initListener()
        initTouch()
    }

    private fun clearOverlapResult() {
        binding.overlapView.clearAll()
        interactiveSegmentationHelper.points.clear()
        binding.imgSegmentation.setImageBitmap(null)
    }

    private fun initListener() {
        binding.addFab.setOnClickListener {
            isAllFabsVisible = if (!isAllFabsVisible) {
                fabsStateChange(true)
                true
            } else {
                fabsStateChange(false)
                false
            }
        }

        binding.takePicture.setOnClickListener {
            clearOverlapResult()
            pictureUri = getImageUri()
            pictureUri?.let {
                takePictureLauncher.launch(it)
            }
        }

        binding.pickPicture.setOnClickListener {
            clearOverlapResult()
            pickImageLauncher.launch("image/*")
        }
    }

    /**
     * Takes the position where the user touches on image (x and y)
     * and draws a marker above it to highlight where item of significance is found
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initTouch() {
        binding.imgSegmentation.setOnTouchListener { v, event ->
            val viewCoords = IntArray(2)
            binding.imgSegmentation.getLocationOnScreen(viewCoords)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (interactiveSegmentationHelper.isInputImageAssigned()) {
                        val touchX = event.x.toInt()
                        val touchY = event.y.toInt()

                        val imageX =
                            touchX // - viewCoords[0] // viewCoords[0] is the X coordinate
                        val imageY =
                            touchY // - viewCoords[1] // viewCoords[1] is the y coordinate

                        if (isModeOne) {
                            binding.overlapView.setSelectPosition(
                                imageX.toFloat(),
                                imageY.toFloat()
                            )
                        } else {
                            binding.overlapView.addSelectPosition(
                                imageX.toFloat(),
                                imageY.toFloat()
                            )
                        }


                        val normX = imageX.toFloat() / binding.imgSegmentation.width
                        val normY = imageY.toFloat() / binding.imgSegmentation.height

                        if (isModeOne) {
                            interactiveSegmentationHelper.segment(normX, normY)
                        } else {
                            interactiveSegmentationHelper.addsegment(normX, normY)
                        }

                    }
                }
                else -> {
                    // no-op
                }
            }
            true
        }
    }

    /**
     * Controls the state of the FAB buttons to show or hide.
     */
    private fun fabsStateChange(isStateShow: Boolean) {
        if (isStateShow) {
            with(binding) {
                takePicture.show()
                pickPicture.show()
                tvPickImageDescription.visibility = View.VISIBLE
                tvTakePictureDescription.visibility = View.VISIBLE
                addFab.extend()
            }
        } else {
            with(binding) {
                takePicture.hide()
                pickPicture.hide()
                tvPickImageDescription.visibility = View.GONE
                tvTakePictureDescription.visibility = View.GONE
                addFab.shrink()
            }
        }
    }

    /**
     * Create file ready for taking picture.
     */
    private fun getImageUri(): Uri {
        val filePicture = File(requireActivity().cacheDir.path + File.separator + "JPEG_" + SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date()) + ".jpg"
        )

        return FileProvider.getUriForFile(
            requireActivity(),
            requireActivity().applicationContext.packageName + ".fileprovider",
            filePicture
        )
    }

    private fun showError(errorMessage: String) {
        showToastLong(errorMessage)
    }

    /**
     * Converts Uri to Bitmap.
     * If a Bitmap is not of the ARGB_8888 type, it needs to be converted to
     * that type because the interactive segmentation helper requires that
     * specific Bitmap type.
     */
    private fun Uri.toBitmap(): Bitmap {
        val maxWidth = 512f
        var bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, this)
        } else {
            val source = ImageDecoder.createSource(requireActivity().contentResolver, this)
            ImageDecoder.decodeBitmap(source)
        }
        // reduce the size of image if it larger than maxWidth
        if (bitmap.width > maxWidth) {
            val scaleFactor = maxWidth / bitmap.width
            bitmap = bitmap.scale(
                (bitmap.width * scaleFactor).toInt(),
                (bitmap.height * scaleFactor).toInt(),
                false
            )
        }
        return if (bitmap.config == Bitmap.Config.ARGB_8888) {
            bitmap
        } else {
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
        }
    }

    override fun onError(error: String) {
        showError(error)
    }

    override fun onResults(result: InteractiveSegmentationHelper.ResultBundle?) {
        // Inform the overlap view to draw over the area of significance returned
        // from the helper
        result?.let {
            binding.overlapView.setMaskResult(
                it.byteBuffer,
                it.maskWidth,
                it.maskHeight
            )
        } ?: kotlin.run {
            binding.overlapView.clearAll()
            interactiveSegmentationHelper.points.clear()
        }
    }

    fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
        val filename = "stock_item_" + System.currentTimeMillis() + ".png"
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename) // "stock_item_cropped.png"
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(
            MediaStore.Images.Media.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + "/MyStockItems"
        )

        var uri: Uri? = null
        var outStream: OutputStream? = null

        try {
            val resolver: ContentResolver = context.contentResolver
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                outStream = resolver.openOutputStream(uri)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                Log.d("SaveImage", "Saved to gallery: " + uri.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (outStream != null) {
                try {
                    outStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }


    fun overlayBitmaps(background: Bitmap, foreground: Bitmap): Bitmap {
        // Make sure both bitmaps are the same size (or scale one if needed)
        val result = createBitmap(background.getWidth(), background.getHeight())
        val canvas = Canvas(result)

        // Draw the background first
        canvas.drawBitmap(background, 0f, 0f, null)

        // Then draw the foreground on top (respects alpha)
        val paint = Paint()
        paint.setAlpha(255) // Full opacity (can be adjusted if you want transparency)
        canvas.drawBitmap(foreground, 0f, 0f, paint)

        return result
    }
}


