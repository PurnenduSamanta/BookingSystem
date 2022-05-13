package com.purnendu.bookingsystem

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName ="BookingDetails")
data class FormModel( var name:String,
                      var mail:String,
                      var phNo:String,
                      var address:String,
                      var country:String,
                      var pin:String,
                      var checkInTime:Long,
                      var checkOutTime:Long,
                      var adults:String,
                      var kids:String,
                      var roomPreference:String,
                      var specialRequest:String,
                      @PrimaryKey(autoGenerate = true)
                      var id:Long=0)
