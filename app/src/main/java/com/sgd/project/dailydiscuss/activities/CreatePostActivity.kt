package com.sgd.project.dailydiscuss.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sgd.project.dailydiscuss.R
import com.sgd.project.dailydiscuss.daos.PostDao

class CreatePostActivity : AppCompatActivity() {

    lateinit var postButton: Button
    lateinit var postInput: EditText
    lateinit var postDao: PostDao
    private val topicId by lazy { intent.getStringExtra("topicId").toString() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        postButton= findViewById(R.id.postButton)
        postInput= findViewById(R.id.postInput)
        postDao= PostDao(topicId)

        postButton.setOnClickListener {
            val textInput= postInput.text.toString().trim()
            if (textInput.isNotEmpty()){
                postDao.addPost(textInput)
                finish()
            }
            else{
                Toast.makeText(this, "Input is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}