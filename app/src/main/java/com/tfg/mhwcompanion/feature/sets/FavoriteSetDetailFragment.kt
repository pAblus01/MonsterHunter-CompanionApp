package com.tfg.mhwcompanion.feature.sets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tfg.mhwcompanion.R
import com.tfg.mhwcompanion.databinding.FragmentFavoriteSetDetailBinding
import com.tfg.mhwcompanion.feature.consulta.ArmorPieceAdapter

class FavoriteSetDetailFragment : Fragment() {

    private var _binding: FragmentFavoriteSetDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteSetDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args =  FavoriteSetDetailFragmentArgs.fromBundle(requireArguments())
        val favorite = LocalFavoriteSetStore(requireContext()).getFavorites()
            .firstOrNull { it.name == args.favoriteName }

        binding.piecesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (favorite == null) {
            binding.errorText.isVisible = true
            binding.contentGroup.isVisible = false
            binding.errorText.text = getString(R.string.favorite_not_found)
            return
        }

        binding.errorText.isVisible = false
        binding.contentGroup.isVisible = true
        binding.favoriteTitle.text = favorite.name
        binding.favoriteMeta.text = getString(
            R.string.favorite_set_meta,
            favorite.pieces.size,
            favorite.averageRarity,
            favorite.totalDefense
        )
        binding.favoriteSkills.text = favorite.topSkills.joinToString(separator = " • ").ifBlank {
            getString(R.string.armor_skills_empty)
        }
        binding.piecesRecyclerView.adapter = ArmorPieceAdapter(favorite.pieces)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}