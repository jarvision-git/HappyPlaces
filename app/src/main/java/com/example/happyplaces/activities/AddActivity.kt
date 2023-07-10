package com.example.happyplaces.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.happyplaces.R
import com.example.happyplaces.database.HappyPlaceApplication
import com.example.happyplaces.database.HappyPlaceDao
import com.example.happyplaces.databinding.ActivityAddBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode


import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class addActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var placesClient: PlacesClient
    private lateinit var responseView: TextView

    private lateinit var binding:ActivityAddBinding
    private var cal=Calendar.getInstance()
    private var mLatitude:Double=0.0
    private var mLongitude:Double=0.0
    private var saveImageToInternalStorage: Uri?=null

    private var mHappyPlaceDetails: HappyPlaceModel?=null
    var id=0


    private lateinit var dateSetListener:DatePickerDialog.OnDateSetListener
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            this.contentResolver.takePersistableUriPermission(uri, flag)
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

        placesClient = Places.createClient(this)


        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }

        if(!Places.isInitialized()){
            Places.initialize(this@addActivity,resources.getString(R.string.google_maps_api_key))

        }

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS))
        {
            mHappyPlaceDetails=intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS)as HappyPlaceModel
        }

        val HappyPlaceDao=(application as HappyPlaceApplication).db.dao()


        setSupportActionBar(binding.addToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val callback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                this@addActivity.finish()
            }
        }
        onBackPressedDispatcher.addCallback(
            this, // LifecycleOwner
            callback
        )

        binding.btnLocation.setOnClickListener {

// Use fields to define the data types to return.
            val placeFields: List<Place.Field> = listOf(Place.Field.NAME,Place.Field.LAT_LNG)

// Use the builder to create a FindCurrentPlaceRequest.
            val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

                val placeResponse = placesClient.findCurrentPlace(request)
                placeResponse.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val response = task.result
                        for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {

                                Log.i(
                                    "current latitude",
                                    "Place '${placeLikelihood.place.latLng.latitude}'"
                                )
                                Log.i(
                                    "current Longitude",
                                    "Place '${placeLikelihood.place.latLng.longitude}'"
                                )
                                binding.etLocation.setText(placeLikelihood.place.name)
                                mLatitude = placeLikelihood.place.latLng.latitude
                                mLongitude = placeLikelihood.place.latLng.longitude
                                break
                        }

                    } else {
                        val exception = task.exception
                        if (exception is ApiException) {
                            Log.e("Location Status", "Place not found: ${exception.statusCode}")
                        }
                    }
                }
            } else {
                Log.e("Location Status", "Permission not found")

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
                locationPermissionRequest.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION))

            }


        }



        dateSetListener=DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDateInView()
        }
        updateDateInView() // gonna fill date on its own

        if (mHappyPlaceDetails!=null)
        {
            supportActionBar?.title="Edit Happy Place"
            binding.etTitle.setText(mHappyPlaceDetails!!.title)
            binding.etDescription.setText(mHappyPlaceDetails!!.description)
            binding.etDate.setText(mHappyPlaceDetails!!.date)
            binding.etLocation.setText(mHappyPlaceDetails!!.location)
            mLatitude=mHappyPlaceDetails!!.latitude
            mLongitude=mHappyPlaceDetails!!.longitude
            id=mHappyPlaceDetails!!.id


            saveImageToInternalStorage=Uri.parse(mHappyPlaceDetails!!.image)
            binding.ivAddImage.setImageURI(saveImageToInternalStorage)
        }
        binding.etDate.setOnClickListener(this)
        binding.btnAddImg.setOnClickListener(this)
        binding.etLocation.setOnClickListener(this)
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
//                if (mHappyPlaceDetails==null)
//                {
//                    id=0
//                }
                val happyPlaceModel=HappyPlaceModel(id,binding.etTitle.text.toString(),
                    saveImageToInternalStorage.toString(),
                    binding.etDescription.text.toString(),
                    binding.etDate.text.toString(),
                    binding.etLocation.text.toString(),
                    mLatitude,mLongitude)
                lifecycleScope.launch{
                    HappyPlaceDao.insertHappyPlace(happyPlaceModel)
                    Toast.makeText(applicationContext,"Record Saved",Toast.LENGTH_LONG).show()
                    finish( )
                }
//                mHappyPlaceDetails=null


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
            R.id.etLocation->{
                try {
                    // These are the list of fields which we required is passed
                    val fields = listOf(
                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                        Place.Field.ADDRESS
                    )
                    // Start the autocomplete intent with a unique request code.
                    val intent =
                        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(this@addActivity)
                    startAutocomplete.launch(intent);


                }catch (e:Exception)
                {
                    e.printStackTrace()
                }

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



    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    mLongitude=place.latLng.longitude
                    mLatitude=place.latLng.latitude
                    Log.i(
                        "place details:", "Place: ${place.name}, ${place.id}"
                    )
                    binding.etLocation.setText(place.address)
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i("place details:", "User canceled autocomplete")
            }
        }


    companion object{
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE=3
        private const val PERMISSION_REQUEST_CODE = 9
    }


}
