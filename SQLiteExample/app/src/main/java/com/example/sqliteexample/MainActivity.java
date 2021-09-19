package com.example.sqliteexample;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    /* 객체 선언부 */
    //메인화면 위젯선언(리사이클뷰 + 할일 추가버튼)
    private RecyclerView rv_todo;
    private FloatingActionButton btn_write;
    // DBHelper, Table_data 담긴 ArrayList, 리사이클뷰_Adapter, 레코드
    private DBHelper mDBHelper;
    private ArrayList<TodoItem> mTodoList;
    private TodoItem mLow;
    private CustomAdapter mAdapter;
    //할일입력화면 위젯선언(제목,내용,완료버튼)
    EditText et_title;
    EditText et_content;
    Button btn_ok;

    @Override// @onCreate()
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setInit();// 초기화 작업
    }

    @Override// @onStart()
    protected void onStart() {
        super.onStart();
    }

    @Override// @onResume()
    protected void onResume() {
        super.onResume();

        /*이벤트 정의 메소드*/

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// 판업창 실행
                Dialog dialog = new Dialog(MainActivity.this, R.style.Theme_MaterialComponents_Light_Dialog);
                dialog.setContentView(R.layout.dialog_edit);
                et_title = (EditText) dialog.findViewById(R.id.edit_title);
                et_content = (EditText) dialog.findViewById(R.id.edit_content);
                btn_ok = (Button) dialog.findViewById(R.id.btn_ok);

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {// 입력값 DB에 넣기
                        String title = et_title.getText().toString();
                        String content = et_content.getText().toString();
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        //값 삽입 : Insert_문
                        mDBHelper.insertTodo(title, content, currentTime);
                        //UI 갱신
                        mLow = new TodoItem();
                        mLow.setTitle(title);
                        mLow.setContent(content);
                        mLow.setWriteDate(currentTime);
                        mAdapter.addItem(mLow); // Adapter 갱신
                        rv_todo.smoothScrollToPosition(0); //첫번쨰 인덱스로 스크롤 이동
                        dialog.dismiss(); // 판업창 종료
                        Toast.makeText(MainActivity.this, "할일 목록이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
            }
        });

    }

    /*사용자 정의 메소드*/

    private void setInit() { //초기화 시작
        /* 객체 생성부 */
        rv_todo = (RecyclerView) findViewById(R.id.rv_todo);
        btn_write = (FloatingActionButton) findViewById(R.id.btn_write);
        mDBHelper = new DBHelper(this);
        mTodoList = new ArrayList<>();

        loadRecentDB(); //최근 데이터 불러오기

    }

    private void loadRecentDB() {
        mTodoList = mDBHelper.getTodoList();
        if (mAdapter == null) {
            mAdapter = new CustomAdapter(mTodoList, this);
            rv_todo.setHasFixedSize(true); //리사이클 뷰 최적화 진행
            rv_todo.setAdapter(mAdapter); //어뎁터 장착
        }
    }
}