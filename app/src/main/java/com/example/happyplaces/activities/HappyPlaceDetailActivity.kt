package com.example.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import com.example.happyplaces.models.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {
    lateinit var binding:ActivityHappyPlaceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)



        var happyplacedetailmodel:HappyPlaceModel?=null

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS))
        {
            happyplacedetailmodel=intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS)as HappyPlaceModel
        }

        if (happyplacedetailmodel!=null)
        {
            setSupportActionBar(binding.navbar)
            supportActionBar?.title=happyplacedetailmodel.title


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

            binding.ivHPImage.setImageURI(Uri.parse(happyplacedetailmodel.image))
            binding.tvDesc.text=happyplacedetailmodel.description
            binding.tvLocation.text=happyplacedetailmodel.location
        }
    }
}