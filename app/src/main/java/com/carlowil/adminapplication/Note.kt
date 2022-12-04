package com.carlowil.adminapplication

class Note() {

    private var title : String = ""
    private var description : String = ""

    constructor(title : String, description : String) : this() {
        this.title = title
        this.description = description
    }

    fun getTitle() : String {
        return title
    }

    fun getDescription() : String {
        return description
    }

    fun setTitle(newTitle : String) {
        title = newTitle
    }

    fun setDescription(newDescription : String) {
        description = newDescription
    }

}