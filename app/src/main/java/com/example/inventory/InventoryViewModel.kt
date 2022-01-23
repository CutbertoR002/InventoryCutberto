package com.example.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData


class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {
    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()
    fun addNewItem(itemName: String, itemPrice: String, itemDescription: String, itemBrand: String,  itemCategory: String,  itemImage: String, itemCount: String) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemDescription, itemBrand, itemCategory, itemImage, itemCount)
        insertItem(newItem)
    }

    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    fun isEntryValid(itemName: String, itemPrice: String, itemBrand: String, itemDescription: String, itemCategory: String, itemImage: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemDescription.isBlank() || itemBrand.isBlank() || itemCategory.isBlank() || itemImage.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

    private fun getNewItemEntry(
        itemName: String,
        itemPrice: String,
        itemDescription: String,
        itemBrand: String,
        itemCategory: String,
        itemImage: String,
        itemCount: String
    ): Item {
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            itemDescription = itemDescription,
            itemBrand = itemBrand,
            itemCategory = itemCategory,
            itemImage = itemImage,
            quantityInStock = itemCount.toInt(),
        )
    }
    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }
    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }
    fun sellItem(item: Item) {
        if (item.quantityInStock > 0) {
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            updateItem(newItem)
        }
    }
    fun isStockAvailable(item: Item): Boolean {
        return (item.quantityInStock > 0)
    }
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemDescription: String,
        itemBrand: String,
        itemCategory: String,
        itemImage: String,
        itemCount: String
    ): Item {
        return Item(
            id = itemId,
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            itemDescription = itemDescription,
            itemBrand = itemBrand,
            itemCategory = itemCategory,
            itemImage = itemImage,
            quantityInStock = itemCount.toInt()
        )
    }

    fun updateItem(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemDescription: String,
        itemBrand: String,
        itemCategory: String,
        itemImage: String,
        itemCount: String
    ) {
        val updatedItem = getUpdatedItemEntry(itemId, itemName, itemPrice, itemDescription, itemBrand, itemCategory, itemImage, itemCount)
        updateItem(updatedItem)
    }
}


class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
