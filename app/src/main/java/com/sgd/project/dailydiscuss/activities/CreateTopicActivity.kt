package com.sgd.project.dailydiscuss.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.sgd.project.dailydiscuss.R
import com.sgd.project.dailydiscuss.daos.TopicDao

class CreateTopicActivity : AppCompatActivity() {
    private lateinit var topicId: EditText
    private lateinit var topicInput: EditText
    private lateinit var addTopic: Button
    private lateinit var topicDao: TopicDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_topic)

        topicId= findViewById(R.id.topicId)
        topicInput= findViewById(R.id.topicInput)
        addTopic= findViewById(R.id.topicButton)
        topicDao= TopicDao()

        addTopic.setOnClickListener {
            val id= topicId.text.toString().trim()
            val titleInput= topicInput.text.toString().trim()
            if (id.isNotEmpty() && titleInput.isNotEmpty()){
                topicDao.addTopic(id, titleInput)
                finish()
            }
            else{
                Toast.makeText(this, "Input is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}