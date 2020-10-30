package com.myapp.youme

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {

    val GLOBAL=MyApp.getInstance()

    //開始時処理
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list=READFILE()
        if(list!=null)addItems(list)

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

    //終了時処理
    override fun onDestroy(){
        super.onDestroy()
        if(GLOBAL.currentflg==true){
            GLOBAL.currentflg=false
            val point:Int=GLOBAL.NOWDIRECTORY.lastIndexOf("/")
            if(point!=-1)GLOBAL.NOWDIRECTORY=GLOBAL.NOWDIRECTORY.substring(0,point)
        }

        //ダイアログから追加を実行した場合、画面上では追加されるものの内部処理ではディレクトリが移動してしまう
        //再表示処理等を再度行うと一つ上のディレクトリに移動したり、実際にフォルダやファイルが作成される場所がずれてしまう問題
        //明日、ここの処理を再修正する必要あり

        //明日はメモ画面の表示の際に実際の内部データが表示されるように変更する必要あり
        //削除機能の実装をもってver1.0完成予定

        //アプリアイコンや通知関連、タスクバーの処理等の追記も併せて明日中に実装する

        //実際のデータの動作処理を確認すること
    }

    //再描画処理を作る※未完成
    override fun onResume(){
        super.onResume()
    }

    //戻るキー押下時処理
    override fun onBackPressed(){
        super.onBackPressed()
        GLOBAL.currentflg=true
        finish()
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
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
            .setPositiveButton("ADD"){dialog,which->
                CreateListFile(myedit.getText().toString())
            }.show()
    }

    //メモ作成時ダイアログの作成
    fun CreateMemoDialog(){
        val myedit= EditText(this)
        var str:String=""
        AlertDialog.Builder(this)
            .setTitle("新規メモ作成")
            .setView(myedit)
            .setPositiveButton("ADD"){dialog,which->
                CreateMemoFile(myedit.getText().toString())
            }.show()
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
        file.delete()

        //画面に更新処理をかける
        finish()
        startActivity(getIntent())
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
    //リストを実際に作成
    fun CreateListFile(text:String){
        if(text!=""){
            val file= File("$filesDir/"+GLOBAL.NOWDIRECTORY+"/"+text)
            file.mkdir()
        }
        finish()
        startActivity(getIntent())
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    //メモを実際に作成
    fun CreateMemoFile(text:String){
        if(text!=""){
            var filename:String=text
            if(filename.length>9){
                filename=filename.substring(0,9)
                filename+="…"
            }
            //この辺の処理から
            val file= File("$filesDir/"+GLOBAL.NOWDIRECTORY+"/"+filename+".txt")
            file.writeText(text)
        }
        finish()
        startActivity(getIntent())
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    //ファイル読み込み処理
    fun READFILE():Array<String>?{
        val GLOBAL=MyApp.getInstance()
        try{
            val file= File("$filesDir"+GLOBAL.NOWDIRECTORY)
            val f_list=file.list()
            return f_list
        }catch(e: FileNotFoundException) {
            return null
        }
    }

}