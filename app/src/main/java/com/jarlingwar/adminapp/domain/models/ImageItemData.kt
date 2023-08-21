package com.jarlingwar.adminapp.domain.models

import android.net.Uri

data class ImageItemData(
    val id: String,
    //local images
    val imageUri: Uri? = null,
    //remote images
    val imageUrl: String? = null
)