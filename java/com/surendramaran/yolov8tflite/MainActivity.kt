//package com.surendramaran.yolov8tflite
//import android.speech.tts.TextToSpeech
//import java.util.Locale
//import android.Manifest
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Matrix
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.AspectRatio
//import androidx.camera.core.Camera
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.surendramaran.yolov8tflite.Constants.LABELS_PATH
//import com.surendramaran.yolov8tflite.Constants.MODEL_PATH
//import com.surendramaran.yolov8tflite.databinding.ActivityMainBinding
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//import com.surendramaran.yolov8tflite.Detector
//import com.surendramaran.yolov8tflite.BoundingBox
//import android.speech.SpeechRecognizer
//import android.speech.RecognitionListener
//import android.content.Intent
//import android.speech.RecognizerIntent
//import android.view.KeyEvent
//
//class MainActivity : AppCompatActivity(), Detector.DetectorListener {
//    private lateinit var binding: ActivityMainBinding
//    private val isFrontCamera = false
//
//
//    private var tts: TextToSpeech? = null
//    private var preview: Preview? = null
//    private var imageAnalyzer: ImageAnalysis? = null
//    private var camera: Camera? = null
//    private var cameraProvider: ProcessCameraProvider? = null
//    private lateinit var detector: Detector
//    private lateinit var speechRecognizer: SpeechRecognizer
//    private lateinit var speechRecognizerIntent: Intent
//    private var isDetecting = false
//    private var lastVolumeUpPressTime: Long = 0
//    private val doubleClickInterval: Long = 500
//
//    private lateinit var cameraExecutor: ExecutorService
//    private lateinit var textToSpeech: TextToSpeech
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this)
//        detector.setup()
//
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
//        }
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
//        textToSpeech = TextToSpeech(this) { status ->
//            if (status == TextToSpeech.SUCCESS) {
//                val result = textToSpeech.setLanguage(Locale.getDefault())
//                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    Log.e(TAG, "Language is not supported.")
//                }
//            } else {
//                Log.e(TAG, "Failed to initialize TextToSpeech.")
//            }
//        }
//        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
//        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
//        }
//
//        speechRecognizer.setRecognitionListener(object : RecognitionListener {
//            override fun onReadyForSpeech(params: Bundle?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onBeginningOfSpeech() {
//                TODO("Not yet implemented")
//            }
//
//            override fun onRmsChanged(rmsdB: Float) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onBufferReceived(buffer: ByteArray?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onEndOfSpeech() {
//                TODO("Not yet implemented")
//            }
//
//            override fun onError(error: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onResults(results: Bundle?) {
//                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//                if (matches != null) {
//                    handleVoiceCommand(matches[0])
//                }
//            }
//
//            override fun onPartialResults(partialResults: Bundle?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onEvent(eventType: Int, params: Bundle?) {
//                TODO("Not yet implemented")
//            }
//        })
//        fun startListening() {
//            speechRecognizer.startListening(speechRecognizerIntent)
//
//
//
//        }
//
//    }
////    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
////        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
////            val currentTime = System.currentTimeMillis()
////            if (currentTime - lastVolumeUpPressTime <= doubleClickInterval) {
////
////                openMyApplication()
////            }
////            lastVolumeUpPressTime = currentTime
////        }
////        return super.onKeyDown(keyCode, event)
////    }
//
////    private fun openMyApplication() {
////        val intent = Intent(this, MainActivity::class.java)
////        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////        startActivity(intent)
////    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            cameraProvider  = cameraProviderFuture.get()
//            bindCameraUseCases()
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun bindCameraUseCases() {
//        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
//
//        val rotation = binding.viewFinder.display.rotation
//
//        val cameraSelector = CameraSelector
//            .Builder()
//            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//            .build()
//
//        preview =  Preview.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setTargetRotation(rotation)
//            .build()
//
//        imageAnalyzer = ImageAnalysis.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .setTargetRotation(binding.viewFinder.display.rotation)
//            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//            .build()
//
//        imageAnalyzer?.setAnalyzer(cameraExecutor) { imageProxy ->
//            val bitmapBuffer =
//                Bitmap.createBitmap(
//                    imageProxy.width,
//                    imageProxy.height,
//                    Bitmap.Config.ARGB_8888
//                )
//            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
//            imageProxy.close()
//
//            val matrix = Matrix().apply {
//                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
//
//                if (isFrontCamera) {
//                    postScale(
//                        -1f,
//                        1f,
//                        imageProxy.width.toFloat(),
//                        imageProxy.height.toFloat()
//                    )
//                }
//            }
//
//            val rotatedBitmap = Bitmap.createBitmap(
//                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
//                matrix, true
//            )
//
//            detector.detect(rotatedBitmap)
//        }
//
//        cameraProvider.unbindAll()
//
//        try {
//            camera = cameraProvider.bindToLifecycle(
//                this,
//                cameraSelector,
//                preview,
//                imageAnalyzer
//            )
//
//            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//        } catch(exc: Exception) {
//            Log.e(TAG, "Use case binding failed", exc)
//        }
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()) {
//        if (it[Manifest.permission.CAMERA] == true) { startCamera() }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        detector.clear()
//        cameraExecutor.shutdown()
//        textToSpeech.shutdown()
//        speechRecognizer.destroy()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        startListening()
//        if (allPermissionsGranted()){
//            startCamera()
//        } else {
//            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
//        }
//    }
//    override fun onPause() {
//        super.onPause()
//        speechRecognizer.stopListening()
//    }
//
//    private fun startListening() {
//        speechRecognizer.startListening(speechRecognizerIntent)
//    }
//    private fun handleVoiceCommand(command: String) {
//        when (command.lowercase()) {
//            "start" -> Log.i("VoiceCommand", "Starting something...")
//            "stop" -> Log.i("VoiceCommand", "Stopping something...")
//
//        }
//    }
//
//    companion object {
//        private const val TAG = "Camera"
//        private const val REQUEST_CODE_PERMISSIONS = 10
//        private val REQUIRED_PERMISSIONS = mutableListOf (
//            Manifest.permission.CAMERA
//        ).toTypedArray()
//    }
//
//    override fun onEmptyDetect() {
//        binding.overlay.invalidate()
//    }
//
//    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
//        runOnUiThread {
//            binding.inferenceTime.text = "${inferenceTime}ms"
//            binding.overlay.apply {
//                setResults(boundingBoxes)
//                invalidate()
//            }
//        }
//
//
//        for (boundingBox in boundingBoxes) {
//                val label = boundingBox.clsName
//                val confidence = boundingBox.cnf
//                val speechText = "Beware $label "
//            try {
//                textToSpeech.speak(speechText, TextToSpeech.QUEUE_ADD, null, null)
//            } catch (e: Exception) {
//                Log.e(TAG, "Error during TextToSpeech: ${e.message}")
//            }
//
//
//        }
//    }
//}
//
//package com.surendramaran.yolov8tflite
//import android.speech.tts.TextToSpeech
//import java.util.Locale
//import android.Manifest
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Matrix
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.AspectRatio
//import androidx.camera.core.Camera
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.surendramaran.yolov8tflite.Constants.LABELS_PATH
//import com.surendramaran.yolov8tflite.Constants.MODEL_PATH
//import com.surendramaran.yolov8tflite.databinding.ActivityMainBinding
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//import com.surendramaran.yolov8tflite.Detector
//import com.surendramaran.yolov8tflite.BoundingBox
//
//class MainActivity : AppCompatActivity(), Detector.DetectorListener {
//    private lateinit var binding: ActivityMainBinding
//    private val isFrontCamera = false
//
//
//    private var tts: TextToSpeech? = null
//    private var preview: Preview? = null
//    private var imageAnalyzer: ImageAnalysis? = null
//    private var camera: Camera? = null
//    private var cameraProvider: ProcessCameraProvider? = null
//    private lateinit var detector: Detector
//
//    private lateinit var cameraExecutor: ExecutorService
//    private lateinit var textToSpeech: TextToSpeech
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this)
//        detector.setup()
//
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
//        }
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
//        textToSpeech = TextToSpeech(this) { status ->
//            if (status == TextToSpeech.SUCCESS) {
//                val result = textToSpeech.setLanguage(Locale.getDefault())
//                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    Log.e(TAG, "Language is not supported.")
//                }
//            } else {
//                Log.e(TAG, "Failed to initialize TextToSpeech.")
//            }
//        }
//    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            cameraProvider  = cameraProviderFuture.get()
//            bindCameraUseCases()
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun bindCameraUseCases() {
//        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
//
//        val rotation = binding.viewFinder.display.rotation
//
//        val cameraSelector = CameraSelector
//            .Builder()
//            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//            .build()
//
//        preview =  Preview.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setTargetRotation(rotation)
//            .build()
//
//        imageAnalyzer = ImageAnalysis.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .setTargetRotation(binding.viewFinder.display.rotation)
//            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//            .build()
//
//        imageAnalyzer?.setAnalyzer(cameraExecutor) { imageProxy ->
//            val bitmapBuffer =
//                Bitmap.createBitmap(
//                    imageProxy.width,
//                    imageProxy.height,
//                    Bitmap.Config.ARGB_8888
//                )
//            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
//            imageProxy.close()
//
//            val matrix = Matrix().apply {
//                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
//
//                if (isFrontCamera) {
//                    postScale(
//                        -1f,
//                        1f,
//                        imageProxy.width.toFloat(),
//                        imageProxy.height.toFloat()
//                    )
//                }
//            }
//
//            val rotatedBitmap = Bitmap.createBitmap(
//                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
//                matrix, true
//            )
//
//            detector.detect(rotatedBitmap)
//        }
//
//        cameraProvider.unbindAll()
//
//        try {
//            camera = cameraProvider.bindToLifecycle(
//                this,
//                cameraSelector,
//                preview,
//                imageAnalyzer
//            )
//
//            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//        } catch(exc: Exception) {
//            Log.e(TAG, "Use case binding failed", exc)
//        }
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()) {
//        if (it[Manifest.permission.CAMERA] == true) { startCamera() }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        detector.clear()
//        cameraExecutor.shutdown()
//        textToSpeech.shutdown()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (allPermissionsGranted()){
//            startCamera()
//        } else {
//            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
//        }
//    }
//
//    companion object {
//        private const val TAG = "Camera"
//        private const val REQUEST_CODE_PERMISSIONS = 10
//        private val REQUIRED_PERMISSIONS = mutableListOf (
//            Manifest.permission.CAMERA
//        ).toTypedArray()
//    }
//
//    override fun onEmptyDetect() {
//        binding.overlay.invalidate()
//    }
//
//    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
//        runOnUiThread {
//            binding.inferenceTime.text = "${inferenceTime}ms"
//            binding.overlay.apply {
//                setResults(boundingBoxes)
//                invalidate()
//            }
//        }
//
//
//        for (boundingBox in boundingBoxes) {
//            val label = boundingBox.clsName
//            val confidence = boundingBox.cnf
//            val speechText = "Beware $label "
//            try {
//                textToSpeech.speak(speechText, TextToSpeech.QUEUE_ADD, null, null)
//            } catch (e: Exception) {
//                Log.e(TAG, "Error during TextToSpeech: ${e.message}")
//            }
//
//
//        }
//    }
//}

//
//package com.surendramaran.yolov8tflite
//import android.os.SystemClock
//
//import android.speech.tts.TextToSpeech
//import java.util.Locale
//import android.Manifest
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Matrix
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.*
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.surendramaran.yolov8tflite.Constants.LABELS_PATH
//import com.surendramaran.yolov8tflite.Constants.MODEL_PATH
//import com.surendramaran.yolov8tflite.databinding.ActivityMainBinding
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//import com.surendramaran.yolov8tflite.Detector
//import com.surendramaran.yolov8tflite.BoundingBox
//
//class MainActivity : AppCompatActivity(), Detector.DetectorListener {
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var detector: Detector
//    private lateinit var textToSpeech: TextToSpeech
//    private lateinit var cameraExecutor: ExecutorService
//    private lateinit var handler: Handler
//    private var lastSpeakTime: Long = 0
//    private val speakInterval: Long = 10000
//
//    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
//    private val REQUEST_CODE_PERMISSIONS = 10
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this)
//        detector.setup()
//
//        textToSpeech = TextToSpeech(this) { status ->
//            if (status == TextToSpeech.SUCCESS) {
//                val result = textToSpeech.setLanguage(Locale.getDefault())
//                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    Log.e("Camera", "اللغة غير مدعومة.")
//                }
//            } else {
//                Log.e("Camera", "فشل في تهيئة TextToSpeech.")
//            }
//        }
//
//        handler = Handler(Looper.getMainLooper())
//
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
//        }
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
//    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//            bindCameraUseCases(cameraProvider)
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
//        val rotation = binding.viewFinder.display.rotation
//        val cameraSelector = CameraSelector.Builder()
//            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//            .build()
//
//        val preview = Preview.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setTargetRotation(rotation)
//            .build()
//
//        val imageAnalyzer = ImageAnalysis.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//            .build()
//
//        imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
//            val bitmapBuffer = Bitmap.createBitmap(
//                imageProxy.width,
//                imageProxy.height,
//                Bitmap.Config.ARGB_8888
//            )
//            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
//            imageProxy.close()
//
//            val matrix = Matrix().apply {
//                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
//            }
//
//            val rotatedBitmap = Bitmap.createBitmap(
//                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
//                matrix, true
//            )
//
//            detector.detect(rotatedBitmap)
//        }
//
//        cameraProvider.unbindAll()
//
//        try {
//            cameraProvider.bindToLifecycle(
//                this, cameraSelector, preview, imageAnalyzer
//            )
//
//            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//        } catch (exc: Exception) {
//            Log.e("Camera", "فشل في ربط الحالات", exc)
//        }
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        detector.clear()
//        cameraExecutor.shutdown()
//        textToSpeech.shutdown()
//    }
//    companion object {
//        private const val TAG = "Blind"
//    }
//
//
//    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
//        runOnUiThread {
//            binding.inferenceTime.text = "${inferenceTime}ms"
//            binding.overlay.apply {
//                setResults(boundingBoxes)
//                invalidate()
//            }
//        }
//
//        val currentTime = SystemClock.uptimeMillis()
//        if (currentTime - lastSpeakTime >= speakInterval) {
//            for (boundingBox in boundingBoxes) {
//                val label = boundingBox.clsName
//                //val confidence = boundingBox.cnf
//                val width = boundingBox.w
//                val height = boundingBox.h
//                val speechText = "Beware $label "
//                if (width > 0.1 && height > 0.1) {
//                    try {
//                        textToSpeech.speak(speechText, TextToSpeech.QUEUE_ADD, null, null)
//                        lastSpeakTime = currentTime
//                    } catch (e: Exception) {
//                        Log.e(TAG, "Error during TextToSpeech: ${e.message}")
//                    }
//
//                }
//           }
//        }
//    }
//    override fun onEmptyDetect() {
//        binding.overlay.invalidate()
//    }
//}


//package com.surendramaran.yolov8tflite
//
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Matrix
//import android.os.Bundle
//import android.os.SystemClock
//import android.os.Handler
//import android.os.Looper
//import android.speech.RecognizerIntent
//import android.speech.tts.TextToSpeech
//import android.util.Log
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.*
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.surendramaran.yolov8tflite.databinding.ActivityMainBinding
//import java.util.*
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//
//class MainActivity : AppCompatActivity(), Detector.DetectorListener {
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var detector: Detector
//    private lateinit var textToSpeech: TextToSpeech
//    private lateinit var cameraExecutor: ExecutorService
//    private lateinit var handler: Handler
//    private var lastSpeakTime: Long = 0
//    private val speakInterval: Long = 10000
//    private var speechCommand: String = ""  // متغير لتخزين الأمر الصوتي
//
//    private val REQUIRED_PERMISSIONS = arrayOf(
//        Manifest.permission.CAMERA,
//        Manifest.permission.RECORD_AUDIO  // صلاحيات جديدة
//    )
//    private val REQUEST_CODE_PERMISSIONS = 10
//
//    companion object {
//        private const val MODEL_PATH = "best_float16.tflite"  // التعيين الصحيح
//        private const val LABELS_PATH = "labels.txt"          // تعيين المسار
//        private const val TAG = "MainActivity"
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this)
//        detector.setup()
//
//        textToSpeech = TextToSpeech(this) { status ->
//            if (status == TextToSpeech.SUCCESS) {
//                val result = textToSpeech.setLanguage(Locale.getDefault())
//                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    Log.e(TAG, "اللغة غير مدعومة.")
//                }
//            } else {
//                Log.e(TAG, "فشل في تهيئة TextToSpeech.")
//            }
//        }
//
//        handler = Handler(Looper.getMainLooper())
//
//        if (allPermissionsGranted()) {
//            startCamera()  // تأكد من أن هذه الدالة موجودة
//        } else {
//            ActivityCompat.requestPermissions(
//                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
//            )
//        }
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
//        // إطلاق ميزة التعرف على الصوت
//        startSpeechRecognition()
//    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//            bindCameraUseCases(cameraProvider)
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
//        val rotation = binding.viewFinder.display.rotation
//        val cameraSelector = CameraSelector.Builder()
//            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//            .build()
//
//        val preview = Preview.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setTargetRotation(rotation)
//            .build()
//
//        val imageAnalyzer = ImageAnalysis.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//            .build()
//
//        imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
//            val bitmapBuffer = Bitmap.createBitmap(
//                imageProxy.width,
//                imageProxy.height,
//                Bitmap.Config.ARGB_8888
//            )
//            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
//            imageProxy.close()
//
//            val matrix = Matrix().apply {
//                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())  // استخدام postRotate
//            }
//
//            val rotatedBitmap = Bitmap.createBitmap(
//                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
//                matrix, true
//            )
//
//            detector.detect(rotatedBitmap)  // تشغيل الكشف
//        }
//
//        try {
//            cameraProvider.bindToLifecycle(
//                this, cameraSelector, preview, imageAnalyzer
//            )
//
//            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//        } catch (exc: Exception) {
//            Log.e(TAG, "فشل في ربط الحالات", exc)
//        }
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        detector.clear()
//        textToSpeech.shutdown()
//        cameraExecutor.shutdown()
//    }
//
//    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
//        runOnUiThread {
//            binding.inferenceTime.text = "${inferenceTime}ms"
//            binding.overlay.apply {
//                setResults(boundingBoxes)
//                invalidate()
//            }
//        }
//
//        val currentTime = SystemClock.uptimeMillis()
//
//        // التحقق من القيمة المخزنة لتحديد ما إذا كانت القراءة الصوتية مفعلة
//        if (speechCommand == "start" && (currentTime - lastSpeakTime >= speakInterval)) {
//            for (boundingBox in boundingBoxes) {
//                val label = boundingBox.clsName
//                val width = boundingBox.w
//                val height = boundingBox.h
//                val speechText = "احترس، هناك $label"
//
//                if (width > 0.1 && height > 0.1) {
//                    try {
//                        textToSpeech.speak(speechText, TextToSpeech.QUEUE_ADD, null, null)
//                        lastSpeakTime = currentTime
//                    } catch (e: Exception) {
//                        Log.e(TAG, "خطأ أثناء TextToSpeech: ${e.message}")
//                    }
//                }
//            }
//        }
//    }
//
//    private fun startSpeechRecognition() {
//        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
//        }
//        speechRecognitionLauncher.launch(speechRecognizerIntent)  // إطلاق التعرف على الصوت
//    }
//
//    private val speechRecognitionLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == RESULT_OK && result.data != null) {
//            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
//            if (!matches.isNullOrEmpty()) {
//                speechCommand = matches[0].lowercase(Locale.getDefault())  // حفظ الأمر الصوتي
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted()) {
//                startCamera()
//            } else {
//                Log.d(TAG, "الأذونات المطلوبة غير ممنوحة.")
//            }
//        }
//    }
//
//    override fun onEmptyDetect() {
//        binding.overlay.invalidate()  // التحقق من الكشف الفارغ
//    }
//}


//package com.surendramaran.yolov8tflite
//import android.os.SystemClock
//
//import android.speech.tts.TextToSpeech
//import java.util.Locale
//import android.Manifest
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Matrix
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.*
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.surendramaran.yolov8tflite.Constants.LABELS_PATH
//import com.surendramaran.yolov8tflite.Constants.MODEL_PATH
//import com.surendramaran.yolov8tflite.databinding.ActivityMainBinding
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//import com.surendramaran.yolov8tflite.Detector
//import com.surendramaran.yolov8tflite.BoundingBox
//import android.view.KeyEvent
//
//class MainActivity : AppCompatActivity(), Detector.DetectorListener {
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var detector: Detector
//    private lateinit var textToSpeech: TextToSpeech
//    private lateinit var cameraExecutor: ExecutorService
//    private lateinit var handler: Handler
//    private var lastSpeakTime: Long = 0
//    private val speakInterval: Long = 10000
//    private var volumeUpPressed = false
//    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
//    private val REQUEST_CODE_PERMISSIONS = 10
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this)
//        detector.setup()
//
//        textToSpeech = TextToSpeech(this) { status ->
//            if (status == TextToSpeech.SUCCESS) {
//                val result = textToSpeech.setLanguage(Locale.getDefault())
//                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    Log.e("Camera", "اللغة غير مدعومة.")
//                }
//            } else {
//                Log.e("Camera", "فشل في تهيئة TextToSpeech.")
//            }
//        }
//
//        handler = Handler(Looper.getMainLooper())
//
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
//        }
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
//    }
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            if (volumeUpPressed) {
//                // Perform your action here to exit the app
//                finish()
//                return true
//            } else {
//                volumeUpPressed = true
//            }
//        }
//        return super.onKeyDown(keyCode, event)
//    }
//
//    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            volumeUpPressed = false
//        }
//        return super.onKeyUp(keyCode, event)
//    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//            bindCameraUseCases(cameraProvider)
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
//        val rotation = binding.viewFinder.display.rotation
//        val cameraSelector = CameraSelector.Builder()
//            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//            .build()
//
//        val preview = Preview.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setTargetRotation(rotation)
//            .build()
//
//        val imageAnalyzer = ImageAnalysis.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//            .build()
//
//        imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
//            val bitmapBuffer = Bitmap.createBitmap(
//                imageProxy.width,
//                imageProxy.height,
//                Bitmap.Config.ARGB_8888
//            )
//            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
//            imageProxy.close()
//
//            val matrix = Matrix().apply {
//                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
//            }
//
//            val rotatedBitmap = Bitmap.createBitmap(
//                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
//                matrix, true
//            )
//
//            detector.detect(rotatedBitmap)
//        }
//
//        cameraProvider.unbindAll()
//
//        try {
//            cameraProvider.bindToLifecycle(
//                this, cameraSelector, preview, imageAnalyzer
//            )
//
//            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//        } catch (exc: Exception) {
//            Log.e("Camera", "فشل في ربط الحالات", exc)
//        }
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        detector.clear()
//        cameraExecutor.shutdown()
//        textToSpeech.shutdown()
//    }
//    companion object {
//        private const val TAG = "Blind"
//    }
//
//
//    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
//        runOnUiThread {
//            binding.inferenceTime.text = "${inferenceTime}ms"
//            binding.overlay.apply {
//                setResults(boundingBoxes)
//                invalidate()
//            }
//        }
//
//        val currentTime = SystemClock.uptimeMillis()
//        if (currentTime - lastSpeakTime >= speakInterval) {
//            for (boundingBox in boundingBoxes) {
//                val label = boundingBox.clsName
//                //val confidence = boundingBox.cnf
//                val width = boundingBox.w
//                val height = boundingBox.h
//                val speechText = "Beware $label "
//                if (width > 0.1 && height > 0.1) {
//                    try {
//                        textToSpeech.speak(speechText, TextToSpeech.QUEUE_ADD, null, null)
//                        lastSpeakTime = currentTime
//                    } catch (e: Exception) {
//                        Log.e(TAG, "Error during TextToSpeech: ${e.message}")
//                    }
//
//                }
//           }
//        }
//    }
//    override fun onEmptyDetect() {
//        binding.overlay.invalidate()
//    }
//}



//package com.surendramaran.yolov8tflite
//
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Matrix
//import android.os.Bundle
//import android.os.SystemClock
//import android.speech.RecognitionListener
//import android.speech.RecognizerIntent
//import android.speech.SpeechRecognizer
//import android.speech.tts.TextToSpeech
//import android.util.Log
//import android.view.KeyEvent
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.*
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.surendramaran.yolov8tflite.databinding.ActivityMainBinding
//import java.util.*
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//
//class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener, Detector.DetectorListener {
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var textToSpeech: TextToSpeech
//    private lateinit var detector: Detector
//    private lateinit var speechRecognizer: SpeechRecognizer
//    private lateinit var cameraExecutor: ExecutorService
//    private lateinit var speechRecognizerIntent: Intent
//    private var lastSpeakTime: Long = 0
//    private val speakInterval: Long = 10000
//
//    private var lastVolumeUpPressTime: Long = 0
//    private val DOUBLE_PRESS_INTERVAL: Long = 500
//
//    private var isListening: Boolean = false
//    private var voiceCommand: String = ""
//
//    companion object {
//        private const val MODEL_PATH = "best_float16.tflite"
//        private const val LABELS_PATH = "labels.txt"
//        private const val TAG = "MainActivity"
//
//        private val REQUIRED_PERMISSIONS = arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO
//        )
//
//        private const val REQUEST_CODE_PERMISSIONS = 10
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        textToSpeech = TextToSpeech(this, this)
//
//        detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this)
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
//        setupSpeechRecognizer()
//
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            requestPermissions()
//        }
//    }
//
//    private fun setupSpeechRecognizer() {
//        if (SpeechRecognizer.isRecognitionAvailable(this)) {
//            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
//            speechRecognizer.setRecognitionListener(object : RecognitionListener {
//                override fun onReadyForSpeech(params: Bundle?) {}
//                override fun onBeginningOfSpeech() {}
//                override fun onRmsChanged(rmsdB: Float) {}
//                override fun onBufferReceived(buffer: ByteArray?) {}
//                override fun onEndOfSpeech() {
//                    isListening = false
//                }
//                override fun onError(error: Int) {
//                    Log.e(TAG, "Error in SpeechRecognizer: $error")
//                }
//                override fun onResults(results: Bundle?) {
//                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//                    if (!matches.isNullOrEmpty()) {
//                        voiceCommand = matches[0]
//                    }
//                }
//                override fun onPartialResults(results: Bundle?) {}
//                override fun onEvent(eventType: Int, params: Bundle?) {}
//            })
//        } else {
//            Log.e(TAG, "Speech recognition is not available on this device")
//        }
//    }
//
//    override fun onInit(status: Int) {
//        if (status == TextToSpeech.SUCCESS) {
//            val result = textToSpeech.setLanguage(Locale.getDefault())
//            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Log.e(TAG, "Language not supported")
//            }
//        } else {
//            Log.e(TAG, "TextToSpeech initialization failed")
//        }
//    }
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            val currentTime = SystemClock.uptimeMillis()
//            if (currentTime - lastVolumeUpPressTime <= DOUBLE_PRESS_INTERVAL) {
//                finish()
//                return true
//            }
//            lastVolumeUpPressTime = currentTime
//        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//            startListening()
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }
//
//    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//            stopListening()
//            return true
//        }
//        return super.onKeyUp(keyCode, event)
//    }
//
//    private fun startListening() {
//        if (!isListening) {
//            speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
//                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
//            }
//            speechRecognizer.startListening(speechRecognizerIntent)
//            isListening = true
//        }
//    }
//
//    private fun stopListening() {
//        if (isListening) {
//            speechRecognizer.stopListening()
//            isListening = false
//        }
//    }
//
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(
//            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
//        )
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//            bindCameraUseCases(cameraProvider)
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
//        val rotation = binding.viewFinder.display.rotation
//        val cameraSelector = CameraSelector.Builder()
//            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//            .build()
//
//        val preview = Preview.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setTargetRotation(rotation)
//            .build()
//
//        preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//
//        val imageAnalyzer = ImageAnalysis.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .build()
//
//        imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
//            val bitmapBuffer = Bitmap.createBitmap(
//                imageProxy.width,
//                imageProxy.height,
//                Bitmap.Config.ARGB_8888
//            )
//            val matrix = Matrix().apply {
//                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
//            }
//
//            val rotatedBitmap = Bitmap.createBitmap(
//                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
//                matrix, true
//            )
//
//            detector.detect(rotatedBitmap)
//            imageProxy.close()
//        }
//
//        cameraProvider.bindToLifecycle(
//            this, cameraSelector, preview, imageAnalyzer
//        )
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        textToSpeech.shutdown()
//        cameraExecutor.shutdown()
//        speechRecognizer.destroy()
//    }
//
//    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
//        binding.inferenceTime.text = "${inferenceTime}ms"
//        if (boundingBoxes.isNotEmpty()) {
//            val currentTime = SystemClock.uptimeMillis()
//
//            if (currentTime - lastSpeakTime >= speakInterval) {
//                boundingBoxes.forEach { box ->
//                    if (box.w > 0.1 && box.h > 0.1) {
//                        textToSpeech.speak("Beware, there is ${box.clsName}", TextToSpeech.QUEUE_ADD, null, null)
//                        lastSpeakTime = currentTime
//                    }
//                }
//            }
//        }
//        binding.overlay.setResults(boundingBoxes)
//        binding.overlay.invalidate()
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted()) {
//                startCamera()
//            } else {
//                Log.e(TAG, "Required permissions not granted.")
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
//
//    override fun onEmptyDetect() {
//        binding.overlay.invalidate()
//    }
//}



//package com.surendramaran.yolov8tflite
//
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Matrix
//import android.os.Bundle
//import android.os.SystemClock
//import android.speech.RecognitionListener
//import android.speech.RecognizerIntent
//import android.speech.SpeechRecognizer
//import android.speech.tts.TextToSpeech
//import android.util.Log
//import android.view.KeyEvent
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.*
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.surendramaran.yolov8tflite.databinding.ActivityMainBinding
//import java.util.*
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//
//class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener, Detector.DetectorListener {
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var textToSpeech: TextToSpeech
//    private lateinit var detector: Detector
//    private lateinit var speechRecognizer: SpeechRecognizer
//    private lateinit var cameraExecutor: ExecutorService
//    private lateinit var speechRecognizerIntent: Intent
//    private var lastSpeakTime: Long = 0
//    private val speakInterval: Long = 10000
//
//    private var lastVolumeUpPressTime: Long = 0
//    private val DOUBLE_PRESS_INTERVAL: Long = 500
//
//    private var isListening: Boolean = false
//    private var voiceCommand: String = ""
//
//    companion object {
//        private const val MODEL_PATH = "best_float16.tflite"
//        private const val LABELS_PATH = "labels.txt"
//        private const val TAG = "MainActivity"
//
//        private val REQUIRED_PERMISSIONS = arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO
//        )
//
//        private const val REQUEST_CODE_PERMISSIONS = 10
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        textToSpeech = TextToSpeech(this, this)
//
//        detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this)
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
//
//        setupSpeechRecognizer()
//
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            requestPermissions()
//        }
//    }
//
//    private fun setupSpeechRecognizer() {
//        if (SpeechRecognizer.isRecognitionAvailable(this)) {
//            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
//            speechRecognizer.setRecognitionListener(object : RecognitionListener {
//                override fun onReadyForSpeech(params: Bundle?) {}
//                override fun onBeginningOfSpeech() {}
//                override fun onRmsChanged(rmsdB: Float) {}
//                override fun onBufferReceived(buffer: ByteArray?) {}
//                override fun onEndOfSpeech() {
//                    isListening = false
//                }
//                override fun onError(error: Int) {
//                    Log.e(TAG, "Error in SpeechRecognizer: $error")
//                }
//                override fun onResults(results: Bundle?) {
//                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//                    if (!matches.isNullOrEmpty()) {
//                        voiceCommand = matches[0]
//                    }
//                }
//                override fun onPartialResults(results: Bundle?) {}
//                override fun onEvent(eventType: Int, params: Bundle?) {}
//            })
//        } else {
//            Log.e(TAG, "Speech recognition is not available on this device")
//        }
//    }
//
//    override fun onInit(status: Int) {
//        if (status == TextToSpeech.SUCCESS) {
//            val result = textToSpeech.setLanguage(Locale.getDefault())
//            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Log.e(TAG, "Language not supported")
//            }
//        } else {
//            Log.e(TAG, "TextToSpeech initialization failed")
//        }
//    }
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            val currentTime = SystemClock.uptimeMillis()
//            if (currentTime - lastVolumeUpPressTime <= DOUBLE_PRESS_INTERVAL) {
//                finish()
//                return true
//            }
//            lastVolumeUpPressTime = currentTime
//        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//            startListening()
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }
//
//    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//            stopListening()
//            return true
//        }
//        return super.onKeyUp(keyCode, event)
//    }
//
//    private fun startListening() {
//        if (!isListening) {
//            speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
//                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
//            }
//            speechRecognizer.startListening(speechRecognizerIntent)
//            isListening = true
//        }
//    }
//
//    private fun stopListening() {
//        if (isListening) {
//            speechRecognizer.stopListening()
//            isListening = false
//        }
//    }
//
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(
//            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
//        )
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//            bindCameraUseCases(cameraProvider)
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
//        val rotation = binding.viewFinder.display.rotation
//        val cameraSelector = CameraSelector.Builder()
//            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//            .build()
//
//        val preview = Preview.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setTargetRotation(rotation)
//            .build()
//
//        val imageAnalyzer = ImageAnalysis.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .build()
//
//        imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
//            try {
//                val bitmapBuffer = Bitmap.createBitmap(
//                    imageProxy.width,
//                    imageProxy.height,
//                    Bitmap.Config.ARGB_8888
//                )
//
//                // نسخ بيانات الصورة إلى البفر
//                imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
//
//                val matrix = Matrix().apply {
//                    postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
//                }
//
//                val rotatedBitmap = Bitmap.createBitmap(
//                    bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
//                    matrix, true
//                )
//
//                detector.detect(rotatedBitmap) // تأكد من أن هذه الدالة تعمل بشكل صحيح
//
//            } catch (e: Exception) {
//                Log.e(TAG, "Error processing image", e)
//            } finally {
//                imageProxy.close() // تأكد من إغلاق الـImageProxy
//            }
//        }
//
//        cameraProvider.unbindAll()
//
//        try {
//            cameraProvider.bindToLifecycle(
//                this, cameraSelector, preview, imageAnalyzer
//            )
//
//            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//
//        } catch (exc: Exception) {
//            Log.e(TAG, "Binding failed", exc)
//        }
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        textToSpeech.shutdown()
//        cameraExecutor.shutdown()
//        speechRecognizer.destroy()
//    }
//
//    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
//        binding.inferenceTime.text = "${inferenceTime}ms"
//        if (boundingBoxes.isNotEmpty()) {
//            val currentTime = SystemClock.uptimeMillis()
//
//            if (currentTime - lastSpeakTime >= speakInterval) {
//                boundingBoxes.forEach { box ->
//                    if (box.w > 0.1 && box.h > 0.1) {
//                        textToSpeech.speak("Beware, there is ${box.clsName}", TextToSpeech.QUEUE_ADD, null, null)
//                        lastSpeakTime = currentTime
//                    }
//                }
//            }
//        }
//        binding.overlay.setResults(boundingBoxes)
//        binding.overlay.invalidate()
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted()) {
//                startCamera()
//            } else {
//                Log.e(TAG, "Required permissions not granted.")
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
//
//    override fun onEmptyDetect() {
//        binding.overlay.invalidate()
//    }
//}
//




//package com.surendramaran.yolov8tflite
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Matrix
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.os.SystemClock
//import android.speech.tts.TextToSpeech
//import android.util.Log
//import android.view.KeyEvent
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.*
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.surendramaran.yolov8tflite.Constants.LABELS_PATH
//import com.surendramaran.yolov8tflite.Constants.MODEL_PATH
//import com.surendramaran.yolov8tflite.databinding.ActivityMainBinding
//import java.util.Locale
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//import com.surendramaran.yolov8tflite.Detector
//import com.surendramaran.yolov8tflite.BoundingBox
//
//class MainActivity : AppCompatActivity(), Detector.DetectorListener {
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var detector: Detector
//    private lateinit var textToSpeech: TextToSpeech
//    private lateinit var cameraExecutor: ExecutorService
//    private lateinit var handler: Handler
//    private var lastSpeakTime: Long = 0
//    private val speakInterval: Long = 10000
//    private var lastVolumeUpTime: Long = 0
//    private var lastVolumeDownTime: Long = 0
//    private val doublePressInterval: Long = 500 // الحد الأقصى بين الضغطتين
//    private var isSpeaking: Boolean = false
//    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
//
//    companion object {
//        private const val TAG = "BlindApp" // تغيير هذا إلى اسم مناسب للتطبيق
//        private const val REQUEST_CODE_PERMISSIONS = 10
//    }
//
//    override fun onCreate(savedBundle: Bundle?) {
//        super.onCreate(savedBundle)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this)
//        detector.setup()
//
//        textToSpeech = TextToSpeech(this) { status ->
//            if (status == TextToSpeech.SUCCESS) {
//                val result = textToSpeech.setLanguage(Locale.getDefault())
//                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    Log.e("Camera", "اللغة غير مدعومة.")
//                }
//            } else {
//                Log.e("Camera", "فشل في تهيئة TextToSpeech.")
//            }
//        }
//
//        handler = Handler(Looper.getMainLooper())
//
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                REQUIRED_PERMISSIONS,
//                REQUEST_CODE_PERMISSIONS
//            )
//        }
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
//    }
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        val currentTime = SystemClock.uptimeMillis()
//
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            if (currentTime - lastVolumeUpTime <= doublePressInterval) {
//                finish() // إنهاء التطبيق عند الضغط مرتين
//                return true
//            }
//            lastVolumeUpTime = currentTime
//        }
//
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//            if (currentTime - lastVolumeDownTime <= doublePressInterval) {
//                isSpeaking = !isSpeaking
//                if (isSpeaking) {
//                    textToSpeech.speak("تفعيل القراءة الصوتية", TextToSpeech.QUEUE_FLUSH, null, null)
//                } else {
//                    textToSpeech.speak("إيقاف القراءة الصوتية", TextToSpeech.QUEUE_FLUSH, null, null)
//                }
//                return true
//            }
//            lastVolumeDownTime = currentTime
//        }
//
//        return super.onKeyDown(keyCode, event)
//    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//            bindCameraUseCases(cameraProvider)
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
//        val rotation = binding.viewFinder.display.rotation
//        val cameraSelector = CameraSelector.Builder()
//            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//            .build()
//
//        val preview = Preview.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setTargetRotation(rotation)
//            .build()
//
//        val imageAnalyzer = ImageAnalysis.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//            .build()
//
//        imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
//            val bitmapBuffer = Bitmap.createBitmap(
//                imageProxy.width,
//                imageProxy.height,
//                Bitmap.Config.ARGB_8888
//            )
//            imageProxy.use {
//                bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)
//            }
//            imageProxy.close()
//
//            val matrix = Matrix().apply {
//                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
//            }
//
//            val rotatedBitmap = Bitmap.createBitmap(
//                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
//                matrix, true
//            )
//
//            detector.detect(rotatedBitmap)
//        }
//
//        cameraProvider.unbindAll()
//
//        try {
//            cameraProvider.bindToLifecycle(
//                this, cameraSelector, preview, imageAnalyzer
//            )
//
//            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//        } catch (exc: Exception) {
//            Log.e("Camera", "فشل في ربط الحالات", exc)
//        }
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        detector.clear()
//        cameraExecutor.shutdown()
//        textToSpeech.shutdown()
//    }
//
//    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
//        runOnUiThread {
//            binding.inferenceTime.text = "${inferenceTime}ms"
//            binding.overlay.apply {
//                setResults(boundingBoxes)
//                invalidate()
//            }
//        }
//
//        val currentTime = SystemClock.uptimeMillis()
//        if (isSpeaking && currentTime - lastSpeakTime >= speakInterval) {
//            for (boundingBox in boundingBoxes) {
//                val label = boundingBox.clsName
//                val speechText = "احترس، $label"
//                if (boundingBox.w > 0.1 && boundingBox.h > 0.1) {
//                    try {
//                        textToSpeech.speak(speechText, TextToSpeech.QUEUE_ADD, null, null)
//                        lastSpeakTime = currentTime
//                    } catch (e: Exception) {
//                        Log.e(TAG, "خطأ أثناء تشغيل TextToSpeech: ${e.message}")
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onEmptyDetect() {
//        binding.overlay.invalidate()
//    }
//}



package com.surendramaran.yolov8tflite

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.KeyEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.surendramaran.yolov8tflite.Constants.LABELS_PATH
import com.surendramaran.yolov8tflite.Constants.MODEL_PATH
import com.surendramaran.yolov8tflite.databinding.ActivityMainBinding
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.surendramaran.yolov8tflite.Detector
import com.surendramaran.yolov8tflite.BoundingBox

class MainActivity : AppCompatActivity(), Detector.DetectorListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var detector: Detector
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var handler: Handler
    private var lastSpeakTime: Long = 0
    private val speakInterval: Long = 8000
    private var lastVolumeUpTime: Long = 0
    private var lastVolumeDownTime: Long = 0
    private val doublePressInterval: Long = 500
    private var isSpeaking: Boolean = false
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    companion object {
        private const val TAG = "BlindApp"
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this)
        detector.setup()

        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("Camera", "اللغة غير مدعومة.")
                }
            } else {
                Log.e("Camera", "فشل في تهيئة TextToSpeech.")
            }
        }

        handler = Handler(Looper.getMainLooper())

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val currentTime = SystemClock.uptimeMillis()

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (currentTime - lastVolumeUpTime <= doublePressInterval) {
                finish()
                return true
            }
            lastVolumeUpTime = currentTime
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (currentTime - lastVolumeDownTime <= doublePressInterval) {
                isSpeaking = !isSpeaking
                if (isSpeaking) {
                    textToSpeech.speak("تفعيل القراءة الصوتية", TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    textToSpeech.speak("إيقاف القراءة الصوتية", TextToSpeech.QUEUE_FLUSH, null, null)
                }
                return true
            }
            lastVolumeDownTime = currentTime
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        val rotation = binding.viewFinder.display.rotation
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(rotation)
            .build()

        val imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()

        imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
            val bitmapBuffer = Bitmap.createBitmap(
                imageProxy.width,
                imageProxy.height,
                Bitmap.Config.ARGB_8888
            )
            imageProxy.use {
                bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)
            }
            imageProxy.close()

            val matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            }

            val rotatedBitmap = Bitmap.createBitmap(
                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
                matrix, true
            )

            detector.detect(rotatedBitmap)
        }

        cameraProvider.unbindAll()

        try {
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e("Camera", "فشل في ربط الحالات", exc)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.clear()
        cameraExecutor.shutdown()
        textToSpeech.shutdown()
    }

    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        runOnUiThread {
            binding.inferenceTime.text = "${inferenceTime}ms"
            binding.overlay.apply {
                setResults(boundingBoxes)
                invalidate()
            }
        }

        val currentTime = SystemClock.uptimeMillis()
        if (isSpeaking && currentTime - lastSpeakTime >= speakInterval) {
            for (boundingBox in boundingBoxes) {
                val label = boundingBox.clsName
                val speechText = "احترس، $label"
                if (boundingBox.w > 0.1 && boundingBox.h > 0.1) {
                    try {
                        textToSpeech.speak(speechText, TextToSpeech.QUEUE_ADD, null, null)
                        lastSpeakTime = currentTime
                    } catch (e: Exception) {
                        Log.e(TAG, "خطأ أثناء تشغيل TextToSpeech: ${e.message}")
                    }
                }
            }
        }
    }

    override fun onEmptyDetect() {
        binding.overlay.invalidate()
    }
}
