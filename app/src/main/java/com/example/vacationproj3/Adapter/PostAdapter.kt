package com.example.vacationproj3.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vacationproj3.data.MyData
import com.example.vacationproj3.data.PostData
import com.example.vacationproj3.databinding.ItemBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.ArrayList


class PostAdapter(private val context: Context) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    //private lateinit var binding : BindingAdapter

    var data : ArrayList<PostData> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding: ItemBinding =
            ItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return PostViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size ?: 0
    }

    inner class PostViewHolder(val binding : ItemBinding ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: PostData) {
            //var currentData : PostData = data
            binding.post = data
            //Glide 통해서 사진 넣어주기
            val storage = Firebase.storage.reference
            storage.child("postImage/"+data.postImageUUID).downloadUrl.addOnSuccessListener {
                Glide.with(context).load(it).into(binding.img)
            }
            //포스트 주인이면 수정삭제 버튼 활성화 아니면 비활성화 if문 분리
            if(MyData.uid.equals(data.writerUid)) {
                binding.edit.visibility = View.VISIBLE
                binding.delet.visibility = View.VISIBLE
            }
            if(!MyData.uid.equals(data.writerUid)){
                binding.edit.visibility = View.GONE
                binding.delet.visibility = View.GONE
            }

        }
    }
}