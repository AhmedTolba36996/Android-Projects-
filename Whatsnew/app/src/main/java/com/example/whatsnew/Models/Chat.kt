package com.example.whatsnew.Models

class Chat {

    private var sender: String = ""
    private var message: String = ""
    private var reciver: String = ""
    private var isseen = false
    private var url: String = ""
    private var messageId: String = ""

    constructor()
    constructor(sender: String, message: String, reciver: String, isseen: Boolean, url: String, messageId: String) {
        this.sender = sender
        this.message = message
        this.reciver = reciver
        this.isseen = isseen
        this.url = url
        this.messageId = messageId
    }

    fun getSender():String?{
        return sender
    }
    fun setSender(sender:String) {
        this.sender = sender
    }

    fun getMessage():String?{
        return message
    }

    fun setMessage(message:String) {
        this.message = message
    }

    fun getReciver():String?{
        return reciver
    }

    fun setReciver(reciver:String) {
        this.reciver = reciver
    }

    fun getURL():String?{
        return url
    }

    fun setURL(url: String) {
        this.url = url!!
    }

    fun getMessageId():String?{
        return messageId
    }

    fun SetMessageId(messageId:String) {
        this.messageId = messageId
    }

    fun isIsSeen():Boolean{
        return isseen
    }

    fun setIsSeen(isseen: Boolean) {
        this.isseen = isseen
    }




}