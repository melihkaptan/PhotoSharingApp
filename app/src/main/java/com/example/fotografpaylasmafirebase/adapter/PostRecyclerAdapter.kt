package com.example.fotografpaylasmafirebase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fotografpaylasmafirebase.R
import com.example.fotografpaylasmafirebase.model.Post
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.post_item.view.*

class PostRecyclerAdapter(private var postList: ArrayList<Post>) :
    RecyclerView.Adapter<PostRecyclerAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.itemView.commentText.text =  postList[position].comment
        holder.itemView.userEmail.text = postList[position].email
        Picasso.get().load(postList[position].imageUrl).into(holder.itemView.imageViewPost)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

}