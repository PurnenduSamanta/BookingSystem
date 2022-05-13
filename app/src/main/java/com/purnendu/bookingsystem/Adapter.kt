package com.purnendu.bookingsystem

import android.content.ClipData.newIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class Adapter(private val context: Context) :
    ListAdapter<FormModel, Adapter.MyViewHolder>(DiffUtil()) {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val ticketId: TextView = itemView.findViewById(R.id.ticketId)
        val name: TextView = itemView.findViewById(R.id.nameUser)
        val email: TextView = itemView.findViewById(R.id.mailUser)
        val phNo: TextView = itemView.findViewById(R.id.phNoUser)
        val address: TextView = itemView.findViewById(R.id.addressUser)
        val pin: TextView = itemView.findViewById(R.id.pinUser)

        val countrySpinner: TextView = itemView.findViewById(R.id.countryUser)
        val chekIn: TextView = itemView.findViewById(R.id.checkInUser)
        val chekOut: TextView = itemView.findViewById(R.id.checkOutUser)
        val adults: TextView = itemView.findViewById(R.id.adultsCounterUser)
        val kids: TextView = itemView.findViewById(R.id.kidsCounterUser)
        val roomPreference: TextView = itemView.findViewById(R.id.roomPreferenceUser)
        val specialRequest: TextView = itemView.findViewById(R.id.specialRequestUser)


    }


    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<FormModel>() {
        override fun areItemsTheSame(oldItem: FormModel, newItem: FormModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FormModel, newItem: FormModel): Boolean {
            return oldItem == newItem
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.single_data_layout, parent, false)
        return MyViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        getItem(position).apply outer@{

            holder.apply {

                this.ticketId.text = "Ticket Id: " + this@outer.id
                this.name.text = "Name: " + this@outer.name
                this.email.text = "Email: " + this@outer.mail
                this.phNo.text = "PhNo: " + this@outer.phNo
                this.address.text = "Address: " + this@outer.address
                this.pin.text = "Pin: " + this@outer.pin
                this.countrySpinner.text = "Country: " + this@outer.country
                this.chekIn.text =
                    "CheckIn: " + TimeFormatter.dateFormat().format(this@outer.checkInTime)
                this.chekOut.text =
                    "CheckOut: " + TimeFormatter.dateFormat().format(this@outer.checkOutTime)
                this.adults.text = "Adults: " + this@outer.adults
                this.kids.text = "Kids: " + this@outer.kids
                this.roomPreference.text = "RoomType: " + this@outer.roomPreference
                this.specialRequest.text = this@outer.specialRequest

            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)

            val id = getItem(position).id
            val name = getItem(position).name
            val email = getItem(position).mail
            val phNo = getItem(position).phNo
            val address = getItem(position).address
            val pin = getItem(position).pin
            val country = getItem(position).country
            val checkIn = getItem(position).checkInTime
            val checkOut = getItem(position).checkOutTime
            val adults = getItem(position).adults
            val kids = getItem(position).kids
            val roomType = getItem(position).roomPreference
            val specialRequest = getItem(position).specialRequest


            intent.putExtra("ID", id)
            intent.putExtra("NAME", name)
            intent.putExtra("EMAIL", email)
            intent.putExtra("PHNO", phNo)
            intent.putExtra("ADDRESS", address)
            intent.putExtra("PIN", pin)
            intent.putExtra("COUNTRY", country)
            intent.putExtra("CHECKIN", checkIn)
            intent.putExtra("CHECKOUT", checkOut)
            intent.putExtra("ADULTS", adults)
            intent.putExtra("KIDS", kids)
            intent.putExtra("ROOMTYPE", roomType)
            intent.putExtra("SPECIALREQUEST", specialRequest)
            intent.putExtra("FROMWHERE", "UPDATE")

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)

        }


    }

    fun getTaskItemId(position: Int): Long {
        return getItem(position).id
    }
}