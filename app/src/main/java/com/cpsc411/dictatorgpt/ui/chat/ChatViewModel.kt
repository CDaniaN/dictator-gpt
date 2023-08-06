package com.cpsc411.dictatorgpt.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.search.SearchBar

class ChatViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Dictator says...\n\nWhat do you want?"
    }

    val text: LiveData<String> = _text
}