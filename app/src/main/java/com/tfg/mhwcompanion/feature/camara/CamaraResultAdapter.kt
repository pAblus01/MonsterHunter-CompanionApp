package com.tfg.mhwcompanion.feature.camara

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tfg.mhwcompanion.R
import com.tfg.mhwcompanion.databinding.ItemCameraDetectionBinding

class CamaraResultAdapter(
    private val items: List<DetectedArmorSlotUiModel>
) : RecyclerView.Adapter<CamaraResultAdapter.CamaraResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CamaraResultViewHolder {
        val binding = ItemCameraDetectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CamaraResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CamaraResultViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class CamaraResultViewHolder(
        private val binding: ItemCameraDetectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DetectedArmorSlotUiModel) {
            binding.slotTitle.text = item.slotType.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }
            binding.slotPrimary.text = item.armorName ?: item.primaryLabel
            binding.slotMeta.text = binding.root.context.getString(
                R.string.camara_result_meta,
                (item.confidence * 100).toInt(),
                item.rarity ?: 0,
                item.defense ?: 0
            )
            binding.slotSet.text = item.armorSetName ?: binding.root.context.getString(R.string.armor_set_unknown)
            binding.slotAlternatives.text = item.alternatives.ifEmpty {
                listOf(binding.root.context.getString(R.string.camara_no_alternatives))
            }.joinToString()
        }
    }
}
