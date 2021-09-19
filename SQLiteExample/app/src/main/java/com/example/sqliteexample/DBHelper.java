package com.example.sqliteexample;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "seongDroid.db";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 데이터 베이스가 생성 될 떄 호출
        db.execSQL("CREATE TABLE IF NOT EXISTS TodoList(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, writeDate TEXT NOT NULL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onCreate(db);

    }

    // SELECT 문 (할일 목록들을 조회)
    public ArrayList<TodoItem> getTodoList() { // TodoList 테이블 AllData -> todoItems 에 담아서 리턴
        ArrayList<TodoItem> todoItems = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase(); //데이터베이스 오픈 모드
        Cursor cursor = db.rawQuery("SELECT * FROM TodoList ORDER BY writeDate DESC", null); //제공된 SQL 실행
        if (cursor.getCount() != 0) { //커서의 행 수가 존재할때
            while (cursor.moveToNext()) { //다음 행으로 이동
                int id = cursor.getInt(cursor.getColumnIndex("id"));// 열값(id) Int 로 반환
                String title = cursor.getString(cursor.getColumnIndex("title"));// title 값 반환
                String content = cursor.getString(cursor.getColumnIndex("content"));// content 값 반환
                String writeDate = cursor.getString(cursor.getColumnIndex("writeDate"));// writeDate 값 반환

                TodoItem todoItem = new TodoItem(); //컬항목들 게터세터해놓은 todoItem 인스턴스에 매칭시키기
                todoItem.setId(id);
                todoItem.setTitle(title);
                todoItem.setContent(content);
                todoItem.setWriteDate(writeDate);
                todoItems.add(todoItem);  //다 넣었으므로 첫단에 만든 Array List 에 추가시키기
            } // 모든 행 다 넣기완료
        }
        cursor.close(); //처리가 끝났으므로 커서 닫기

        return todoItems; //TodoList 테이블의 All Data 담긴 ArrayList 를 리턴

    }


    // INSERT 문
    public void insertTodo(String title, String content, String writeDate) {//인수값을 각 열항목에 그대로 추가하기
        SQLiteDatabase db = getWritableDatabase(); //데이터베이스 쓰기모드
        db.execSQL("INSERT INTO TodoList (title, content, writeDate) VALUES('" + title + "', '" + content + "', '" + writeDate + "');");
    }

    // UPDATE 문
    public void updateTodo(String title, String content, String writeDate, String beforeDate) { //인수값을 각 열항목에 그대로 변경하기
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE TodoList SET title ='" + title + "', content ='" + content + "', writeDate ='" + writeDate + "' WHERE writeDate='" + beforeDate + "'");
    }


    // DELETE 문
    public void deleteTodo(String beforeDate) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM TodoList WHERE writeDate = '" + beforeDate + "'"); //id 값에 맞는 행지우기
    }
}
