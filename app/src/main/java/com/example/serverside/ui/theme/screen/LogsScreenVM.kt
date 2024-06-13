package com.example.serverside.ui.theme.screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serverside.repository.DbRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LogsScreenVM(private val db: DbRepository) : ViewModel() {

    private var _logs: MutableStateFlow<MutableList<Array<String>>> =
        MutableStateFlow(mutableListOf(arrayOf()))
    var logs: StateFlow<MutableList<Array<String>>> = _logs

    fun getLogs() {
        viewModelScope.launch {
            _logs.value = db.getAll()
            launch {
                _logs.value.forEach {
                    it.forEach {
                    }
                }
            }
        }
    }
}