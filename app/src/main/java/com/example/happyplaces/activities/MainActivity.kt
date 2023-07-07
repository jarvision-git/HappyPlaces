package com.example.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.adapters.HappyPlaceAdapter
import com.example.happyplaces.database.HappyPlaceApplication
import com.example.happyplaces.database.HappyPlaceDao
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.example.happyplaces.util.SwipeToDeleteCallback
import com.example.happyplaces.util.SwipeToEditCallback
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

            val itemAdapter=HappyPlaceAdapter(this,happyPlaceList)

            binding.rvMain.layoutManager = LinearLayoutManager(this)  //important to view recyclerview
            binding.rvMain.adapter=itemAdapter
            binding.rvMain.visibility= View.VISIBLE
            binding.tvNoRecord.visibility=View.GONE

            Toast.makeText(applicationContext,"Record shown", Toast.LENGTH_LONG).show()

            itemAdapter.onItemClick = { model ->

                // do something with your item
//                Log.d("TAG", "${model.description}")
                val intent=Intent(this@MainActivity,HappyPlaceDetailActivity::class.java)
//                val bundle = Bundle()
//                bundle.putString("image",contact.image)
//                bundle.putString("desc",contact.description)
//                bundle.putString("location",contact.location)
//                intent.putExtras(bundle)
                intent.putExtra(EXTRA_PLACE_DETAILS,model)
                startActivity(intent)
            }

        }
        else{
            binding.rvMain.visibility= View.GONE
            binding.tvNoRecord.visibility=View.VISIBLE
        }

        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvMain.adapter as HappyPlaceAdapter
                adapter.notifyEditItem(
                    this@MainActivity,
                    viewHolder.adapterPosition,
                    ADD_PLACE_ACTIVITY_REQUEST_CODE
                )
            }
        }
        val editItemTouchHelper=ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding.rvMain)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvMain.adapter as HappyPlaceAdapter
                val delModel:HappyPlaceModel
                delModel=adapter.notifyDeleteItem(
                    this@MainActivity,
                    viewHolder.adapterPosition,
                    ADD_PLACE_ACTIVITY_REQUEST_CODE
                )
                lifecycleScope.launch{
                    happyPlaceDao.deleteHappyPlace(delModel)

                    Toast.makeText(
                        applicationContext, "Record deleted successfully.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
        val deleteSwipeTouchHelper=ItemTouchHelper(deleteSwipeHandler)
        deleteSwipeTouchHelper.attachToRecyclerView(binding.rvMain)

    }

    companion object{
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1

        internal const val EXTRA_PLACE_DETAILS = "extra_place_details"
    }
}

