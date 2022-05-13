package com.purnendu.bookingsystem

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface Dao {

    @Insert
    suspend fun insertTask(form: FormModel)


    @Query("Select * from `BookingDetails`")
    fun getDetails(): LiveData<List<FormModel>>

    @Query("Update BookingDetails Set  name=:name,mail=:mail,phNo=:phNo,address=:address," +
            "country=:country,pin=:pin,checkInTime=:checkInTime,checkOutTime=:checkOutTime," +
            "adults=:adults,kids=:kids,roomPreference=:roomPreference,specialRequest=:specialRequest where id=:uId")
    suspend fun updateData(uId:Long,name:String,mail:String,phNo:String,address:String,country:String,pin:String,
    checkInTime:Long,checkOutTime:Long,adults:String,kids:String,roomPreference:String,specialRequest:String)



}