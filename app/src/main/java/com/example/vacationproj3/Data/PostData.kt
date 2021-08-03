package com.example.vacationproj3.Data

data class PostData (
    var username: String,
    var writerUid : String,
    var userPhotoUrl: String,
    var userStressLevel: String,

    var heart: ArrayList<String>,
    var postImageUUID: String?,
    var text: String,
    var postUid : String

    )
