package com.example.whatsnew.Notification

class Data {

    private var user:String = ""
    private var body:String = ""
    private var title:String = ""
    private var sented:String = ""
    private var icon = 0

    constructor()
    constructor(user: String, body: String, title: String, sented: String, icon: Int) {
        this.user = user
        this.body = body
        this.title = title
        this.sented = sented
        this.icon = icon
    }

    fun getUser():String?{return user}
    fun setUser(user:String){this.user=user}

    fun getBody():String?{return body}
    fun setBody(body:String){this.body=body}

    fun getSented():String?{return sented}
    fun setSented(sented:String){this.sented=sented}

    fun getTitle():String?{return title}
    fun setTitle(title:String){this.title=title}

    fun getIcon():Int{return icon}
    fun setIcon(icon:Int){this.icon=icon}



}