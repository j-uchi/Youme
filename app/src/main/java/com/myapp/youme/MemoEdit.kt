package com.myapp.youme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_memo_disp.*
import kotlinx.android.synthetic.main.activity_memo_edit.*
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class MemoEdit : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()

    //開始時処理
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_edit)
        strTitle.setText(pickFileName(GLOBAL.NOWDIRECTORY))
        strMain.setText(READFILE())
    }

    //メニューバー表示
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.menu_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //メニューボタンリスナークラス
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //保存ボタン押下時
        if(item.itemId == R.id.menu_save){
            //ファイル保存に関する処理を記述する
            UpdateFile()
        }
        return super.onOptionsItemSelected(item)
    }

    //戻るキー押下時処理
    override fun onBackPressed(){
        super.onBackPressed()
        finish()
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }

    //ファイル読み込み処理
    fun READFILE():String{
        var str:String=""
        try{
            val file= File("$filesDir"+GLOBAL.NOWDIRECTORY)
            val scan= Scanner(file)
            while(scan.hasNextLine()){
                str+=scan.nextLine()
                if(scan.hasNextLine())str+="\n"
            }
        }
        catch(e: FileNotFoundException){
            println(e)
        }
        return str
    }

    //引数:ディレクトリ 戻り値:ファイル名（拡張子含まず）
    fun pickFileName(str:String):String{
        var FileName:String=str
        var point:Int=str.lastIndexOf(".")
        if(point!=-1){
            FileName = str.substring(0,point)
        }
        point=str.lastIndexOf("/")
        if(point!=-1){
            FileName=FileName.substring(point+1)
        }
        return FileName
    }

    //ファイル更新
    fun UpdateFile(){

        //ファイルを削除する
        val file=File("$filesDir/"+GLOBAL.NOWDIRECTORY)
        file.delete()

        //ディレクトリを一旦戻す
        val point:Int=GLOBAL.NOWDIRECTORY.lastIndexOf("/")
        if(point!=-1)GLOBAL.NOWDIRECTORY=GLOBAL.NOWDIRECTORY.substring(0,point)

        //変更内容に合わせてファイルを作成
        if(strMain.text.toString()!=""&&strTitle.text.toString()!=""){
            val file= File("$filesDir/"+GLOBAL.NOWDIRECTORY+"/"+strTitle.text+".txt")
            file.writeText(strMain.text.toString())
            GLOBAL.NOWDIRECTORY+="/"+strTitle.text+".txt"
        }

        //元の画面に戻る
        finish()
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)

    }

}