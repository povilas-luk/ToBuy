package com.example.tobuy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyTouchHelper
import com.example.tobuy.R
import com.example.tobuy.database.entity.ItemEntity
import com.example.tobuy.databinding.FragmentHomeBinding
import com.example.tobuy.ui.BaseFragment
import com.example.tobuy.ui.MainActivity

class HomeFragment : BaseFragment(), ItemEntityInterface {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            navigateViaNavGraph(R.id.action_homeFragment_to_addItemEntityFragment)
        }

        val controller = HomeEpoxyController(this)
        binding.epoxyRecyclerView.setController(controller)

        sharedViewModel.itemEntitiesLiveData.observe(viewLifecycleOwner) { itemEntityList ->
            controller.itemEntityList = itemEntityList as ArrayList<ItemEntity>
        }

        // Swiping to delete
        EpoxyTouchHelper.initSwiping(binding.epoxyRecyclerView)
            .left()
            .withTarget(HomeEpoxyController.ItemEntityEpoxyModel::class.java)
            .andCallbacks(object : EpoxyTouchHelper.SwipeCallbacks<HomeEpoxyController.ItemEntityEpoxyModel>() {

                override fun onSwipeCompleted(
                    model: HomeEpoxyController.ItemEntityEpoxyModel?,
                    itemView: View?,
                    position: Int,
                    direction: Int
                ) {
                    val itemRemoved = model?.itemEntity ?: return
                    sharedViewModel.deleteItem(itemRemoved)
                }

            })
    }

    override fun onResume() {
        super.onResume()
        //mainActivity.hideKeyboard(requireView())
    }

    override fun onDeleteItemEntity(itemEntity: ItemEntity) {
        sharedViewModel.deleteItem(itemEntity)
    }

    override fun onBumpPriority(itemEntity: ItemEntity) {
        val currentPriority = itemEntity.priority
        var newPriority = currentPriority + 1
        if (newPriority > 3) {
            newPriority = 1
        }

        val updatedItemEntity = itemEntity.copy(priority = newPriority)
        sharedViewModel.updateItem(updatedItemEntity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}