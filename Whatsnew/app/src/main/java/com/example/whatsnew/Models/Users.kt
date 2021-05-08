package com.example.whatsnew.Models

class Users {

    private var uid: String = ""
    private var cover: String = ""
    private var facebook: String = ""
    private var instgram: String = ""
    private var profile: String = ""
    private var search: String = ""
    private var status: String = ""
    private var username: String = ""

    constructor()
    constructor(uid: String, cover: String, facebook: String, instgram: String, profile: String, search: String, status: String, username: String) {
        this.uid = uid
        this.cover = cover
        this.facebook = facebook
        this.instgram = instgram
        this.profile = profile
        this.search = search
        this.status = status
        this.username = username
    }

    fun getUID():String?{
        return uid
    }
    fun setUID(uid:String) {
        this.uid = uid
    }

    fun getCover():String?{
        return cover
    }

    fun setCover(cover:String) {
        this.cover = cover
    }

    fun getFaceBook():String?{
        return facebook
    }

    fun setFaceBook(facebook:String) {
        this.facebook = facebook
    }

    fun getInstgram():String?{
        return instgram
    }

    fun setInstgram(instgram: String) {
        this.instgram = instgram
    }

    fun getProfile():String?{
        return profile
    }

    fun setProfile(profile:String) {
        this.profile = profile
    }

    fun getSearch():String?{
        return search
    }

    fun setSearch(search: String) {
        this.search = search
    }

    fun getStatus():String?{
        return status
    }

    fun setStatus(status:String) {
        this.status = status
    }

    fun getUserName():String?{
        return username
    }

    fun setUserName(username: String) {
        this.username = username
    }


}