package com.example.disasterrecovery

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.example.disasterrecovery.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit
import com.google.firebase.database.FirebaseDatabase // Pastikan ini ada


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseHelper: DatabaseHelper
    private val databaseReference = FirebaseDatabase.getInstance().getReference("user_profiles") // Ganti dengan path yang sesuai

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        binding.btnSaveLocal.setOnClickListener {
            saveToLocalDatabase()
        }

        // Schedule worker for auto-upload when internet is available
        scheduleUploadWorker()
    }

    private fun saveToLocalDatabase() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val photo = "" // Could be a photo URI

        // Insert user profile into local database
        val result = databaseHelper.insertUserProfile(name, email, photo)
        if (result > 0) {
            Toast.makeText(this, "Data saved locally", Toast.LENGTH_SHORT).show()
            // Call the function to upload data immediately
            uploadDataToFirebase()
        } else {
            Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadDataToFirebase() {
        val allData = databaseHelper.getAllUserProfiles()

        allData.forEach { userProfile ->
            // Upload data to Firebase
            databaseReference.child(userProfile.id.toString()).setValue(userProfile)
                .addOnSuccessListener {
                    // Successfully uploaded data
                }
                .addOnFailureListener { _: Exception ->
                    // Failed to upload data
                }
        }
    }

    private fun scheduleUploadWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest = PeriodicWorkRequestBuilder<UploadWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(uploadWorkRequest)
    }
}
