package com.tmsfalcon.device.tmsfalcon.activities.test

import com.google.gson.JsonElement
import com.tmsfalcon.device.tmsfalcon.Responses.PostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface TestDataInterface {

    @GET
    fun uploadFile(@Url url: String?):  Call<JsonElement>

}