package com.example.insclone.model

class User{
    private var username: String = ""
    private var fullname: String = ""
    private var bio: String = ""
    private var image: String = ""
    private var uid: String = ""

    constructor()
    constructor(username:String, fullname:String, bio:String, uid:String, image:String){
        this.username = username
        this.fullname = fullname
        this.bio = bio
        this.image = image
        this.uid = uid
    }

    fun getUsername():String{
        return username
    }

    fun setusername(username: String){
        this.username = username
    }


    fun getFullname():String{
        return fullname
    }

    fun setfullname(fullname: String){
        this.fullname = fullname
    }

    fun getBio():String{
        return bio
    }

    fun setbio(bio: String){
        this.bio = bio
    }

    fun getImage():String{
        return image
    }

    fun setimage(image: String){
        this.image = image
    }

    fun getUID():String{
        return uid
    }

    fun setUID(uid: String){
        this.uid = uid
    }


}