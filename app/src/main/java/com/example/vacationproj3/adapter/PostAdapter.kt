package com.example.vacationproj3.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vacationproj3.activity.EditActivity
import com.example.vacationproj3.function.Firestore
import com.example.vacationproj3.R
import com.example.vacationproj3.data.MyData
import com.example.vacationproj3.data.PostData
import com.example.vacationproj3.databinding.ItemBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList


class PostAdapter(private val context: Context) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    //private lateinit var binding : BindingAdapter

    var data: ArrayList<PostData> = ArrayList()
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

    inner class PostViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: PostData) {
            //var currentData : PostData = data
            binding.post = data
            //Glide 통해서 사진 넣어주기
            val storage = Firebase.storage.reference


            //val uid = db.collection("post").document(uid).toString()
            storage.child("postImages/" + data.postImageUUID).downloadUrl.addOnSuccessListener {
                Glide.with(context).load(it).into(binding.img)
            }

            //포스트 주인이면 수정삭제 버튼 활성화 아니면 비활성화 if문 분리
            if (MyData.uid.equals(data.writerUid)) {
                binding.edit.visibility = View.VISIBLE
                binding.bar2.visibility = View.VISIBLE
                binding.delete.visibility = View.VISIBLE
            }
            if (!MyData.uid.equals(data.writerUid)) {
                binding.edit.visibility = View.GONE
                binding.bar2.visibility = View.GONE
                binding.delete.visibility = View.GONE
            }

            binding.delete.setOnClickListener {//포스트삭제
                val builder = AlertDialog.Builder(context)
                builder.setTitle("경고")
                builder.setMessage("삭제하시겠습니까?")
                builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                    CoroutineScope(Dispatchers.Main).launch {
                        val result = Firestore.delPost(data.postUid)
                        if (result == true) {
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("삭제 완료")
                            builder.setMessage("삭제가 성공적으로 완료됐습니다.")
                            builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                                this@PostAdapter.data.remove(data)
                                this@PostAdapter.notifyDataSetChanged()

                            }
                            builder.show()
                        } else {
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle("삭제 오류")
                            builder.setMessage("문제가 생겼습니다. 나중에 다시 시도해주세요.")
                            builder.setPositiveButton("확인") { _: DialogInterface, _: Int ->
                            }
                            builder.show()
                        }
                    }

                }
                builder.setNegativeButton("취소") { _: DialogInterface, _: Int ->
                }
                builder.show()
            }

            binding.edit.setOnClickListener {//포스트수정
                val intent = Intent(context, EditActivity::class.java)
                intent.putExtra("text", data.text)
                intent.putExtra("postUid", data.postUid)
                context.startActivity(intent)
            }

            //좋아요버튼 초기설정
            if (data.heart.contains(MyData?.uid)) {
                binding.heartBtn.setImageResource(R.drawable.smile_blue)
            }

            binding.heartBtn.setOnClickListener {//좋아요 클릭시
                CoroutineScope(Dispatchers.Main).launch {
                    val result: Boolean? = Firestore.heartButton(data.postUid)
                    if (result == true) { //추가시
                        data.heart.add(MyData.uid)
                        binding.heart.text = data.heart.size.toString()
                        binding.heartBtn.setImageResource(R.drawable.smile_blue)
                    } else if (result == false) { //삭제시
                        data.heart.remove(MyData.uid)
                        binding.heart.text = data.heart.size.toString()
                        binding.heartBtn.setImageResource(R.drawable.smile_grey)
                    }

                }
            }

        }
    }
}