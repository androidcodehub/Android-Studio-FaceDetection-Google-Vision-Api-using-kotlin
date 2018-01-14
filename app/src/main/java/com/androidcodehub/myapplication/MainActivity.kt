package com.androidcodehub.myapplication

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View

import com.androidcodehub.myapplication.Camera.CameraSourcePreview
import com.androidcodehub.myapplication.Camera.GraphicOverlay
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector


import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var mCameraSource: CameraSource? = null
    private var mPreview: CameraSourcePreview? = null
    private var mGraphicOverlay: GraphicOverlay? = null

    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_main)
        mPreview = findViewById<View>(R.id.preview) as CameraSourcePreview
        mGraphicOverlay = findViewById<View>(R.id.faceOverlay) as GraphicOverlay
        val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource()
        } else {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission")
        val permissions = arrayOf(Manifest.permission.CAMERA)
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM)
            return
        }

        val thisActivity = this
        val listener = View.OnClickListener {
            ActivityCompat.requestPermissions(thisActivity, permissions,
                    RC_HANDLE_CAMERA_PERM)
        }

        Snackbar.make(mGraphicOverlay!!, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show()
    }

    private fun createCameraSource() {

        val context = applicationContext
        val detector = FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build()
        detector.setProcessor(
                MultiProcessor.Builder<Face>(mGraphicOverlay?.let { GraphicFaceTrackerFactory(it) })
                        .build())

        if (!detector.isOperational) {
            Log.w(TAG, "Face detector dependencies are not yet available.")
        }
        mCameraSource = CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(10.0f)
                .build()
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        mPreview!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCameraSource != null) {
            mCameraSource!!.release()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source")
            createCameraSource()
            return
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.size +
                " Result code = " + if (grantResults.size > 0) grantResults[0] else "(empty)")

        val listener = DialogInterface.OnClickListener { dialog, id -> finish() }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show()
    }


    private fun startCameraSource() {
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS)
            dlg.show()
        }

        if (mCameraSource != null) {
            try {
                mGraphicOverlay?.let { mPreview!!.start(mCameraSource!!, it) }
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                mCameraSource!!.release()
                mCameraSource = null
            }

        }
    }

    companion object {
        private val TAG = "FaceTracker"
        private val RC_HANDLE_GMS = 9001
        private val RC_HANDLE_CAMERA_PERM = 2
    }
}
