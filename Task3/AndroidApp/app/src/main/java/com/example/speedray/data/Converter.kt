package com.example.speedray.data

import androidx.room.TypeConverter
import java.util.Date

// Because Room can't persist data with the type date, we will need a typeConverter class

class Converter {
    @TypeConverter
    fun timestampToDate(value:Long?): Date?{
        return value?.let { Date(it) }
    }
    @TypeConverter
    fun dateToTimeStamp(date:Date?): Long?{
        return date?.time?.toLong()
    }



}