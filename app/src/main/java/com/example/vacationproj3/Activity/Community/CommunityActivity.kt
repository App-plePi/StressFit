package com.example.vacationproj3.Activity.Community

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vacationproj3.Adapter.PostAdapter
import com.example.vacationproj3.Data.PostData
import com.example.vacationproj3.Function.Firestore
import com.example.vacationproj3.R
import com.example.vacationproj3.databinding.ActivityCommunityBinding
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommunityActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCommunityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community)
        val postAdapter = PostAdapter(this)
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = postAdapter
        CoroutineScope(Dispatchers.Main).launch {
            val dt : ArrayList<PostData> = Firestore.getPosts()
            postAdapter.data = dt
            postAdapter.notifyDataSetChanged()
        }



    }
}