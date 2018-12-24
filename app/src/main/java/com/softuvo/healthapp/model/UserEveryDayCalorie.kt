package com.softuvo.healthapp.model

class UserEveryDayCalorie {

    var breakfast=""
    var lunch=""
    var dinner=""
    var snacks=""
    var other=""
    var totalcalories=""
    constructor()
    {

    }

    constructor(
        breakfast: String,
        lunch: String,
        dinner: String,
        snacks: String,
        other: String,
        totalcalories: String
    ) {
        this.breakfast = breakfast
        this.lunch = lunch
        this.dinner = dinner
        this.snacks = snacks
        this.other = other
        this.totalcalories = totalcalories
    }


}