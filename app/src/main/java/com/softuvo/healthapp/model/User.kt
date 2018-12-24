package com.softuvo.healthapp.model

import android.net.Uri

class User {

         var id=""
         var username=""
         var useremail=""
         var userphone=""
         var userlocation=""
         var userage=""
         var userheight_feet=""
         var userheight_inch=""
         var userweight=""
         var imageurl=""


    constructor(
        id: String,
        username: String,
        useremail: String,
        userphone: String,
        userlocation: String,
        userage: String,
        userheight_feet: String,
        userheight_inch: String,
        userweight: String
    ) {
        this.id = id
        this.username = username
        this.useremail = useremail
        this.userphone = userphone
        this.userlocation = userlocation
        this.userage = userage
        this.userheight_feet = userheight_feet
        this.userheight_inch = userheight_inch
        this.userweight = userweight
    }
    constructor()
    {

    }
}