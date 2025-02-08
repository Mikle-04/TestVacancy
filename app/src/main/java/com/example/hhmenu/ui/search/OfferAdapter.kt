package com.example.hhmenu.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.Offer
import com.example.hhmenu.R

class OfferAdapter(
    private val onOfferClick: (Offer) -> Unit
) : RecyclerView.Adapter<OfferAdapter.OfferViewHolder>() {

    private var offerList = emptyList<Offer>()

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitleOffer)
        val btnAction: TextView = itemView.findViewById(R.id.btnActionOffer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.offer_item, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val offer = offerList[position]

        holder.tvTitle.text = offer.title

        when (offer.id ?: "") {
            "near_vacancies" -> {
                holder.ivIcon.setImageResource(R.drawable.ic_near)
                holder.ivIcon.visibility = View.VISIBLE
            }
            "level_up_resume" -> {
                holder.ivIcon.setImageResource(R.drawable.ic_level_up)
                holder.ivIcon.visibility = View.VISIBLE
            }
            "temporary_job" -> {
                holder.ivIcon.setImageResource(R.drawable.ic_temporary)
                holder.ivIcon.visibility = View.VISIBLE
            }
            else -> {
                holder.ivIcon.visibility = View.GONE // Скрываем иконку, если id отсутствует или неизвестен
            }
        }

        if (offer.button != null) {
            holder.btnAction.text = offer.button!!.text
            holder.btnAction.visibility = View.VISIBLE
            holder.btnAction.setOnClickListener {
                onOfferClick(offer)
            }
        } else {
            holder.btnAction.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onOfferClick(offer)
        }
    }

    override fun getItemCount(): Int = offerList.size

    fun submitList(newList: List<Offer>) {
        offerList = newList
        notifyDataSetChanged()
    }
}