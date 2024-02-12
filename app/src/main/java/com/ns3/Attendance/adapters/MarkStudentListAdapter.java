package com.ns3.Attendance.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ns3.Attendance.R;
import com.ns3.Attendance.realm.Student;

import io.realm.RealmList;
import io.realm.RealmResults;


public class MarkStudentListAdapter extends BaseAdapter
{
    Context context;
    RealmResults<Student> studentList;
    RealmList<Student> presentStudents;

    TextView nameView;
    CheckBox presentCheck;


    public MarkStudentListAdapter(Context context, RealmResults<Student> studentList, RealmList<Student> presentStudents) {
        this.context = context;
        this.studentList = studentList;
        this.presentStudents = presentStudents;
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
        if(convertView==null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.mark_student_list_item, parent, false);
        }
        nameView = (TextView)convertView.findViewById(R.id.mark_student_list_name);
        presentCheck = (CheckBox)convertView.findViewById(R.id.mark_student_list_check);

        nameView.setText(studentList.get(position).getStudent_name());
        if(presentStudents.contains(studentList.get(position)))
            presentCheck.setChecked(true);
        else
            presentCheck.setChecked(false);
        return convertView;
    }
}