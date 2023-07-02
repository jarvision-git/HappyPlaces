package com.example.happyplaces.activities

import android.app.DatePickerDialog
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.happyplaces.R
import com.example.happyplaces.database.HappyPlaceApplication
import com.example.happyplaces.database.HappyPlaceDao
import com.example.happyplaces.databinding.ActivityAddBinding
import com.example.happyplaces.models.HappyPlaceModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class addActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:ActivityAddBinding
    private var cal=Calendar.getInstance()
    private var mLatitude:Double=0.0
    private var mLongitude:Double=0.0
    private var saveImageToInternalStorage: Uri?=null


    private lateinit var dateSetListener:DatePickerDialog.OnDateSetListener
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            saveImageToInternalStorage=uri
            val source = ImageDecoder.createSource(this.contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            binding.ivAddImage.setImageBitmap(bitmap)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val HappyPlaceDao=(application as HappyPlaceApplication).db.dao()


        setSupportActionBar(binding.addToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val callback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(
            this, // LifecycleOwner
            callback
        )



        dateSetListener=DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDateInView()
        }
        updateDateInView() // gonna fill date on its own
        binding.etDate.setOnClickListener(this)
        binding.btnAddImg.setOnClickListener(this)
        binding.btnSave.setOnClickListener {
            when {

            binding.etTitle.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please Enter Title",Toast.LENGTH_SHORT).show()
            }
            binding.etDescription.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please Enter Description",Toast.LENGTH_SHORT).show()
            }
            binding.etLocation.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please Enter Location",Toast.LENGTH_SHORT).show()
            }
            saveImageToInternalStorage==null -> {
                Toast.makeText(this, "Please Enter an Image",Toast.LENGTH_SHORT).show()
            }
            else->{
                val happyPlaceModel=HappyPlaceModel(0,binding.etTitle.text.toString(),
                    saveImageToInternalStorage.toString(),
                    binding.etDescription.text.toString(),
                    binding.etDate.text.toString(),
                    binding.etLocation.text.toString(),
                    mLatitude,mLongitude)
                lifecycleScope.launch{
                    HappyPlaceDao.insertHappyPlace(happyPlaceModel)
                    Toast.makeText(applicationContext,"Record Saved",Toast.LENGTH_LONG).show()
                }


            }
        }

        }



    }


    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.etDate ->{
                DatePickerDialog(this@addActivity,dateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.btnAddImg -> {
                launch()

            }
            R.id.btnSave->{
//                when {
//                    binding.etTitle.text.isNullOrEmpty() -> {
//                        Toast.makeText(this, "Please Enter Title",Toast.LENGTH_SHORT).show()
//                    }
//                    binding.etDescription.text.isNullOrEmpty() -> {
//                        Toast.makeText(this, "Please Enter Description",Toast.LENGTH_SHORT).show()
//                    }
//                    binding.etLocation.text.isNullOrEmpty() -> {
//                        Toast.makeText(this, "Please Enter Location",Toast.LENGTH_SHORT).show()
//                    }
//                    saveImageToInternalStorage==null -> {
//                        Toast.makeText(this, "Please Enter an Image",Toast.LENGTH_SHORT).show()
//                    }
//                    else->{
//                        val happyPlaceModel=HappyPlaceModel(0,binding.etTitle.text.toString(),
//                            saveImageToInternalStorage.toString(),
//                            binding.etDescription.text.toString(),
//                            binding.etDate.text.toString(),
//                            binding.etLocation.text.toString(),
//                            mLatitude,mLongitude)
////                        val dbHandler=DatabaseHandler(this)
////                        val addHappyPlaceResult=dbHandler.addHappyPlace(happyPlaceModel)
//
////                        if(addHappyPlaceResult>0)
////                        {
////                            Toast.makeText(this,"The Details have been successfully inserted",Toast.LENGTH_SHORT).show()
////                            finish()
////                        }
////                        else
////                        {
////                            Log.e("Database","$addHappyPlaceResult")
////                        }
//
//                        lifecycleScope.launch{
//                            HappyPlaceDao.insertHappyPlace(happyPlaceModel)
//                            Toast.makeText(applicationContext,"Record Saved",Toast.LENGTH_LONG).show()
//                        }
//
//
//                    }
//                }
            }
        }

    }

    private fun launch() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    private fun updateDateInView(){
        val myFormat="dd.MM.yyyy"
        val sdf=SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDate.setText(sdf.format(cal.time).toString())
    }


}