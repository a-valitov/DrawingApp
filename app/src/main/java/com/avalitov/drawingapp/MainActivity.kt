package com.avalitov.drawingapp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import java.lang.Exception

lateinit var drawingView : DrawingView
lateinit var ibBrush : ImageButton
lateinit var ibGallery : ImageButton
lateinit var ivBackground : ImageView
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
        ibGallery = findViewById(R.id.ib_gallery)
        llPaintColors = findViewById(R.id.ll_paint_colors)
        ivBackground = findViewById(R.id.iv_background)

        drawingView.setBrushSize(20.toFloat())

        mImageButtonCurrentPaint = llPaintColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        ibBrush.setOnClickListener{
            showBrushSizeChooserDialog()
        }

        ibGallery.setOnClickListener{
            if(isStorageReadingAllowed()){

                val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(pickPhotoIntent, GALLERY)



            } else {
                requestStoragePermission()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
                try {
                    if(data!!.data != null){
                        //setting the user's image as background
                        ivBackground.visibility = View.VISIBLE
                        ivBackground.setImageURI(data.data)
                    } else{
                        Toast.makeText(
                            this@MainActivity,
                            "Error in parsing the image or it's corrupted.",
                            Toast.LENGTH_SHORT)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
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

    private fun requestStoragePermission(){
        //if we need to explain to user why does they need that permission
        //we pass permission.ToString
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())){
            Toast.makeText(this, "You need permission to add a background image.", Toast.LENGTH_SHORT).show()
        }
        //and here we pass permissions as objects
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERMISSION_CODE) {
            if((grantResults.isNotEmpty()) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this@MainActivity,
                "Permission granted! Now you can read the storage.",
                Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@MainActivity,
                    "You have denied the permission to storage.",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isStorageReadingAllowed(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        return (result == PackageManager.PERMISSION_GRANTED)
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
    }

}