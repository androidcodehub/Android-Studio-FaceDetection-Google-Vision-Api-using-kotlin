package com.androidcodehub.myapplication

import com.androidcodehub.myapplication.Camera.GraphicOverlay
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face


class GraphicFaceTrackerFactory(internal var overlay: GraphicOverlay) : MultiProcessor.Factory<Face> {
    override fun create(face: Face): Tracker<Face> {
        return GraphicFaceTracker(overlay)
    }
}