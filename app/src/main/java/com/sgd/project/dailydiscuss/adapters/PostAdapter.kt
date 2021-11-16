package com.sgd.project.dailydiscuss.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sgd.project.dailydiscuss.R
import com.sgd.project.dailydiscuss.Utils
import com.sgd.project.dailydiscuss.model.Post


//Using FirestoreRecyclerAdapter because it helps in fetching live data from Firestore.
//On the basis of options adapter fills data in recycler view.
class PostAdapter(options: FirestoreRecyclerOptions<Post>, private val listener: IPostAdapter, private val flag: String) :
    FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(options) {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder= PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent,false))
        viewHolder.likeButton.setOnClickListener {
            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.postText.text= model.text
        holder.userName.text= model.author.displayName
        Glide.with(holder.userImage.context).load(model.author.imageUrl).circleCrop().into(holder.userImage)
        holder.likeCount.text= model.likedBy.size.toString()
        holder.createdAt.text= Utils.getTimeAgo(model.timestamp)

        val auth= Firebase.auth
        //To check whether admin or user
        val currentUserId=
            if (flag == "true")
                ""
            else
                auth.currentUser!!.uid

        if (model.likedBy.contains(currentUserId)){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_liked))
        }
        else{
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_unliked))
        }
    }
}

interface IPostAdapter{
    fun onLikeClicked(postId: String)
}
