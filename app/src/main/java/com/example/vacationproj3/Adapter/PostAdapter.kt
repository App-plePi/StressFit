package com.example.vacationproj3.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vacationproj3.Activity.Community.CommunityActivity
import com.example.vacationproj3.Activity.EditActivity
import com.example.vacationproj3.Activity.MainActivity
import com.example.vacationproj3.Function.Firestore
import com.example.vacationproj3.data.MyData
import com.example.vacationproj3.data.PostData
import com.example.vacationproj3.databinding.ItemBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.components.ComponentRuntime
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            val uid = MyData.uid.toString()
            storage.child("postImages/"+data.postImageUUID).downloadUrl.addOnSuccessListener {
                Glide.with(context).load(it).into(binding.img)
            }
            //포스트 주인이면 수정삭제 버튼 활성화 아니면 비활성화 if문 분리
            if(MyData.uid.equals(data.writerUid)) {
                binding.edit.visibility = View.VISIBLE
                binding.delete.visibility = View.VISIBLE
            }
            if(!MyData.uid.equals(data.writerUid)){
                binding.edit.visibility = View.GONE
                binding.delete.visibility = View.GONE
            }
            binding.delete.setOnClickListener{
                CoroutineScope(Dispatchers.Main).launch {
                    val result = Firestore.delPost(uid)
                    if (result == true) {
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("삭제 완료")
                    } else {
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("삭제 오류")
                    }
                }

            }

            binding.edit.setOnClickListener{
                context.startActivity(Intent(context, EditActivity::class.java))
            }

            binding.heartBtn.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    val result : Boolean? = Firestore.heartButton(uid)
                    if (result==true){
                        binding.heartBtn.setBackgroundColor(Color.parseColor("#000000"))
                        Toast.makeText(context, "추가",Toast.LENGTH_SHORT).show()
                    }
                    else if (result == false){
                        binding.heartBtn.setBackgroundColor(Color.parseColor("#D3D3D3"))
                        Toast.makeText(context, "삭제",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(context, "오류",Toast.LENGTH_SHORT).show()
                    }

                }
            }

        }
    }
}