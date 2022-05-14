package com.purnendu.bookingsystem.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.purnendu.bookingsystem.R
import com.purnendu.bookingsystem.TimeFormatter
import com.purnendu.bookingsystem.database.Database
import com.purnendu.bookingsystem.database.FormModel
import com.purnendu.bookingsystem.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    private var profileImagePath: String = ""

    private val country = arrayListOf("India", "China", "Pakistan", "Bangladesh", "USA")
    private val roomType = arrayListOf("A/C", "Non A/C")

    private var myCalendar: Calendar = Calendar.getInstance()
    private var myCalendar1: Calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var dateSetListener1: DatePickerDialog.OnDateSetListener

    private lateinit var database: Database


    //opening gallery and selecting image
    private var someActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val uri: Uri? = data.data
                val lastPathSegment = uri?.lastPathSegment
                val bitmap: Bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                } else {
                    val source = uri?.let { ImageDecoder.createSource(this.contentResolver, it) }
                    source?.let { ImageDecoder.decodeBitmap(it) }!!
                }
                CoroutineScope(Dispatchers.Main).launch { setProfileImage(bitmap, lastPathSegment) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        database = Database.getDataBase(this)


        val countryAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, country)
        binding.countrySpinner.adapter = countryAdapter


        val roomPreferenceAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roomType)
        binding.roomPreference.adapter = roomPreferenceAdapter

        fromUpdate()



        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)
            updateDateLabel()
        }

        dateSetListener1 = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            myCalendar1.set(Calendar.YEAR, year)
            myCalendar1.set(Calendar.MONTH, month)
            myCalendar1.set(Calendar.DAY_OF_MONTH, day)
            updateDateLabel1()
        }


        binding.checkIn.setOnClickListener {
            openDatePickerDialog()
        }

        binding.checkOut.setOnClickListener {
            openDatePickerDialog1()
        }



        binding.showData.setOnClickListener {
            startActivity(Intent(this@MainActivity, ShowAllData::class.java))
        }



        binding.adultPlus.setOnClickListener {

            var adultsCounter = binding.adultCounter.text.toString().toInt()
            if (adultsCounter < 10) {
                adultsCounter++
                binding.adultCounter.text = adultsCounter.toString()
            }

        }

        binding.childrenPlus.setOnClickListener {
            var kidsCounter = binding.childrenCounter.text.toString().toInt()
            if (kidsCounter < 10) {
                kidsCounter++
                binding.childrenCounter.text = kidsCounter.toString()
            }
        }

        binding.adultMinus.setOnClickListener {
            var adultsCounter = binding.adultCounter.text.toString().toInt()
            if (adultsCounter > 0) {
                adultsCounter--
                binding.adultCounter.text = adultsCounter.toString()
            }


        }

        binding.childrenMinus.setOnClickListener {

            var kidsCounter = binding.childrenCounter.text.toString().toInt()
            if (kidsCounter > 0) {
                kidsCounter--
                binding.childrenCounter.text = kidsCounter.toString()
            }
        }


        binding.profileImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            someActivityResultLauncher.launch(intent)
        }



        binding.submit.setOnClickListener {

            val userName = binding.name.text.toString()
            val userEmail = binding.email.text.toString()
            val userPhNo = binding.phoneNo.text.toString()
            val userAddress = binding.address.text.toString()

            val userPin = binding.pinCode.text.toString()
            val userCountry = binding.countrySpinner.selectedItem.toString()

            val userAdultCounter = binding.adultCounter.text.toString()
            val userKidsCounter = binding.childrenCounter.text.toString()
            val userRoomPreference = binding.roomPreference.selectedItem.toString()
            val userSpecialRequest = binding.specialRequest.text.toString()

            val userCheckIn = binding.checkIn.text.toString()
            val userCheckOut = binding.checkOut.text.toString()



            if (userName.isEmpty())
                return@setOnClickListener

            if (userEmail.isEmpty())
                return@setOnClickListener

            if (userPhNo.isEmpty())
                return@setOnClickListener

            if (userAddress.isEmpty())
                return@setOnClickListener

            if (userPin.isEmpty())
                return@setOnClickListener


            if (userSpecialRequest.isEmpty())
                return@setOnClickListener

            if (userCheckIn.isEmpty())
                return@setOnClickListener

            if (userCheckOut.isEmpty())
                return@setOnClickListener


            val intent = intent
            val fromWhere = intent.getStringExtra("FROMWHERE")

            if (profileImagePath == "")
                return@setOnClickListener

            if (fromWhere == null) {
                CoroutineScope(Dispatchers.IO).launch {
                    database.taskDao().insertTask(
                        FormModel(
                            userName,
                            userEmail,
                            userPhNo,
                            userAddress,
                            userCountry,
                            userPin,
                            myCalendar.time.time,
                            myCalendar1.time.time,
                            userAdultCounter,
                            userKidsCounter,
                            userRoomPreference,
                            userSpecialRequest,
                            profileImagePath
                        )
                    )
                }
                Toast.makeText(this@MainActivity, "Data Inserted", Toast.LENGTH_SHORT).show()


            } else if (fromWhere == "UPDATE") {
                val id = intent.getLongExtra("ID", -1)
                CoroutineScope(Dispatchers.IO).launch {

                    database.taskDao().updateData(
                        id,
                        userName,
                        profileImagePath,
                        userEmail,
                        userPhNo,
                        userAddress,
                        userCountry,
                        userPin,
                        myCalendar.time.time,
                        myCalendar1.time.time,
                        userAdultCounter,
                        userKidsCounter,
                        userRoomPreference,
                        userSpecialRequest
                    )


                }
                Toast.makeText(this@MainActivity, "Data Updated", Toast.LENGTH_SHORT).show()

            }

            binding.apply {
                name.text.clear()
                email.text.clear()
                phoneNo.text.clear()
                address.text.clear()
                pinCode.text.clear()
                checkIn.text.clear()
                checkOut.text.clear()
                specialRequest.text.clear()
                profileImage.setImageResource(R.drawable.ic_baseline_add_24)
            }


        }


    }

    private fun fromUpdate() {

        val intent = intent

        intent.getStringExtra("FROMWHERE") ?: return


        val nameV = intent.getStringExtra("NAME")
        val profileImageV = intent.getStringExtra("PROFILEIMAGE")
        val emailV = intent.getStringExtra("EMAIL")
        val phNoV = intent.getStringExtra("PHNO")
        val addressV = intent.getStringExtra("ADDRESS")
        val pinV = intent.getStringExtra("PIN")
        val checkInV = intent.getLongExtra("CHECKIN", 0)
        val checkOutV = intent.getLongExtra("CHECKOUT", 0)
        val adultsV = intent.getStringExtra("ADULTS")
        val kidsV = intent.getStringExtra("KIDS")
        val specialRequestV = intent.getStringExtra("SPECIALREQUEST")
        val selectedCountry = intent.getStringExtra("COUNTRY")
        val preference = intent.getStringExtra("ROOMTYPE")

        if (profileImageV != null) {
            profileImagePath=profileImageV
        }

        binding.apply {
            name.setText(nameV)
            email.setText(emailV)
            phoneNo.setText(phNoV)
            address.setText(addressV)
            pinCode.setText(pinV)
            checkIn.setText(TimeFormatter.dateFormat().format(checkInV))
            checkOut.setText(TimeFormatter.dateFormat().format(checkOutV))
            adultCounter.text = adultsV
            childrenCounter.text = kidsV
            specialRequest.setText(specialRequestV)
        }


        val imgFile = profileImageV?.let { File(it) }
        if (imgFile != null) {
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                binding.profileImage.setImageBitmap(myBitmap)
            }
        }

        val countryPositionInArray = country.indexOf(selectedCountry)
        val preferencePositionInArray = roomType.indexOf(preference)

        binding.countrySpinner.setSelection(countryPositionInArray)
        binding.roomPreference.setSelection(preferencePositionInArray)


    }


    private fun openDatePickerDialog() {
        val dialog = DatePickerDialog(
            this,
            dateSetListener,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.minDate = System.currentTimeMillis()
        dialog.show()
    }

    private fun openDatePickerDialog1() {
        val dialog = DatePickerDialog(
            this,
            dateSetListener1,
            myCalendar1.get(Calendar.YEAR),
            myCalendar1.get(Calendar.MONTH),
            myCalendar1.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.minDate = System.currentTimeMillis()
        dialog.show()
    }

    private fun updateDateLabel() {
        binding.checkIn.setText(TimeFormatter.dateFormat().format(myCalendar.time))
    }

    private fun updateDateLabel1() {
        binding.checkOut.setText(TimeFormatter.dateFormat().format(myCalendar1.time))
    }

    private suspend fun setProfileImage(bitmap: Bitmap, lastPathSegment: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            profileImagePath = savingImageInFile(bitmap, lastPathSegment)
        }.join()
        binding.profileImage.setImageBitmap(bitmap)
    }

    private fun savingImageInFile(bitmap: Bitmap, lastPathSegment: String?): String {

        val cw = ContextWrapper(applicationContext)

        val directory: File = cw.getDir("ProfileImageDirectory", Context.MODE_PRIVATE)
        // Create imageDir path to /data/data/BookingSystem/app_data/ProfileImageDirectory
        val myPath = File(directory, "$lastPathSegment.jpg")

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(myPath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return myPath.absolutePath
    }
}