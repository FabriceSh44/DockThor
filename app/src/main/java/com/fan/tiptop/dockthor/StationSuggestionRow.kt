package com.fan.tiptop.dockthor

class StationSuggestionRow {
    private var adress = ""

    constructor(adress: String) {
        this.adress = adress
    }

    override fun toString(): String {
        return "${this.adress}"
    }



}
