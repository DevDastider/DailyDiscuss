package com.sgd.project.dailydiscuss.daos

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sgd.project.dailydiscuss.model.Post
import com.sgd.project.dailydiscuss.model.Topic
import com.sgd.project.dailydiscuss.model.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TopicDao {
    private val db= FirebaseFirestore.getInstance()
    val auth= Firebase.auth
    val topicCollection= db.collection("topics")

    fun addTopic(id:String, text: String){
        GlobalScope.launch {
            val topic= Topic(id, text)

            //Adding post in our Firestore collection
            topicCollection.document().set(topic)
        }
    }

    fun findCount(topicId: String){
        var count= 0

        GlobalScope.launch {
            topicCollection.document(topicId).collection("posts").get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        count= it.result.size()
                        Log.d("bhul", count.toString())
                    }
                }

            val topic= topicCollection.document(topicId).get().await().toObject(Topic::class.java)
            topic?.count= count
            topicCollection.document(topicId).set(topic!!)
        }
        /*Log.d("bhul", "returning $count")
        return count*/
    }
}