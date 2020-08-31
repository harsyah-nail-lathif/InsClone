package com.example.insclone.model

class Post {

    private var postId: String = ""
    private var postImage: String = ""
    private var publisher: String = ""
    private var description: String = ""

    constructor()
    constructor(postid: String, postImage: String, publisher: String, description: String) {
        this.postId = postid
        this.postImage = postImage
        this.publisher = publisher
        this.description = description
    }

    //getter
    fun getPostid():String{
        return postId
    }

    fun getPostimage():String{
        return postImage
    }

    fun getPublisher():String{
        return publisher
    }

    fun getDescription():String{
        return description
    }

    //setter

    fun setPostid(postid: String){
        this.postId = postid
    }

    fun setPostimage(postImage: String){
        this.postImage = postImage
    }

    fun setPublisher(publisher: String){
        this.publisher = publisher
    }

    fun setDescription(description: String){
        this.description = description
    }

}