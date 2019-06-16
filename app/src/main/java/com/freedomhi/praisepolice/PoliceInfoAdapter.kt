package com.freedomhi.praisepolice

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.freedomhi.praisepolice.Police
import kotlinx.android.synthetic.main.item_police_info.view.*

class PoliceInfoAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val policeList = mutableListOf<Police>()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
	            PoliceInfoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_police_info, parent, false))
	 
    override fun getItemCount(): Int = policeList.size
 
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as PoliceInfoViewHolder
        vh.bind(policeList[position])
    }
    
    fun addPoliceInfo(police: Police) {
        policeList.add(0, police)
        notifyItemInserted(0)
    }
 
    internal inner class PoliceInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
        fun bind(police: Police) {
            itemView.pid.text = police.id
            itemView.name.text = police.name
            itemView.position.text = positionMap[police.position.toIntOrNull() ?: -1] ?: ""
        }
    }
    
    companion object {
	    val positionMap = mapOf(
				1 to "副輔警總監",
				2 to "女總督察(輔警)",
				3 to "女警司(輔警)",
				4 to "女警員",
				5 to "女警員(輔警)",
				6 to "女警署警長(輔警)",
				7 to "女警長",
				8 to "女警長(輔警)",
				9 to "女高級督察(輔警)",
				10 to "女高級警員",
				11 to "女高級警員(輔警)",
				12 to "督察(輔警)",
				13 to "總督察(輔警)",
				14 to "總警司(輔警)",
				15 to "警司(輔警)",
				16 to "警員",
				17 to "警員(輔警)",
				18 to "警署警長",
				19 to "警署警長(輔警)",
				20 to "警長",
				21 to "警長(輔警)",
				22 to "輔警總監",
				23 to "高級督察(輔警)",
				24 to "高級警司(輔警)",
				25 to "高級警員",
				26 to "高級警員(輔警)"
		)
	}
}
