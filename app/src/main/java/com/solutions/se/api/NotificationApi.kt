package com.solutions.se.api

import com.solutions.se.model.Notification
import retrofit2.http.GET

interface NotificationApi {
    @GET("notificaciones")
    suspend fun getNotificaciones(): List<Notification>
}
