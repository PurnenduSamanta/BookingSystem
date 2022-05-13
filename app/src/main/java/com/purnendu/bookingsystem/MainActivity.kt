package com.purnendu.bookingsystem

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var phNo: EditText
    private lateinit var address: EditText
    private lateinit var pin: EditText
    private lateinit var countrySpinner: Spinner
    private lateinit var chekIn: EditText
    private lateinit var chekOut: EditText
    private lateinit var adults: TextView
    private lateinit var adultsMinus: FloatingActionButton
    private lateinit var adultsPlus:  FloatingActionButton
    private lateinit var kids: TextView
    private lateinit var kidsMinus: FloatingActionButton
    private lateinit var kidsPlus:  FloatingActionButton
    private lateinit var roomPreference: Spinner
    private lateinit var specialRequest: EditText
    private lateinit var submit: Button
    private lateinit var showData: Button

    private val country = arrayListOf("India", "China", "Pakistan", "Bangladesh", "USA")
    private val roomType = arrayListOf("A/C", "Non A/C")


    private var myCalendar: Calendar = Calendar.getInstance()
    private var myCalendar1: Calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var dateSetListener1: DatePickerDialog.OnDateSetListener


    private lateinit var database: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = Database.getDataBase(this)

        initialization()


        val countryAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, country)
        countrySpinner.adapter = countryAdapter


        val roomPreferenceAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roomType)
        roomPreference.adapter = roomPreferenceAdapter

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


        chekIn.setOnClickListener {
            openDatePickerDialog()
        }

        chekOut.setOnClickListener {
            openDatePickerDialog1()
        }



        showData.setOnClickListener {
            startActivity(Intent(this@MainActivity, ShowAllData::class.java))
        }



        adultsPlus.setOnClickListener {

            var adultsCounter = adults.text.toString().toInt()
            if (adultsCounter < 10) {
                adultsCounter++
                adults.text = adultsCounter.toString()
            }

        }

        kidsPlus.setOnClickListener {
            var kidsCounter = kids.text.toString().toInt()
            if (kidsCounter < 10) {
                kidsCounter++
                kids.text = kidsCounter.toString()
            }
        }

        adultsMinus.setOnClickListener {
            var adultsCounter = adults.text.toString().toInt()
            if (adultsCounter > 0) {
                adultsCounter--
                adults.text = adultsCounter.toString()
            }


        }

        kidsMinus.setOnClickListener {

            var kidsCounter = kids.text.toString().toInt()
            if (kidsCounter > 0) {
                kidsCounter--
                kids.text = kidsCounter.toString()
            }
        }



        submit.setOnClickListener {


            val userName = name.text.toString()
            val userEmail = email.text.toString()
            val userPhNo = phNo.text.toString()
            val userAddress = address.text.toString()

            val userPin = pin.text.toString()
            val userCountry = countrySpinner.selectedItem.toString()

            val userAdultCounter = adults.text.toString()
            val userKidsCounter = kids.text.toString()
            val userRoomPreference = roomPreference.selectedItem.toString()
            val userSpecialRequest = specialRequest.text.toString()

            val userCheckIn = chekIn.text.toString()
            val userCheckOut = chekOut.text.toString()



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
                            userSpecialRequest
                        )
                    )

                }.onJoin



                Toast.makeText(this@MainActivity, "Data Inserted", Toast.LENGTH_SHORT).show()


            } else if (fromWhere == "UPDATE") {
                val id = intent.getLongExtra("ID",-1)
                CoroutineScope(Dispatchers.IO).launch {

                    database.taskDao().updateData(
                            id,
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
                            userSpecialRequest
                        )


                }.onJoin
                Toast.makeText(this@MainActivity, "Data Updated", Toast.LENGTH_SHORT).show()

            }

            name.text.clear()
            email.text.clear()
            phNo.text.clear()
            address.text.clear()
            pin.text.clear()
            chekIn.text.clear()
            chekOut.text.clear()
            specialRequest.text.clear()



        }


    }

    private fun fromUpdate() {

        val intent = intent

        intent.getStringExtra("FROMWHERE") ?: return

        val nameV = intent.getStringExtra("NAME")
        val emailV = intent.getStringExtra("EMAIL")
        val phNoV = intent.getStringExtra("PHNO")
        val addressV = intent.getStringExtra("ADDRESS")
        val pinV = intent.getStringExtra("PIN")
        val checkInV = intent.getLongExtra("CHECKIN",0)
        val checkOutV = intent.getLongExtra("CHECKOUT",0)
        val adultsV = intent.getStringExtra("ADULTS")
        val kidsV = intent.getStringExtra("KIDS")
        val specialRequestV = intent.getStringExtra("SPECIALREQUEST")
        val selectedCountry = intent.getStringExtra("COUNTRY")
        val preference = intent.getStringExtra("ROOMTYPE")

        name.setText(nameV)
        email.setText(emailV)
        phNo.setText(phNoV)
        address.setText(addressV)
        pin.setText(pinV)
        chekIn.setText(TimeFormatter.dateFormat().format(checkInV))
        chekOut.setText(TimeFormatter.dateFormat().format(checkOutV))
        adults.text = adultsV
        kids.text = kidsV
        specialRequest.setText(specialRequestV)

        val countryPositionInArray= country.indexOf(selectedCountry)
        val preferencePositionInArray=roomType.indexOf(preference)

        countrySpinner.setSelection(countryPositionInArray)
        roomPreference.setSelection(preferencePositionInArray)


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
        chekIn.setText(TimeFormatter.dateFormat().format(myCalendar.time))
    }

    private fun updateDateLabel1() {
        chekOut.setText(TimeFormatter.dateFormat().format(myCalendar1.time))
    }

    private fun initialization() {

        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        phNo = findViewById(R.id.phoneNo)
        address = findViewById(R.id.address)
        pin = findViewById(R.id.pinCode)
        countrySpinner = findViewById(R.id.countrySpinner)
        chekIn = findViewById(R.id.checkIn)
        chekOut = findViewById(R.id.checkOut)
        adults = findViewById(R.id.adultCounter)
        adultsMinus = findViewById(R.id.adultMinus)
        adultsPlus = findViewById(R.id.adultPlus)
        kids = findViewById(R.id.childrenCounter)
        kidsMinus = findViewById(R.id.childrenMinus)
        kidsPlus = findViewById(R.id.childrenPlus)
        roomPreference = findViewById(R.id.roomPreference)
        specialRequest = findViewById(R.id.specialRequest)
        submit = findViewById(R.id.submit)
        showData = findViewById(R.id.showData)
    }
}