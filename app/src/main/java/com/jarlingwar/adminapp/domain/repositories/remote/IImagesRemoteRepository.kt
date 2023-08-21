package com.jarlingwar.adminapp.domain.repositories.remote

interface IImagesRemoteRepository {
    fun deletePhotos(photos: List<String>)
}