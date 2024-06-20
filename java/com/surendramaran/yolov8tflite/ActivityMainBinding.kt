package com.surendramaran.yolov8tflite

import android.view.SurfaceView
import android.widget.TextView

data class ActivityMainBinding(
    val viewFinder: SurfaceView,
    val inferenceTime: TextView,
    val overlay: OverlayView
)
