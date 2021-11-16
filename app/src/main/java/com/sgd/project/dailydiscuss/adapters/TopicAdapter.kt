package com.sgd.project.dailydiscuss.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sgd.project.dailydiscuss.R
import com.sgd.project.dailydiscuss.daos.TopicDao
import com.sgd.project.dailydiscuss.model.Topic

class TopicAdapter(options: FirestoreRecyclerOptions<Topic>, private val listener: ITopicAdapter, private val topicDao: TopicDao) : FirestoreRecyclerAdapter<Topic, TopicAdapter.TopicViewHolder>(options) {

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val topicText: TextView= itemView.findViewById(R.id.topicText)
        val topicNPosts: TextView= itemView.findViewById(R.id.nPosts)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val viewHolder= TopicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false))
        //fetching topic id
        viewHolder.topicText.setOnClickListener {
            listener.onTopicClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int, model: Topic) {
        holder.topicText.text= model.topicName

        topicDao.findCount((snapshots.getSnapshot(holder.adapterPosition).id)).toString()
        val countValue= "${model.count} post(s)"
        holder.topicNPosts.text= countValue
    }

}
interface ITopicAdapter{
    fun onTopicClicked(topicId: String)
}
