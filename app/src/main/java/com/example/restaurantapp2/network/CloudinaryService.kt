package com.example.restaurantapp2.network

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object CloudinaryService {
    private const val CLOUD_NAME = "drgkgue3x"       // replace
    private const val UPLOAD_PRESET = "default_unsigned" // replace
    private const val UPLOAD_URL =
        "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"

    private val client = OkHttpClient()

    // runs on IO thread, returns the secure_url or throws
    suspend fun uploadImage(imageUri: Uri, context: Context): String =
        withContext(Dispatchers.IO) {
            val bytes = context.contentResolver
                .openInputStream(imageUri)!!
                .readBytes()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file", "upload.jpg",
                    bytes.toRequestBody("image/*".toMediaType())
                )
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build()

            val request = Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
                ?: throw Exception("Empty response from Cloudinary")

            if (!response.isSuccessful)
                throw Exception("Cloudinary error: $body")

            val json = JSONObject(body)
            json.getString("secure_url") // this is your image URL
        }
}