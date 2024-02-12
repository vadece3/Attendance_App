package com.ns3.Attendance.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ns3.Attendance.R;
import com.ns3.Attendance.realm.DateRegister;
import com.ns3.Attendance.realm.Student;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.util.ArrayList;

import io.realm.RealmList;

public class GraphListAdapter extends BaseAdapter {

    Context context;
    BarChart mBarChart;

    ArrayList<ArrayList<Student>> studentsList;
    RealmList<DateRegister> registerRecords;
    RealmList<Student> studentPresentList;

    public GraphListAdapter(Context context,ArrayList<ArrayList<Student>> studentsList,RealmList<DateRegister> registerRecords) {
        this.context = context;
        this.studentsList = studentsList;
        this.registerRecords = registerRecords;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.graph_list_item, parent, false);
        }

        if(position <getCount()-1)
        {
            String footer;
            mBarChart = (BarChart) convertView.findViewById(R.id.barchart);
            mBarChart.clearChart();
            for(int i=0 ; i<2 ; i++) {
                footer = studentsList.get(position).get(i).getStudent_name();
                mBarChart.addBar(new BarModel(footer.substring(0), calculateAttendance(studentsList.get(position).get(i)), 0xFF123456));
            }
            mBarChart.startAnimation();
        }
        else
        {
            String footer;
            mBarChart = (BarChart) convertView.findViewById(R.id.barchart);
            mBarChart.clearChart();
            for(int i=0 ; i<studentsList.get(position).size()%2 ; i++) {
                footer = studentsList.get(position).get(i).getStudent_name();
                mBarChart.addBar(new BarModel(footer.substring(0), calculateAttendance(studentsList.get(position).get(i)), 0xFF123456));

            }mBarChart.startAnimation();
        }

        return convertView;
    }

    private float calculateAttendance(Student selectedStudent)
    {
        int i;
        float totalDaysPresent=0,attendancePercentage,totalNumRecords=0;
        for (i = 0; i < registerRecords.size(); i++)
        {
            totalNumRecords+= registerRecords.get(i).getValue();
            studentPresentList = registerRecords.get(i).getStudentPresent();
            if (studentPresentList.contains(selectedStudent)) {
                totalDaysPresent+=registerRecords.get(i).getValue();
            }
        }
        attendancePercentage = (totalDaysPresent*100)/totalNumRecords;
        return attendancePercentage;
    }

    @Override
    public int getCount() {
            return studentsList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
