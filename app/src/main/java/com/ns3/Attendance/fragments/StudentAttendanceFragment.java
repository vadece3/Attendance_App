package com.ns3.Attendance.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ns3.Attendance.R;
import com.ns3.Attendance.adapters.StudentAttendanceListAdapter;
import com.ns3.Attendance.realm.DateRegister;
import com.ns3.Attendance.realm.Register;
import com.ns3.Attendance.realm.Student;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;

public class StudentAttendanceFragment extends Fragment
{
    View view;
    android.content.Context context;

    String batchID;
    String rollNum;

    Realm realm;
    RealmConfiguration realmConfig;
    RealmList<DateRegister> registerRecords;
    RealmList<Student> studentsPresentList;
    Student selectedStudent;
    int totalNumRecords=0,totalDaysPresent=0;
    float attendancePercentage;
    ArrayList<Integer> presentDatesID;

    ListView recordListView;
    TextView percentageView;
    StudentAttendanceListAdapter studentAttendanceListAdapter;

    ImageView imageView;
    Button button;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_student_attendance, container, false);

        realmConfig = new RealmConfiguration.Builder(context).build();
        realm = Realm.getInstance(realmConfig);

        recordListView = (ListView)view.findViewById(R.id.student_record_list);
        percentageView = (TextView)view.findViewById(R.id.attendance_percentage);

        presentDatesID = new ArrayList<Integer>();
        registerRecords = realm.where(Register.class).equalTo("BatchID",batchID).findFirst().getRecord();
        registerRecords.sort("dateToday");
        for(int i=0 ; i<registerRecords.size() ; i++)
            totalNumRecords+=registerRecords.get(i).getValue();
        selectedStudent = realm.where(Student.class).equalTo("Roll_number",rollNum).findFirst();

        calculateAttendance();

        studentAttendanceListAdapter = new StudentAttendanceListAdapter(context,registerRecords,presentDatesID,totalNumRecords,totalDaysPresent,StudentAttendanceFragment.this);
        recordListView.setAdapter(studentAttendanceListAdapter);
        totalDaysPresent = 0;

//start code view student image
        imageView = (ImageView)view.findViewById(R.id.imageView);
        button = (Button)view.findViewById(R.id.add_image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        loadImage();
//end code view student image

        return view;
    }

    private void loadImage() {
        RealmResults<Student> results = realm.where(Student.class).equalTo("Roll_number",rollNum).findAll();
        for(Student student : results){
            String SIM = student.getStudent_Image();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] byteFormat = stream.toByteArray();

            byteFormat = Base64.decode(SIM, Base64.NO_WRAP);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(byteFormat, 0, byteFormat.length);
            imageView.setImageBitmap(decodedImage);
        }

    }


    //view student image
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);

            imageView.buildDrawingCache();
            Bitmap bmp = imageView.getDrawingCache();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] byteFormat = stream.toByteArray();

                // Get the Base64 string
                String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
////////////////////////////////check the roll number/////////////////
            RealmResults<Student> results = realm.where(Student.class).equalTo("Roll_number",rollNum).findAll();

            //enter image into database
            realm.beginTransaction();

            for(Student Student_Image : results){
                Student_Image.setStudent_Image(imgString);
            }

            realm.commitTransaction();

        }
    }





    private void calculateAttendance() {
        int i;
        for (i = 0; i < registerRecords.size(); i++)
        {
            studentsPresentList = registerRecords.get(i).getStudentPresent();
            if (studentsPresentList.contains(selectedStudent)) {
                presentDatesID.add(registerRecords.get(i).getDateID());
                totalDaysPresent+=registerRecords.get(i).getValue();
            }
        }
        attendancePercentage = ((float)totalDaysPresent*100)/(float)totalNumRecords;
        percentageView.setText(""+attendancePercentage + " %");
        totalNumRecords=totalDaysPresent=0;
    }

    public void markStudent(int dateRegisterId)
    {
        DateRegister record;
        Student student;
        record = realm.where(DateRegister.class).equalTo("dateID",dateRegisterId).findFirst();
        student = realm.where(Student.class).equalTo("Roll_number",rollNum).findFirst();
        realm.beginTransaction();
        if(!record.getStudentPresent().contains(student))
            record.getStudentPresent().add(student);
        realm.commitTransaction();

        registerRecords = realm.where(Register.class).equalTo("BatchID",batchID).findFirst().getRecord();

        for(int i=0 ; i<registerRecords.size() ; i++)
            totalNumRecords+=registerRecords.get(i).getValue();

        int i;
        for (i = 0; i < registerRecords.size(); i++)
        {
            studentsPresentList = registerRecords.get(i).getStudentPresent();
            if (studentsPresentList.contains(selectedStudent)) {
                totalDaysPresent+=registerRecords.get(i).getValue();
            }
        }
        attendancePercentage = ((float)totalDaysPresent*100)/(float)totalNumRecords;
        percentageView.setText(""+attendancePercentage + " %");
        totalNumRecords=totalDaysPresent=0;
    }

    public void unmarkStudent(int dateRegisterId)
    {
        DateRegister record;
        Student student;
        realm.beginTransaction();
        record = realm.where(DateRegister.class).equalTo("dateID",dateRegisterId).findFirst();
        student = realm.where(Student.class).equalTo("Roll_number",rollNum).findFirst();
        if( record.getStudentPresent().contains(student))
            record.getStudentPresent().remove(student);
        realm.commitTransaction();

        registerRecords = realm.where(Register.class).equalTo("BatchID",batchID).findFirst().getRecord();
        for(int i=0 ; i<registerRecords.size() ; i++)
            totalNumRecords+=registerRecords.get(i).getValue();

        int i;
        for (i = 0; i < registerRecords.size(); i++)
        {
            studentsPresentList = registerRecords.get(i).getStudentPresent();
            if (studentsPresentList.contains(selectedStudent)) {
                totalDaysPresent+=registerRecords.get(i).getValue();
            }
        }
        attendancePercentage = ((float)totalDaysPresent*100)/(float)totalNumRecords;
        percentageView.setText(""+attendancePercentage + " %");
        totalNumRecords=totalDaysPresent=0;
    }


    public void getBatchID(String batchID)
    {
        this.batchID = batchID;
    }
    public void getStudentRoll(String rollNum) { this.rollNum = rollNum; }
    public void getActivityContext(android.content.Context context) { this.context = context; }
}
