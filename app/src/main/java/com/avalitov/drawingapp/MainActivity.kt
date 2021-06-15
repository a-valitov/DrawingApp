package com.avalitov.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get

lateinit var drawingView : DrawingView
lateinit var ibBrush : ImageButton
lateinit var smallBtn : ImageButton
lateinit var mediumBtn : ImageButton
lateinit var largeBtn : ImageButton
lateinit var llPaintColors : LinearLayout

class MainActivity : AppCompatActivity() {

    private var mImageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawing_view)
        ibBrush = findViewById(R.id.ib_brush)
        llPaintColors = findViewById(R.id.ll_paint_colors)

        drawingView.setBrushSize(20.toFloat())

        mImageButtonCurrentPaint = llPaintColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        ibBrush.setOnClickListener{
            showBrushSizeChooserDialog()
        }
    }

    private fun showBrushSizeChooserDialog(){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size: ")

        smallBtn = brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener {
            drawingView.setBrushSize(10.toFloat())
            brushDialog.dismiss()
        }

        mediumBtn = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener {
            drawingView.setBrushSize(20.toFloat())
            brushDialog.dismiss()
        }

        largeBtn = brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener {
            drawingView.setBrushSize(30.toFloat())
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

    fun paintClicked(view: View){
        if(view !== mImageButtonCurrentPaint) {
            val imageButton = view as ImageButton

            var colorTag = imageButton.tag.toString()
            drawingView.setColor(colorTag)

            //the chosen imageButton is now pressed
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )

            //and the previous imageButton should be unpressed
            mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )
            //the chosen imageButton is set to the global variable
            mImageButtonCurrentPaint = view
        }
    }

}