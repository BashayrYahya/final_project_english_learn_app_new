package com.example.final_project_english_learn_app.login_register


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.final_project_english_learn_app.MainActivity2
import com.example.final_project_english_learn_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "RegisterFragment"
class RegisterFragment : Fragment() {


    private lateinit var fullName: EditText
  private lateinit var  email:EditText
   private lateinit var  password:EditText
   private lateinit var phone : EditText
   private lateinit var  registerBtn: Button
    private lateinit var goToLogin:TextView
     private var valid = true
     lateinit var fireAuth : FirebaseAuth
     lateinit var fireStore  : FirebaseFirestore
   lateinit var  isTeacher: RadioButton
   lateinit var  isStudent: RadioButton




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireAuth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()


    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_register, container, false)

        fullName = view.findViewById(R.id.registerName)
        email = view.findViewById(R.id.registerEmail)
        password = view.findViewById(R.id.password)
        phone = view.findViewById(R.id.registerPhone)
        registerBtn = view.findViewById(R.id.registerBtn)
        goToLogin = view.findViewById(R.id.logintext)
        isTeacher= view.findViewById(R.id.isTeacher)
        isStudent= view.findViewById(R.id.isStudent)




        registerBtn.setOnClickListener {
            checkField(fullName,email,password,phone)

            if(isTeacher.isClickable) {
                isStudent.display }else if (isStudent.isChecked){
                isTeacher.display
            }


            if (valid) { // new register
                fireAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {

                               val user: FirebaseUser = fireAuth.currentUser!!
                            //val user = User(id = task.user!!.uid, email = email, fullName = fullName)

                                // if register successes user's information
                                Log.d("TAG", "createUserWithEmail:success")
                                Toast.makeText(
                                    context, "account created successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            val userInfo = hashMapOf(
                                "fullName" to fullName.text.toString(),
                                "email" to email.text.toString(),
                                "phone" to phone.text.toString()
                            )

                            // specify if  the user admen
                            if (isTeacher.isChecked) {
                                userInfo["isAdmin"] = "1"

                            }
                            if (isStudent.isChecked) {
                                userInfo["isAdmin"] = "0"
                                val intent = Intent(context, MainActivity2::class.java)
                                startActivity(intent)
                            }

                                //save users data on fireBaseStore
                                    fireStore.collection("Users").document(user.uid)
                                        .set(userInfo).addOnSuccessListener {
                                            Log.e(TAG , "done")
                                        }.addOnFailureListener {
                                            Log.e(TAG , "something gone wrong" , it)
                                        }






                        } else {
                            // If register failed, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure")
                            Toast.makeText(
                                context, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
            }


        }
        goToLogin.setOnClickListener { findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }



        return view

    }


    fun checkField(fullname: EditText ,email:EditText,
                   password:EditText,phone:EditText): Boolean {
        if (fullname.text.toString().isEmpty()||(email.text.toString().isEmpty())
            || (password.text.toString().isEmpty())||(phone.text.toString().isEmpty()))
            // (isTeacher.isChecked || isStudent.isChecked)
                {
            fullname.error = "you should fill your name"
                    email.error="you should fill your email"
                    password.error = "ou should fill your password"
                    phone.error="ou should fill your phone"

            valid = false
        } else {
            valid = true
        }
        return valid
    }



}