package com.sgd.project.dailydiscuss.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query
import com.sgd.project.dailydiscuss.R
import com.sgd.project.dailydiscuss.adapters.ITopicAdapter
import com.sgd.project.dailydiscuss.adapters.TopicAdapter
import com.sgd.project.dailydiscuss.daos.TopicDao
import com.sgd.project.dailydiscuss.model.Topic

class TopicActivity : AppCompatActivity(), ITopicAdapter {
    private lateinit var topicView: RecyclerView
    private lateinit var topicViewAdapter: TopicAdapter
    private lateinit var topicDao: TopicDao
    private lateinit var flag: String
    private lateinit var addTopicButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        flag= intent.getStringExtra("flag").toString()
        topicView= findViewById(R.id.topicView)
        addTopicButton= findViewById(R.id.addTopic)

        setUpTopicView()
    }

    private fun setUpTopicView() {
        //Adding + button for admin, and making invisible for user
        if (flag == "true"){
            addTopicButton.visibility= View.VISIBLE
        }

        addTopicButton.setOnClickListener {
            addTopic()
        }

        topicDao= TopicDao()
        val topicCollection= topicDao.topicCollection
        val query= topicCollection.orderBy("topicId", Query.Direction.DESCENDING)

        val recyclerViewOptions= FirestoreRecyclerOptions.Builder<Topic>().setQuery(query, Topic::class.java).build()

        topicViewAdapter= TopicAdapter(recyclerViewOptions, this, topicDao)
        topicView.adapter= topicViewAdapter
        topicView.layoutManager= LinearLayoutManager(this)
    }

    private fun addTopic() {
        val intent= Intent(this, CreateTopicActivity::class.java)
        startActivity(intent)
    }

    override fun onTopicClicked(topicId: String) {
        //Log.d("bhul", topicId)
        val intent= Intent(this, MainActivity::class.java)
        intent.putExtra("topicId", topicId)
        intent.putExtra("flag", flag)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        //Called to start listening to changes in Firestore database
        topicViewAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        //Called to stop listening to changes in Firestore database
        topicViewAdapter.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sign_out, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Creating different logout for admin & user
        if (flag == "true"){
            finish()
            startActivity(Intent(this, SignInActivity::class.java))
        }
        else
            logout()

        return true
    }

    private fun logout() {
        topicDao.auth.signOut()
        finish()
        val intent= Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

}