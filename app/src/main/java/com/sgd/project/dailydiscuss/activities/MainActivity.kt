package com.sgd.project.dailydiscuss.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query
import com.sgd.project.dailydiscuss.R
import com.sgd.project.dailydiscuss.adapters.IPostAdapter
import com.sgd.project.dailydiscuss.adapters.PostAdapter
import com.sgd.project.dailydiscuss.daos.PostDao
import com.sgd.project.dailydiscuss.model.Post

class MainActivity : AppCompatActivity(), IPostAdapter {
    private lateinit var addPostButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: PostAdapter
    private lateinit var postDao: PostDao
    private val topicId by lazy { intent.getStringExtra("topicId").toString() }
    private lateinit var flag: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flag= intent.getStringExtra("flag").toString()
        addPostButton= findViewById(R.id.addPost)
        recyclerView= findViewById(R.id.recyclerView)

        //If logged in as admin
        if (flag=="true")
            addPostButton.visibility= View.GONE

        addPostButton.setOnClickListener {
            val intent= Intent(this, CreatePostActivity::class.java).apply {
                putExtra("topicId", topicId)
            }
            startActivity(intent)
        }

        setUpRecyclerView()
        supportActionBar!!.title=
            if (flag== "true")
                "Hi Admin"
            else
                "Hi ${postDao.auth.currentUser!!.displayName!!.trim()}"
    }

    override fun onStart() {
        super.onStart()
        //Called to start listening to changes in Firestore database
        recyclerViewAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        //Called to stop listening to changes in Firestore database
        recyclerViewAdapter.stopListening()
    }

    private fun setUpRecyclerView() {
        postDao= PostDao(topicId)
        val postsCollection= postDao.postCollections
        //Creating options to fetch data from Firestore document collection.
        val query= postsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
        val recyclerViewOptions= FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        recyclerViewAdapter= PostAdapter(recyclerViewOptions, this, flag)   //Adding flag for checking admin or user

        recyclerView.adapter= recyclerViewAdapter
        recyclerView.layoutManager= LinearLayoutManager(this)
    }

    override fun onLikeClicked(postId: String) {
        //Checking for admin or user
        if (flag=="false")
            postDao.updateLikes(postId)
        else
            Toast.makeText(this, "Not allowed", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sign_out, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (flag == "true"){
            finish()
            startActivity(Intent(this, SignInActivity::class.java))
        }
        else
            logout()

        return true
    }

    private fun logout() {
        postDao.auth.signOut()
        finish()
        val intent= Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }
}