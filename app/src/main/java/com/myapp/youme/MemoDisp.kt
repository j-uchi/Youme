package com.myapp.youme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_memo_disp.*
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class MemoDisp : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_disp)
        txtMain.setText(READFILE())
    }

    //終了時処理
    override fun onDestroy(){
        super.onDestroy()
        val point:Int=GLOBAL.NOWDIRECTORY.lastIndexOf("/")
        if(point!=-1)GLOBAL.NOWDIRECTORY=GLOBAL.NOWDIRECTORY.substring(0,point)
    }

    //メニューバー表示
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //メニューボタンリスナークラス
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_edit){
            //エディトモードへ遷移
        }
        return super.onOptionsItemSelected(item)
    }

    //戻るキー押下時処理
    override fun onBackPressed(){
        super.onBackPressed()
        finish()
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }

    fun READFILE():String{
        var str:String=""
        try{
            val file= File("$filesDir"+GLOBAL.NOWDIRECTORY)
            val scan= Scanner(file)
            while(scan.hasNextLine()){
                str+=scan.nextLine()+"\n"
            }
        }
        catch(e: FileNotFoundException){
            println(e)
        }
        return str
    }
}