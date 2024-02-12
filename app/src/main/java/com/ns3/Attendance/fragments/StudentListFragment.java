package com.ns3.Attendance.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ns3.Attendance.R;
import com.ns3.Attendance.adapters.StudentListAdapter;
import com.ns3.Attendance.realm.Register;
import com.ns3.Attendance.realm.Student;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

public class StudentListFragment extends Fragment
{
    View view;
    android.content.Context context;

    ListView studentListView;
    StudentListAdapter studentListAdapter;
    RealmResults<Student> studentList;
    RealmList<Student> tempStudentList1;
    RealmList<Student> tempStudentList2;
    ArrayList<Integer> positions;
    ArrayList<String> rollNums;

    String batchID;

    Realm realm;
    RealmConfiguration realmConfig;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_student_list, container, false);

        realmConfig = new RealmConfiguration.Builder(context).build();
        realm = Realm.getInstance(realmConfig);

        studentListView = (ListView)view.findViewById(R.id.student_listview);

        positions = new ArrayList<Integer>();
        rollNums = new ArrayList<String>();

        initilizeList();

        return view;
    }

    public void initilizeList()
    {
        positions.clear();
        rollNums.clear();
        tempStudentList1 = realm.where(Register.class).equalTo("BatchID",batchID).findFirst().getStudents();
        for(int i=0 ; i<tempStudentList1.size() ; i++)
        {
            if(!rollNums.contains(tempStudentList1.get(i).getRoll_number())) {
                Log.e("CCC",tempStudentList1.get(i).getRoll_number());
                rollNums.add(tempStudentList1.get(i).getRoll_number());
                positions.add(i);
            }
        }

        tempStudentList2 = new RealmList<Student>();
        for(int i=0 ; i<positions.size() ; i++) {
            tempStudentList2.add(tempStudentList1.get(positions.get(i)));
        }

        Register register;
        realm.beginTransaction();
        register = realm.where(Register.class).equalTo("BatchID",batchID).findFirst();
        register.setStudents(tempStudentList2);
        realm.commitTransaction();

        studentList = realm.where(Register.class).equalTo("BatchID",batchID).findFirst().getStudents().sort("Student_name");


        studentListAdapter = new StudentListAdapter(context,studentList);
        studentListView.setAdapter(studentListAdapter);
    }

    public void getBatchID(String batchID)
    {
        this.batchID = batchID;
    }

    public void getActivityContext(android.content.Context context)
    {
        this.context = context;
    }
}
