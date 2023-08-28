package com.jarlingwar.adminapp.domain.repositories.remote

interface IImagesRepository {
    fun deletePhotos(photos: List<String>)
}