package com.example.vacationproj3.activity.community

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vacationproj3.adapter.PostAdapter
import com.example.vacationproj3.function.Firestore
import com.example.vacationproj3.R
import com.example.vacationproj3.data.PostData
import com.example.vacationproj3.databinding.ActivityCommunityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommunityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommunityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community)
        val postAdapter = PostAdapter(this)
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = postAdapter
        CoroutineScope(Dispatchers.Main).launch {
            val dt: ArrayList<PostData> = Firestore.getPosts()
            postAdapter.data = dt
            postAdapter.notifyDataSetChanged()
        }
        binding.fab.setOnClickListener {
            startActivity(Intent(this, WriteActivity::class.java))
        }


    }

}
