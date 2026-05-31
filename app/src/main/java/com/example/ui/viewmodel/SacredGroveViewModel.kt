package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.SacredGroveDatabase
import com.example.data.models.PrayerRequest
import com.example.data.models.SermonOrDevotional
import com.example.data.repository.SacredGroveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Home : AppScreen()
    object Prayer : AppScreen()
    object Profile : AppScreen()
    object AdminDashboard : AppScreen()
}

class SacredGroveViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SacredGroveRepository

    val publishedRequests: StateFlow<List<PrayerRequest>>
    val pendingRequests: StateFlow<List<PrayerRequest>>
    val pendingCount: StateFlow<Int>
    val sermonsAndDevotionals: StateFlow<List<SermonOrDevotional>>
    val userFavorites: StateFlow<List<SermonOrDevotional>>
    val userRequests: StateFlow<List<PrayerRequest>>

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentUser = MutableStateFlow("María García")
    val currentUser: StateFlow<String> = _currentUser.asStateFlow()

    private val _audioPlayingState = MutableStateFlow<SermonOrDevotional?>(null)
    val audioPlayingState: StateFlow<SermonOrDevotional?> = _audioPlayingState.asStateFlow()

    private val _audioProgress = MutableStateFlow(0f) // 0.0 to 1.0 representing progress bar
    val audioProgress: StateFlow<Float> = _audioProgress.asStateFlow()

    init {
        val db = SacredGroveDatabase.getDatabase(application)
        repository = SacredGroveRepository(db.prayerRequestDao(), db.sermonOrDevotionalDao())

        // Ensure database has prepopulated content
        viewModelScope.launch {
            repository.checkAndPrepopulate()
        }

        publishedRequests = repository.publishedRequests.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        pendingRequests = repository.pendingRequests.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        pendingCount = repository.pendingCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

        sermonsAndDevotionals = repository.allSermonsAndDevotionals.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        userFavorites = repository.favoriteSermonsAndDevotionals.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        userRequests = repository.getRequestsByUser("María García").stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun togglePrayerSupport(request: PrayerRequest) = viewModelScope.launch {
        val updated = request.copy(
            isPrayedByUser = !request.isPrayedByUser,
            supportCount = if (request.isPrayedByUser) request.supportCount - 1 else request.supportCount + 1
        )
        repository.updatePrayerRequest(updated)
    }

    fun submitNewPrayerRequest(text: String, category: String, isUrgent: Boolean) = viewModelScope.launch {
        val newRequest = PrayerRequest(
            userName = _currentUser.value,
            requestText = text,
            category = category,
            isUrgent = isUrgent,
            isPublished = false, // Must be moderated by Leadership!
            timeAgo = "Hace un momento",
            userAvatar = "person"
        )
        repository.addPrayerRequest(newRequest)
    }

    fun approvePendingRequest(request: PrayerRequest) = viewModelScope.launch {
        val approved = request.copy(
            isPublished = true,
            timeAgo = "Hace un momento"
        )
        repository.updatePrayerRequest(approved)
    }

    fun archiveRequest(request: PrayerRequest) = viewModelScope.launch {
        repository.deletePrayerRequestById(request.id)
    }

    fun toggleSaveItem(item: SermonOrDevotional) = viewModelScope.launch {
        val updated = item.copy(isSaved = !item.isSaved)
        repository.updateSermonOrDevotional(updated)
    }

    fun toggleLikeItem(item: SermonOrDevotional) = viewModelScope.launch {
        val isLiked = !item.isLiked
        val updated = item.copy(
            isLiked = isLiked,
            likesCount = if (isLiked) item.likesCount + 1 else item.likesCount - 1
        )
        repository.updateSermonOrDevotional(updated)
    }

    fun toggleAudioPlaying(item: SermonOrDevotional) {
        if (_audioPlayingState.value?.id == item.id) {
            // Already active, so toggle off/on simulation or stop
            _audioPlayingState.value = null
        } else {
            _audioPlayingState.value = item
            _audioProgress.value = 0.316f // Fixed representation matching the podcast player screenshot (12:04 out of 38:12)
        }
    }

    fun updateAudioProgress(progress: Float) {
        _audioProgress.value = progress
    }

    fun triggerContentAction(actionType: String) = viewModelScope.launch {
        // Interactively simulates uploading/adding content in leadership view
        when (actionType) {
            "sermon" -> {
                val newSermon = SermonOrDevotional(
                    type = "sermon",
                    title = "El Sendero del Silencio",
                    authorOrSubtitle = "Sermón del Hermano Gabriel",
                    passage = "Lucas 5:16",
                    description = "Aprender a retirarse a lugares solitarios para encontrarse con Dios.",
                    duration = "18:40",
                    likesCount = 42,
                    commentsCount = 3,
                    imageUrl = "https://images.unsplash.com/photo-1542273917363-3b1817f69a2d" // trees foggy
                )
                repository.insertSermonOrDevotional(newSermon)
            }
            "devotional" -> {
                val newDevotional = SermonOrDevotional(
                    type = "devotional",
                    title = "Guiados por Aguas de Reposo",
                    authorOrSubtitle = "Lectura del Día",
                    passage = "Salmo 23:2",
                    description = "Medita hoy sobre cómo Jesús conduce a tu espíritu fatigado hacia verdes pastos.",
                    isSaved = false,
                    imageUrl = "https://images.unsplash.com/photo-1501854140801-50d01698950b" // landscape green
                )
                repository.insertSermonOrDevotional(newDevotional)
            }
            "video" -> {
                val newVideoSermon = SermonOrDevotional(
                    type = "sermon",
                    title = "Fe que mueve montañas",
                    authorOrSubtitle = "Culto grabados - Pastor Otoniel",
                    passage = "Marcos 11:22-25",
                    description = "Una mirada audaz a la fe inquebrantable en tiempos difíciles.",
                    duration = "45:10",
                    likesCount = 88,
                    commentsCount = 7,
                    imageUrl = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05" // green mountains
                )
                repository.insertSermonOrDevotional(newVideoSermon)
            }
        }
    }
}
