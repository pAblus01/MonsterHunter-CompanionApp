package com.tfg.mhwcompanion.feature.sets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tfg.mhwcompanion.R
import com.tfg.mhwcompanion.databinding.ItemArmorSetSummaryBinding
import com.tfg.mhwcompanion.domain.model.ArmorSetSummary

class ArmorSetSummaryAdapter(
    private val items: List<ArmorSetSummary>,
    private val onItemClick: (ArmorSetSummary) -> Unit
) : RecyclerView.Adapter<ArmorSetSummaryAdapter.ArmorSetSummaryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArmorSetSummaryViewHolder {
        val binding = ItemArmorSetSummaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArmorSetSummaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArmorSetSummaryViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    override fun getItemCount(): Int = items.size

    class ArmorSetSummaryViewHolder(
        private val binding: ItemArmorSetSummaryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ArmorSetSummary, onItemClick: (ArmorSetSummary) -> Unit) {
            binding.setName.text = item.name
            binding.setMeta.text = itemView.context.getString(
                R.string.sets_item_meta,
                item.pieceCount,
                item.averageRarity,
                item.totalBaseDefense
            )
            binding.setKinds.text = itemView.context.getString(
                R.string.sets_item_kinds,
                item.kinds.joinToString(separator = " · ")
            )
            binding.setSkills.text = item.topSkills.joinToString(separator = " • ").ifBlank {
                itemView.context.getString(R.string.armor_skills_empty)
            }
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }
}