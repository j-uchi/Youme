package com.myapp.youme

import android.app.Application
import java.io.File
import java.io.FileNotFoundException

class MyApp : Application(){

    var NOWDIRECTORY:String=""
    var currentflg=false

    override fun onCreate(){
        super.onCreate()
    }

    companion object{
        private var instance:MyApp?=null
        fun getInstance():MyApp{
            if(instance==null)
                instance=MyApp()
            return instance!!
        }
    }


}