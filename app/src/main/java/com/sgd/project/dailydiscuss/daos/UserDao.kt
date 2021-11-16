package com.sgd.project.dailydiscuss.daos

import android.util.Patterns
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sgd.project.dailydiscuss.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//The function of this class is to add the user entry in the users database

class UserDao {

    // Getting access of our Firestore database
    private val db= FirebaseFirestore.getInstance()
    //Creating the collection if not created. If created then getting access of it.
    private val usersCollection= db.collection("users")

    //Storing user details in our Firestore database
    fun addUser(user: User?){
        if (user!=null){
            GlobalScope.launch(Dispatchers.IO) {
                //Setting the values of the document with id as uid in users collection
                usersCollection.document(user.uid).set(user)
            }
        }
    }

    //This is called to get the details of our user in the form of document of users collection
    fun getUserById(uId:String): Task<DocumentSnapshot> {
        return usersCollection.document(uId).get()
    }
}
