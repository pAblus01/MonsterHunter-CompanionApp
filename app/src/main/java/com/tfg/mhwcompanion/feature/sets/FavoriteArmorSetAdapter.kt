package com.tfg.mhwcompanion.feature.sets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tfg.mhwcompanion.R
import com.tfg.mhwcompanion.databinding.ItemFavoriteSetBinding
import com.tfg.mhwcompanion.domain.model.FavoriteArmorSet

class FavoriteArmorSetAdapter(
    private val items: List<FavoriteArmorSet>,
    private val onOpen: (FavoriteArmorSet) -> Unit,
    private val onEdit: (FavoriteArmorSet) -> Unit,
    private val onDelete: (FavoriteArmorSet) -> Unit
) : RecyclerView.Adapter<FavoriteArmorSetAdapter.FavoriteArmorSetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteArmorSetViewHolder {
        val binding = ItemFavoriteSetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteArmorSetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteArmorSetViewHolder, position: Int) {
        holder.bind(items[position], onOpen, onEdit, onDelete)
    }

    override fun getItemCount(): Int = items.size

    class FavoriteArmorSetViewHolder(
        private val binding: ItemFavoriteSetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: FavoriteArmorSet,
            onOpen: (FavoriteArmorSet) -> Unit,
            onEdit: (FavoriteArmorSet) -> Unit,
            onDelete: (FavoriteArmorSet) -> Unit
        ) {
            binding.favoriteName.text = item.name
            binding.favoritePieces.text = listOfNotNull(
                item.head?.name,
                item.chest?.name,
                item.arms?.name,
                item.waist?.name,
                item.legs?.name
            ).joinToString(separator = "\n")
            binding.favoriteMeta.text = itemView.context.getString(
                R.string.favorite_set_meta,
                item.pieces.size,
                item.averageRarity,
                item.totalDefense
            )
            binding.root.setOnClickListener { onOpen(item) }
            binding.editFavoriteButton.setOnClickListener { onEdit(item) }
            binding.deleteFavoriteButton.setOnClickListener { onDelete(item) }
        }
    }
}