package com.example.happyplaces.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.happyplaces.R
import com.example.happyplaces.models.HappyPlaceModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(),OnMapReadyCallback {

    private var mHappyPlaceDetail:HappyPlaceModel?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        try{
            super.onCreate(savedInstanceState)

            setContentView(R.layout.activity_map)
        }
        catch (e:Exception) {
            Log.e("Error report :", "onCreateView", e);
            throw e;
        }


        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            mHappyPlaceDetail=intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel //as HappyPlaceModel converts serializableExtra into HappyPlaceModel
        }

        if(mHappyPlaceDetail!=null)
        {
            val supportMapFragment:SupportMapFragment=supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            supportMapFragment.getMapAsync(this)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        val position= LatLng(mHappyPlaceDetail!!.latitude,mHappyPlaceDetail!!.longitude)
        //displaying marker
        map.addMarker(MarkerOptions().position(position).title(mHappyPlaceDetail!!.location))
        //zooming onto the marker
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position,15f))


    }


}