package com.tfg.mhwcompanion.feature.consulta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tfg.mhwcompanion.databinding.FragmentConsultaBinding
import com.tfg.mhwcompanion.domain.model.ArmorPiece

class ConsultaFragment : Fragment() {

    private var _binding: FragmentConsultaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sectionTitle.text = getString(com.tfg.mhwcompanion.R.string.consulta_title)
        binding.sectionDescription.text = getString(com.tfg.mhwcompanion.R.string.consulta_description)

        val armorPieces = buildMockArmorPieces()
        binding.armorRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.armorRecyclerView.adapter = ArmorPieceAdapter(armorPieces)
        binding.emptyStateText.isVisible = armorPieces.isEmpty()
    }

    private fun buildMockArmorPieces(): List<ArmorPiece> = listOf(
        ArmorPiece(
            id = 1,
            name = "Rathalos Helm Alpha+",
            rank = "Master Rank",
            rarity = 11,
            type = "Head",
            imageUrl = null
        ),
        ArmorPiece(
            id = 2,
            name = "Nargacuga Mail Beta+",
            rank = "Master Rank",
            rarity = 10,
            type = "Chest",
            imageUrl = null
        ),
        ArmorPiece(
            id = 3,
            name = "Kaiser Vambraces Beta+",
            rank = "Master Rank",
            rarity = 12,
            type = "Arms",
            imageUrl = null
        ),
        ArmorPiece(
            id = 4,
            name = "Damascus Coil Beta+",
            rank = "High Rank",
            rarity = 9,
            type = "Waist",
            imageUrl = null
        ),
        ArmorPiece(
            id = 5,
            name = "Garuga Greaves Beta+",
            rank = "Master Rank",
            rarity = 11,
            type = "Legs",
            imageUrl = null
        )
    )

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
