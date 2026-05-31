package com.example.data.repository

import com.example.data.database.PrayerRequestDao
import com.example.data.database.SermonOrDevotionalDao
import com.example.data.models.PrayerRequest
import com.example.data.models.SermonOrDevotional
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class SacredGroveRepository(
    private val prayerRequestDao: PrayerRequestDao,
    private val sermonOrDevotionalDao: SermonOrDevotionalDao
) {
    val publishedRequests: Flow<List<PrayerRequest>> = prayerRequestDao.getPublishedRequests()
    val pendingRequests: Flow<List<PrayerRequest>> = prayerRequestDao.getPendingRequests()
    val pendingCount: Flow<Int> = prayerRequestDao.getPendingRequestsCount()
    val allSermonsAndDevotionals: Flow<List<SermonOrDevotional>> = sermonOrDevotionalDao.getAll()
    val favoriteSermonsAndDevotionals: Flow<List<SermonOrDevotional>> = sermonOrDevotionalDao.getFavorites()

    fun getRequestsByUser(userName: String): Flow<List<PrayerRequest>> {
        return prayerRequestDao.getRequestsByUserName(userName)
    }

    suspend fun addPrayerRequest(request: PrayerRequest): Long {
        return prayerRequestDao.insertRequest(request)
    }

    suspend fun updatePrayerRequest(request: PrayerRequest) {
        prayerRequestDao.updateRequest(request)
    }

    suspend fun deletePrayerRequestById(id: Int) {
        prayerRequestDao.deleteRequestById(id)
    }

    suspend fun updateSermonOrDevotional(item: SermonOrDevotional) {
        sermonOrDevotionalDao.update(item)
    }

    suspend fun insertSermonOrDevotional(item: SermonOrDevotional) {
        sermonOrDevotionalDao.insert(item)
    }

    // Prepopulate database if absolutely empty
    suspend fun checkAndPrepopulate() {
        // Quick query as flow first element to check if empty
        val existingRequests = prayerRequestDao.getPublishedRequests().firstOrNull() ?: emptyList()
        val existingPending = prayerRequestDao.getPendingRequests().firstOrNull() ?: emptyList()
        
        if (existingRequests.isEmpty() && existingPending.isEmpty()) {
            val defaultRequests = listOf(
                // Published/Approved ones
                PrayerRequest(
                    userName = "Elena Rodriguez",
                    requestText = "Pido una oración por la salud de mi madre, quien se encuentra en recuperación tras una cirugía delicada. Que la paz del bosque la envuelva y le brinde fortaleza.",
                    category = "Salud",
                    timeAgo = "Hace poco",
                    isUrgent = true,
                    supportCount = 24,
                    userAvatar = "person",
                    isPublished = true
                ),
                PrayerRequest(
                    userName = "Mateo Salazar",
                    requestText = "Buscando guía espiritual para un nuevo camino laboral. Que la sabiduría nos ilumine a todos en tiempos de cambio.",
                    category = "Trabajo",
                    timeAgo = "Hace 2 horas",
                    supportCount = 12,
                    userAvatar = "person",
                    isPublished = true
                ),
                PrayerRequest(
                    userName = "Familia García",
                    requestText = "Damos gracias por la llegada de nuestro nuevo nieto y pedimos bendiciones para su crecimiento en esta comunidad.",
                    category = "Gratitud",
                    timeAgo = "Ayer",
                    supportCount = 45,
                    userAvatar = "family_restroom",
                    isPublished = true
                ),
                PrayerRequest(
                    userName = "Lucía M.",
                    requestText = "Por la paz en el mundo y por aquellos que se sienten solos. Que encuentren consuelo en la hermandad de nuestro sagrado bosque.",
                    category = "Paz",
                    timeAgo = "Hace 5 horas",
                    supportCount = 8,
                    userAvatar = "favorite",
                    isPublished = true
                ),
                // Maria García's requests so she sees her own requests on her profile!
                // "Mis Peticiones" section has: "Por la salud de mi abuela Elena...", "Gratitud por el nuevo comienzo laboral."
                PrayerRequest(
                    userName = "María García",
                    requestText = "Por la salud de mi abuela Elena...",
                    category = "Salud",
                    timeAgo = "Actualizado hace 2 días",
                    supportCount = 18,
                    userAvatar = "person",
                    isPublished = true
                ),
                PrayerRequest(
                    userName = "María García",
                    requestText = "Gratitud por el nuevo comienzo laboral.",
                    category = "Gratitud",
                    timeAgo = "Finalizado el 12 Oct",
                    supportCount = 5,
                    userAvatar = "person",
                    isPublished = true,
                    isArchived = false // Wait, let's keep it visible under "Mis peticiones" in profile!
                ),
                // Pending moderation requests for the Leadership Panel!
                PrayerRequest(
                    userName = "Maria González",
                    requestText = "Pido oración por la salud de mi abuelo que está en el hospital...",
                    category = "Salud",
                    timeAgo = "Hace 2 horas",
                    isPublished = false
                ),
                PrayerRequest(
                    userName = "Juan Perez",
                    requestText = "Doy gracias por el nuevo empleo y pido sabiduría...",
                    category = "Gratitud",
                    timeAgo = "Hace 5 horas",
                    isPublished = false
                )
            )
            for (req in defaultRequests) {
                prayerRequestDao.insertRequest(req)
            }
        }

        val existingSermons = sermonOrDevotionalDao.getAll().firstOrNull() ?: emptyList()
        if (existingSermons.isEmpty()) {
            val defaultSermons = listOf(
                SermonOrDevotional(
                    type = "sermon",
                    title = "La Verdad En Todo Tiempo",
                    authorOrSubtitle = "Pastor Otoniel Gómez",
                    passage = "Mateo 5:33-37",
                    description = "el Señor nos enseña a ser creyentes que hablen con verdad.",
                    duration = "24:15",
                    likesCount = 124,
                    commentsCount = 12,
                    imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b" // Forest misty trail
                ),
                SermonOrDevotional(
                    type = "devotional",
                    title = "Aprovechando el tiempo",
                    authorOrSubtitle = "Lectura Devocional",
                    passage = "1 Timoteo 4:7",
                    description = "Desecha las fábulas profanas y de viejas. Ejercítate para la piedad.",
                    likesCount = 89,
                    commentsCount = 4,
                    isSaved = true,
                    imageUrl = "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86" // Beautiful lush mossy forest floor/bark
                ),
                // In Maria's favorites: Salmo 23: La Paz
                SermonOrDevotional(
                    type = "devotional",
                    title = "Salmo 23: La Paz",
                    authorOrSubtitle = "Lectura diaria",
                    passage = "Salmo 23",
                    description = "Jehová es mi pastor; nada me faltará. En lugares de delicados pastos me hará descansar...",
                    likesCount = 312,
                    commentsCount = 28,
                    isSaved = true,
                    imageUrl = "https://images.unsplash.com/photo-1473448912268-2022ce9509d8" // Forest sunrise
                ),
                SermonOrDevotional(
                    type = "podcast",
                    title = "Whispers of the Spirit",
                    authorOrSubtitle = "Season 2, Episode 4: The Sound of Silence",
                    passage = "El Sonido del Silencio",
                    description = "Encuentra paz en el recogimiento espiritual y los murmullos de la creación.",
                    duration = "38:12",
                    likesCount = 215,
                    commentsCount = 18,
                    isSaved = false,
                    imageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e" // Sunbeams through tall trees
                ),
                SermonOrDevotional(
                    type = "devotional",
                    title = "Medita en la Palabra",
                    authorOrSubtitle = "Audio - 12 min",
                    passage = "Meditación",
                    description = "Ejercicios diarios de contemplación orante y lectio divina en el sagrado bosque.",
                    duration = "12:00",
                    likesCount = 56,
                    commentsCount = 2,
                    isSaved = true,
                    imageUrl = "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc" // Green forest leaves dew
                )
            )
            sermonOrDevotionalDao.insertAll(defaultSermons)
        }
    }
}
