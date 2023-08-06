package com.cpsc411.dictatorgpt.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.search.SearchBar

class HomeViewModel : ViewModel() {
    private val _eventText = MutableLiveData<String>().apply {
        value = "Loading Events..."
    }

    private val _reminderText = MutableLiveData<String>().apply {
        value = "Loading Reminders..."
    }

    val eventText: LiveData<String> = _eventText
    val reminderText: LiveData<String> = _reminderText
}