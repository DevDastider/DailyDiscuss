package com.sgd.project.dailydiscuss.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.sgd.project.dailydiscuss.R
import com.sgd.project.dailydiscuss.activities.TopicActivity

class AdminLoginFragment : Fragment() {

    private lateinit var userName: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?, ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName= view.findViewById(R.id.adminName)
        password= view.findViewById(R.id.adminPassword)
        loginButton= view.findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            checkAdminLoginCredentials(userName.text.toString(), password.text.toString())
        }
    }

    private fun checkAdminLoginCredentials(userName: String, password: String) {
        if (userName.isEmpty() || password.isEmpty()){
            Toast.makeText(activity, "Username/Password field empty", Toast.LENGTH_SHORT).show()
        }
        else if(userName== "admin" && password == "1234"){
            val intent= Intent(activity, TopicActivity::class.java)
            intent.putExtra("flag", "true")
            startActivity(intent)
        }
        else{
            Toast.makeText(activity, "Invalid Username/Password", Toast.LENGTH_SHORT).show()
        }
    }
}