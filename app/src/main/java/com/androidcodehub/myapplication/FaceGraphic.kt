package com.androidcodehub.myapplication

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log

import com.androidcodehub.myapplication.Camera.FaceTrackingListener
import com.androidcodehub.myapplication.Camera.GraphicOverlay
import com.google.android.gms.vision.face.Face


internal class FaceGraphic(overlay: GraphicOverlay) : GraphicOverlay.Graphic(overlay) {
    private val mFacePositionPaint: Paint
    private val mIdPaint: Paint
    private val mBoxPaint: Paint
    @Volatile private var mFace: Face? = null
    private var mFaceId: Int = 0
    private val mFaceHappiness: Float = 0.toFloat()

    init {

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.size
        val selectedColor = COLOR_CHOICES[mCurrentColorIndex]
        mFacePositionPaint = Paint()
        mFacePositionPaint.color = selectedColor
        mIdPaint = Paint()
        mIdPaint.color = selectedColor
        mIdPaint.textSize = ID_TEXT_SIZE
        mBoxPaint = Paint()
        mBoxPaint.color = selectedColor
        mBoxPaint.style = Paint.Style.STROKE
        mBoxPaint.strokeWidth = BOX_STROKE_WIDTH
    }

    fun setId(id: Int) {
        mFaceId = id
    }

    fun updateFace(face: Face) {
        mFace = face
        postInvalidate()
        logFaceData(mFace, object : FaceTrackingListener {
            override fun onFaceLeftMove() {

            }

            override fun onFaceRightMove() {

            }

            override fun onFaceUpMove() {

            }

            override fun onFaceDownMove() {

            }

            override fun onGoodSmile() {

            }

            override fun onEyeCloseError() {

            }

            override fun onMouthOpenError() {

            }

            override fun onMultipleFaceError() {

            }
        })
    }

    override fun draw(canvas: Canvas) {
        val face = mFace ?: return
        val x = translateX(face.position.x + face.width / 2)
        val y = translateY(face.position.y + face.height / 2)
        Log.e("Y", "" + y)
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint)
        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint)
        /* canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
       */ canvas.drawText("left eye: " + String.format("%.2f", face.isLeftEyeOpenProbability), x - ID_X_OFFSET * 2, y - ID_Y_OFFSET * 2, mIdPaint)
        val xOffset = scaleX(face.width / 2.0f)
        val yOffset = scaleY(face.height / 2.0f)
        val left = x - xOffset
        val top = y - yOffset
        val right = x + xOffset
        val bottom = y + yOffset
        val EulerY = mFace!!.eulerY
        val EulerZ = mFace!!.eulerZ
        canvas.drawText("Euler Y: " + String.format("%.2f", EulerY), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint)
        canvas.drawText("Euler Z: " + String.format("%.2f", EulerZ), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint)
        Log.e("Right", "" + right)
        Log.e("Left", "" + left)
        Log.e("Top", "" + top)
        Log.e("Bottom", "" + bottom)
        canvas.drawRect(left, top, right, bottom, mBoxPaint)
    }

    private fun logFaceData(mFaces: Face?, listener: FaceTrackingListener) {
        val smilingProbability: Float
        val leftEyeOpenProbability: Float
        val rightEyeOpenProbability: Float
        val eulerY: Float
        val eulerZ: Float

        smilingProbability = mFaces!!.isSmilingProbability
        leftEyeOpenProbability = mFaces.isLeftEyeOpenProbability
        rightEyeOpenProbability = mFaces.isRightEyeOpenProbability
        eulerY = mFaces.eulerY
        eulerZ = mFaces.eulerZ
        /* Log.e( "Tuts+ Face Detection", "Smiling: " + smilingProbability );
            Log.e( "Tuts+ Face Detection", "Left eye open: " + leftEyeOpenProbability );
            Log.e( "Tuts+ Face Detection", "Right eye open: " + rightEyeOpenProbability );
            Log.e( "Tuts+ Face Detection", "Euler Y: " + eulerY );*/
        Log.e("Tuts+ Face Detection", "Euler Z: " + eulerZ)

    }

    companion object {
        private val FACE_POSITION_RADIUS = 10.0f
        private val ID_TEXT_SIZE = 40.0f
        private val ID_Y_OFFSET = 50.0f
        private val ID_X_OFFSET = -50.0f
        private val BOX_STROKE_WIDTH = 5.0f
        private val COLOR_CHOICES = intArrayOf(Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW)
        private var mCurrentColorIndex = 0
    }
}
