package com.tfg.mhwcompanion.feature.consulta

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tfg.mhwcompanion.R
import com.tfg.mhwcompanion.databinding.ItemArmorPieceBinding
import com.tfg.mhwcompanion.domain.model.ArmorPiece

class ArmorPieceAdapter(
    private val items: List<ArmorPiece>
) : RecyclerView.Adapter<ArmorPieceAdapter.ArmorPieceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArmorPieceViewHolder {
        val binding = ItemArmorPieceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArmorPieceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArmorPieceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ArmorPieceViewHolder(
        private val binding: ItemArmorPieceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ArmorPiece) {
            binding.armorName.text = item.name
            binding.armorType.text = item.type
            binding.armorRank.text = item.rank
            binding.armorRarity.text = itemView.context.getString(
                R.string.armor_rarity_format,
                item.rarity
            )
        }
    }
}