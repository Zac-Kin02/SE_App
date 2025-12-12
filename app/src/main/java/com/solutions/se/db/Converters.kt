package com.solutions.se.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.solutions.se.model.Rol

class Converters {

    @TypeConverter
    fun fromRol(value: Rol?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toRol(value: String?): Rol? {
        val type = object : TypeToken<Rol>() {}.type
        return Gson().fromJson(value, type)
    }
}