package com.example.whatsnew.Models

class ChatList {

    private var id:String = ""

    constructor()
    constructor(id: String) {
        this.id = id
    }
    fun getId():String?{
        return id
    }

    fun SetId(id:String) {
        this.id = id
    }

}