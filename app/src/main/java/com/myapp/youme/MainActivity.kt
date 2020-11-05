package com.myapp.youme

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileNotFoundException


class MainActivity : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()

    //開始時処理
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DispReflesh()

        //fabリスナークラス
        val fab_plus: View =findViewById(R.id.fab_plus)
        val fab_close: View =findViewById(R.id.fab_close)
        val fab_list: View =findViewById(R.id.fab_list)
        val fab_memo: View =findViewById(R.id.fab_memo)
        fab_plus.setOnClickListener{
            //fabplusボタン処理
            fab_plus.setVisibility(View.INVISIBLE)
            fab_close.setVisibility(View.VISIBLE)
            fab_list.setVisibility(View.VISIBLE)
            fab_memo.setVisibility(View.VISIBLE)
        }
        fab_close.setOnClickListener{
            //fabcloseボタン処理
            fab_plus.setVisibility(View.VISIBLE)
            fab_close.setVisibility(View.INVISIBLE)
            fab_list.setVisibility(View.INVISIBLE)
            fab_memo.setVisibility(View.INVISIBLE)
        }
        fab_list.setOnClickListener{
            CreateListDialog()
        }
        fab_memo.setOnClickListener{
            CreateMemoDialog()
        }

    }

    //再表示処理
    override fun onResume(){
        super.onResume()
        if(GLOBAL.refleshflg){
            GLOBAL.refleshflg=false
            DispReflesh()
        }
    }

    //メニューバー表示
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //メニューボタンリスナークラス
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //ルートディレクトリへ回帰
        if(item.itemId == R.id.menu_home){
            GLOBAL.NOWDIRECTORY=""
            val INTENT=getIntent()
            INTENT.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(INTENT)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        return super.onOptionsItemSelected(item)
    }

    //戻るキー押下時処理
    override fun onBackPressed(){
        super.onBackPressed()
        val point:Int=GLOBAL.NOWDIRECTORY.lastIndexOf("/")
        if(point!=-1)GLOBAL.NOWDIRECTORY=GLOBAL.NOWDIRECTORY.substring(0,point)
        finish()
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }

    //画面のリフレッシュ
    fun DispReflesh(){
        val list=READFILE()
        if(list!=null)addItems(list)
        txtDir.setText("Dir : "+GLOBAL.NOWDIRECTORY)
    }

    //画面にファイルやフォルダを表示
    fun addItems(f_list:Array<String>){
        val SV=findViewById<View>(R.id.scrollview)as ViewGroup
        SV.removeAllViews()
        var count=0
        txtCenter.setText("データがありません")
        if (f_list != null) {
            for(i in f_list.indices){
                //もしlist[i]がテキストファイルだった場合
                if(f_list[i]!="rList") {
                    txtCenter.setText("")
                    if(f_list[i].lastIndexOf(".txt")!=-1){
                        getLayoutInflater().inflate(R.layout.layout_memo,SV)
                        val layout=SV.getChildAt(count)as ConstraintLayout
                        count++
                        layout.setTag(count)
                        (layout.getChildAt(1)as TextView).setText(hideExtention(f_list[i]))
                        layout.setOnClickListener{LayoutListener(it.getTag().toString().toInt(),f_list[i])}
                        layout.setOnLongClickListener{
                            CreateDeleteDialog(f_list[i],false)
                            true
                        }
                    }
                    //list[i]がフォルダだった場合
                    else{
                        getLayoutInflater().inflate(R.layout.layout_list,SV)
                        val layout=SV.getChildAt(count)as ConstraintLayout
                        count++
                        layout.setTag(count)
                        (layout.getChildAt(1)as TextView).setText(f_list[i])
                        layout.setOnClickListener{LayoutListener(it.getTag().toString().toInt(),f_list[i])}
                        layout.setOnLongClickListener{
                            CreateDeleteDialog(f_list[i],true)
                            true
                        }
                    }
                }
            }
        }
    }

    //レイアウトのタップ時処理分岐
    fun LayoutListener(num:Int,directoryName:String){
        GLOBAL.NOWDIRECTORY +="/"+directoryName
        if(GLOBAL.NOWDIRECTORY.lastIndexOf(".")!=-1){
            startActivity(Intent(this,MemoDisp::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        else{
            startActivity(Intent(this,MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    //引数:ファイル名 戻り値:ファイル名（拡張子含まず）
    fun hideExtention(str:String):String{
        var point:Int=str.lastIndexOf(".")
        if(point!=-1){
            return str.substring(0,point)
        }
        return ""
    }

    //リスト作成時ダイアログの作成
    fun CreateListDialog(){
        val myedit= EditText(this)
        var str:String=""
        AlertDialog.Builder(this)
            .setTitle("新規リスト作成")
            .setView(myedit)
            .setPositiveButton("作成"){dialog,which->
                CreateListFile(myedit.getText().toString())
            }
            .setNegativeButton("キャンセル") { dialog, which ->
            }
            .setCancelable(false)
            .setIcon(R.drawable.ic_baseline_format_list_bulleted_24)
            .show()
    }

    //メモ作成時ダイアログの作成
    fun CreateMemoDialog(){
        val myedit= EditText(this)
        var str:String=""
        AlertDialog.Builder(this)
            .setTitle("新規メモ作成")
            .setView(myedit)
            .setPositiveButton("作成"){dialog,which->
                CreateMemoFile(myedit.getText().toString())
            }
            .setNegativeButton("キャンセル") { dialog, which ->
            }
            .setCancelable(false)
            .setIcon(R.drawable.ic_baseline_message_24)
            .show()
    }

    //削除確認ダイアログの作成
    fun CreateDeleteDialog(f_name:String,f_type:Boolean){
        var str:String=""
        if(f_type==true)str="フォルダ : "+f_name
        else str="ファイル:"+f_name
        AlertDialog.Builder(this)
            .setTitle(str)
            .setMessage("名称変更または削除しますか？")
            .setPositiveButton("削除"){dialog,which->
                DeleteFile(f_name)
            }
            .setNegativeButton("キャンセル"){dialog,which->
            }
            .setNeutralButton("名称変更"){dialog,which->
                //追加でダイアログを作成する
            }
            .setIcon(R.drawable.ic_baseline_warning_24)
            .show()
    }

    //ファイル削除
    fun DeleteFile(filename:String){
        //ファイルを削除する
        val file=File("$filesDir/"+GLOBAL.NOWDIRECTORY+"/"+filename)
        if(file.delete()){
            //削除成功
            Toast.makeText(applicationContext, "削除しました", Toast.LENGTH_SHORT).show()
        }
        else{
            //削除失敗
            Toast.makeText(applicationContext, "データが残っています", Toast.LENGTH_SHORT).show()
        }

        ACTIVITY_RESTART()
    }

    //リストを実際に作成
    fun CreateListFile(text:String){
        if(text!=""){
            var filename:String=text
            if(filename.length>14){
                filename=filename.substring(0,14)
                filename+="…"
            }
            val file= File("$filesDir/"+GLOBAL.NOWDIRECTORY+"/"+filename)
            file.mkdir()
        }
        ACTIVITY_RESTART()
    }

    //メモを実際に作成
    fun CreateMemoFile(text:String){
        if(text!=""){
            var filename:String=text
            if(filename.length>14){
                filename=filename.substring(0,14)
                filename+="…"
            }
            //この辺の処理から
            val file= File("$filesDir/"+GLOBAL.NOWDIRECTORY+"/"+filename+".txt")
            file.writeText(text)
        }
        ACTIVITY_RESTART()
    }

    //ファイル読み込み処理
    fun READFILE():Array<String>?{
        try{
            val file= File("$filesDir"+GLOBAL.NOWDIRECTORY)
            val f_list=file.list()
            return f_list
        }catch(e: FileNotFoundException) {
            return null
        }
    }

    //アクティビティ自体をリロード
    fun ACTIVITY_RESTART(){
        finish()
        startActivity(getIntent())
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}