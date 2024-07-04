package com.harshRajpurohit.letsBrowse.model

data class Bookmark(val name: String, val url: String, var image: ByteArray? = null, var imagePath: Int? = null)
