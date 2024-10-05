package com.example.disasterrecovery

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.FirebaseDatabase

class UploadWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val databaseHelper = DatabaseHelper(context)
    private val databaseReference = FirebaseDatabase.getInstance().getReference("user_profiles") // Ganti dengan path yang sesuai

    override fun doWork(): Result {
        // Ambil semua data pengguna dari database lokal
        val allData = databaseHelper.getAllUserProfiles()

        allData.forEach { userProfile ->
            // Upload data ke Firebase
            databaseReference.child(userProfile.id.toString()).setValue(userProfile)
                .addOnSuccessListener {
                    // Berhasil mengupload data
                }
                .addOnFailureListener { _: Exception ->
                    // Gagal mengupload data, bisa ditambahkan log atau penanganan lainnya
                }
        }

        return Result.success()
    }
}
