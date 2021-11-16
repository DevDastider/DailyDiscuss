package com.sgd.project.dailydiscuss.daos


import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sgd.project.dailydiscuss.model.Post
import com.sgd.project.dailydiscuss.model.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao(val topicId: String) {

    private val db= FirebaseFirestore.getInstance()
    val postCollections= db.collection("topics").document(topicId).collection("posts")
    //To get author of post
    val auth= Firebase.auth

    //To add posts in our database
    fun addPost(text:String){
        /*Using not-null asserted call to ensure if there is no user but still some script is trying to
        use addPost function.*/
        //To fetch current user
        val currentUserId= auth.currentUser!!.uid

        GlobalScope.launch {
            //We are parsing the user details from the document form in Firestore to our user object
            val user= UserDao().getUserById(currentUserId).await().toObject(User::class.java)!!

            val currentTime= System.currentTimeMillis()
            val post= Post(text, user, currentTime)

            //Adding post in our Firestore collection
            postCollections.document().set(post)
        }
    }

    private fun getPostById(postId: String): Task<DocumentSnapshot> {
        return postCollections.document(postId).get()
    }

    fun updateLikes(postId: String){
        GlobalScope.launch {
            val currentUser= auth.currentUser!!.uid
            val post= getPostById(postId).await().toObject(Post::class.java)

            if (post!!.likedBy.contains(currentUser)){
                post.likedBy.remove(currentUser)
            }
            else{
                post.likedBy.add(currentUser)
            }
            //Updating post
            postCollections.document(postId).set(post)
        }

    }
}