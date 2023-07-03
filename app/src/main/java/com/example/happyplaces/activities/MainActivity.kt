package com.example.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happyplaces.adapters.HappyPlaceAdapter
import com.example.happyplaces.database.HappyPlaceApplication
import com.example.happyplaces.database.HappyPlaceDao
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.models.HappyPlaceModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val HappyPlaceDao=(application as HappyPlaceApplication).db.dao()

        binding.fabAddHappyPlace.setOnClickListener {
            val intent=Intent(this, addActivity::class.java)
            startActivity(intent)

        }

        lifecycleScope.launch{
            HappyPlaceDao.fetchAllHappyPlaces().collect(){
                val list= ArrayList(it)
                //TODO(setup list into recycler view )
                setupDataInRecyclerView(list,HappyPlaceDao)
            }

        }



    }

    private fun setupDataInRecyclerView(happyPlaceList:ArrayList<HappyPlaceModel>,happyPlaceDao:HappyPlaceDao) {
        if(happyPlaceList.isNotEmpty())
        {
            val itemAdapter=HappyPlaceAdapter(happyPlaceList)

            binding.rvMain.layoutManager = LinearLayoutManager(this)  //important to view recyclerview
            binding.rvMain.adapter=itemAdapter
            binding.rvMain.visibility= View.VISIBLE
            binding.tvNoRecord.visibility=View.GONE

            Toast.makeText(applicationContext,"Record shown", Toast.LENGTH_LONG).show()
        }
        else{
            binding.rvMain.visibility= View.GONE
            binding.tvNoRecord.visibility=View.VISIBLE
        }

    }
}