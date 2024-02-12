package com.ns3.Attendance.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ns3.Attendance.ClassDetailsActivity;
import com.ns3.Attendance.R;
import com.ns3.Attendance.realm.Student;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class StudentListAdapter extends BaseAdapter
{



    Context context;
    RealmResults<Student> studentList;

    TextView rollView,nameView,phoneView,mac1View,mac2View;
    //Smallimage variable
    ImageView smallimageView;
    Realm realm;
    String rollNum;


    public StudentListAdapter(Context context, RealmResults<Student> studentList) {
       this.context = context;
        this.studentList = studentList;
    }


    @Override
    public int getCount() {
        if(studentList!=null)
            return studentList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return studentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.student_list_item, parent, false);
        }

        rollView = (TextView)convertView.findViewById(R.id.student_item_roll);
        nameView = (TextView)convertView.findViewById(R.id.student_item_name);
        phoneView = (TextView)convertView.findViewById(R.id.student_item_phone);
        smallimageView = (ImageView)convertView.findViewById(R.id.SmallimageView);

        rollView.setText(studentList.get(position).getRoll_number());
        nameView.setText(studentList.get(position).getStudent_name());
        phoneView.setText("Tel / Email : "+studentList.get(position).getPhone_no());

          //start set smallimage
            String SIMs = studentList.get(position).getStudent_Image();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] byteFormat = stream.toByteArray();

            byteFormat = Base64.decode(SIMs, Base64.NO_WRAP);
            Bitmap decodedImages = BitmapFactory.decodeByteArray(byteFormat, 0, byteFormat.length);
            smallimageView.setImageBitmap(decodedImages);
        //end set smallimage


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ClassDetailsActivity)context).showStudentAttendanceFragment(studentList.get(position).getRoll_number());
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Delete Student from Class?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                RealmList<Student> finalStudents = new RealmList<Student>();
                                for(int i=0 ; i<studentList.size() ; i++) {
                                    if(i!=position)
                                        finalStudents.add(studentList.get(i));
                                }
                                ((ClassDetailsActivity)context).updateStudentList(finalStudents);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {


                            }
                        }).create();
                AlertDialog alert = builder.create();
                alert.show();


                return false;
            }
        });

        return convertView;

    }

}
