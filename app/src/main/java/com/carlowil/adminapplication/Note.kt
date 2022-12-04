package com.carlowil.adminapplication

import com.google.firebase.firestore.Exclude

data class Note(var title : String = "", var description : String = "", var priority : Int = 0) {
    var id : String = "" @Exclude get
}
