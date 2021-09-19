package com.example.sqliteexample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//어뎁터 및 뷰 홀더 구현
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private static final String TAG = "CustomAdapter";

    private ArrayList<TodoItem> mTodoItems;
    private Context mContext;
    private DBHelper mDBHelper;

    public CustomAdapter(ArrayList<TodoItem> mTodoItems, Context mContext) {
        this.mTodoItems = mTodoItems;
        this.mContext = mContext;
        mDBHelper = new DBHelper(mContext);
    }

    @NonNull
    @Override //ViewHolder 에 연결된 View 를 생성 및 초기화
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(holder);
    }

    @Override //ViewHolder 의 각 position Update
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {

        holder.tv_title.setText(mTodoItems.get(position).getTitle());
        holder.tv_content.setText(mTodoItems.get(position).getContent());
        holder.tv_writeDate.setText(mTodoItems.get(position).getWriteDate());

    }

    @Override //데이터 세트크기 리턴
    public int getItemCount() {

        return mTodoItems.size();

    }

    //Adapter 에 연결할 ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_writeDate;
        private TodoItem low;

        public ViewHolder(@NonNull View itemView) {//layaout에 있는위젯들 홀더에 연결

            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_writeDate = itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int curPos = getAdapterPosition(); // 현재 아이템 인덱스값
                    low = mTodoItems.get(curPos); // 현재 레코드값

                    String[] strChoiceItems = {"수정하기", "삭제하기"}; //판업창 choice 항목
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext); //판업창 생성
                    builder.setTitle("원하는 작업을 선택 하세요.");//판업창 제목
                    builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                        @Override //데이터 변경하는 판업창 띄우기(dialog_edit)
                        public void onClick(DialogInterface dialogInterface, int position) {
                            //쿼리문 매개변수에 쓰일 기존날짜
                            String beforeTime = low.getWriteDate();
                            switch (position) {
                                case 0: // 수정하기 : 선택된 레코드값 수정
                                    Dialog dialog = new Dialog(mContext, R.style.Theme_MaterialComponents_DayNight_Dialog);
                                    //dialog_edit : Inflate 및 바인딩
                                    dialog.setContentView(R.layout.dialog_edit);
                                    EditText et_title = (EditText) dialog.findViewById(R.id.edit_title);
                                    EditText et_content = (EditText) dialog.findViewById(R.id.edit_content);
                                    Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
                                    //객체 초기화
                                    et_title.setText(low.getTitle());
                                    et_content.setText(low.getContent());
                                    //커서 앞으로 이동
                                    et_title.setSelection(et_title.length());

                                    btn_ok.setOnClickListener(new View.OnClickListener() {
                                        @Override // 현재 인덱스의 레코드값 DB 수정
                                        public void onClick(View view) {
                                            //editText 수정값 Read
                                            String title = et_title.getText().toString();
                                            String content = et_content.getText().toString();
                                            //객체생성 : update 문에 쓰일 매개변수(현재 날짜)
                                            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                            mDBHelper.updateTodo(title, content, currentTime, beforeTime);
                                            //동적인 UI 를 위한 작업 : todoItem - mTodoItems 의 자료형 : 세터작업으로 update
                                            low.setTitle(title);
                                            low.setContent(content);
                                            low.setWriteDate(low.getWriteDate());
                                            notifyItemChanged(curPos, low); //항목 변경 이벤트
                                            dialog.dismiss();
                                            Toast.makeText(mContext, "선택된 항목의 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    dialog.show();
                                    break;

                                case 1: // 삭제하기 : 현제 레코드값 삭제
                                    mDBHelper.deleteTodo(beforeTime);

                                    //동적인 UI 작업 : 현재 포지션의 List 삭제
                                    mTodoItems.remove(curPos);
                                    notifyItemRemoved(curPos); //항목 제거 이벤트
                                    Toast.makeText(mContext, "선택된 항목의 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
                    builder.show();

                }
            });

        }
    }

    /* MainActivity 에서 할일 추가 후 갱신을 위한 인스턴스로 쓰일 함수 */
    public void addItem(TodoItem item) { //item 첫번쨰 항목으로 넣어주기
        mTodoItems.add(0, item);
        notifyItemInserted(0); //항목 삽입 이벤트
    }


}
