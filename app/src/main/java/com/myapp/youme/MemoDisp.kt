package com.myapp.youme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_memo_disp.*
import kotlinx.android.synthetic.main.activity_memo_disp.txtDir
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class MemoDisp : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_disp)
    }

    //終了時処理
    override fun onDestroy(){
        super.onDestroy()
    }

    //再表示処理
    override fun onResume(){
        super.onResume()
        txtMain.setText(READFILE())
        txtDir.setText("Dir : "+GLOBAL.NOWDIRECTORY)
    }

    //メニューバー表示
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.menu_memo, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //メニューボタンリスナークラス
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //エディトモードへ遷移
        if(item.itemId == R.id.menu_edit){
            startActivity(Intent(this,MemoEdit::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        return super.onOptionsItemSelected(item)
    }

    //戻るキー押下時処理
    override fun onBackPressed(){
        super.onBackPressed()

        //ディレクトリを戻す
        val point:Int=GLOBAL.NOWDIRECTORY.lastIndexOf("/")
        if(point!=-1)GLOBAL.NOWDIRECTORY=GLOBAL.NOWDIRECTORY.substring(0,point)
        GLOBAL.refleshflg=true

        //アクティビティを終了する
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