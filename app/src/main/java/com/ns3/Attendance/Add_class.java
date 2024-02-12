package com.ns3.Attendance;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.ns3.Attendance.others.Excel_sheet_access;
import com.ns3.Attendance.realm.Register;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class Add_class extends AppCompatActivity
{
    private static String TAG = "Add_class";

    int card_padding;
    DisplayMetrics metrics;
    int height;
    EditText subject_edit,subjectcode_edit,stream_edit,group_edit;
    Spinner semester_edit,batch_edit,section_edit;
    ImageView imageView;
    String batch,subject,batchid,subjectcode,semester,stream,section,group,Getsubjectcode,dbsubjectcode;
    int semesterI,batchI;
    CheckBox groupCheck;
    boolean groupB = false;
    int year;

    Excel_sheet_access excel_sheet;

    Realm realm;
    RealmResults<Register> checkBatch;
    RealmConfiguration realmConfig;

    private static final int FILE_SELECT_CODE = 0;

    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        //start load data into spinner
        String[] country = { "","1","2","3"};
        Spinner spin = (Spinner) findViewById(R.id.edit_semester);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        String[] season = { "","DAY","NIGHT"};
        Spinner spins = (Spinner) findViewById(R.id.edit_section);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aas = new ArrayAdapter(this,android.R.layout.simple_spinner_item,season);
        aas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spins.setAdapter(aas);

        String[] semester = { "","1","2","3","4"};
        Spinner spinss = (Spinner) findViewById(R.id.edit_batch);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aass = new ArrayAdapter(this,android.R.layout.simple_spinner_item,semester);
        aass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinss.setAdapter(aass);

        //end code load data into spinner



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.main_blue));
        }
        initToolbar();

        realmConfig = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfig);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        height= metrics.heightPixels;
        card_padding=height/20;

        batch_edit = (Spinner) findViewById(R.id.edit_batch);
        subject_edit = (EditText)findViewById(R.id.edit_subject);
        semester_edit = (Spinner)findViewById(R.id.edit_semester);
        subjectcode_edit = (EditText)findViewById(R.id.edit_subjectcode);
        stream_edit = (EditText)findViewById(R.id.edit_stream);
        section_edit = (Spinner) findViewById(R.id.edit_section);
        group_edit = (EditText)findViewById(R.id.edit_group);

        groupCheck = (CheckBox)findViewById(R.id.check_groupcheck);
        groupCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupCheck.isChecked())
                {
                    groupB = true;
                    findViewById(R.id.add_class_layout_9).setVisibility(View.VISIBLE);
                }
                else
                {
                    groupB = false;
                    findViewById(R.id.add_class_layout_9).setVisibility(View.GONE);
                }
            }
        });

        Button bu = (Button)findViewById(R.id.realm_browser);
        bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Add_class.this, "I`M HAPPY TO HAVE YOU HERE.\n\n" +
                        "PLEASE FOLLOW THE INSTRUCTIONS AND ENJOY ME ! ! ! ! ! ! ! ! !", Toast.LENGTH_SHORT).show();
                /*List<Class<? extends RealmObject>> classes = new ArrayList<>();
                classes.add(Register.class);
                classes.add(Student.class);
                classes.add(DateRegister.class);
                new RealmBrowser.Builder(Add_class.this)
                        .add(realm, classes)
                        .show();*/
            }
        });

        ///show sample excellsheet details
        Button but= (Button)findViewById(R.id.sample_excell_sheets);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               displayDoc();
            }
        });


        imageView=(ImageView)findViewById(R.id.button_import_excel);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (subject_edit.getText().toString().trim().equalsIgnoreCase(""))
                    subject_edit.setError("This field can not be blank");
                else if (subjectcode_edit.getText().toString().trim().equalsIgnoreCase(""))
                    subjectcode_edit.setError("This field can not be blank");
                else if (batch_edit.getSelectedItem().toString().trim().equalsIgnoreCase(""))
                    setSpinnerError(batch_edit,"This field can not be blank");
                else if (semester_edit.getSelectedItem().toString().trim().equalsIgnoreCase(""))
                    setSpinnerError(semester_edit,"This field can not be blank");
                else if (stream_edit.getText().toString().trim().equalsIgnoreCase(""))
                    stream_edit.setError("This field can not be blank");
                else if (section_edit.getSelectedItem().toString().trim().equalsIgnoreCase(""))
                    setSpinnerError(section_edit,"This field can not be blank");
                else if (groupB && group_edit.getText().toString().trim().equalsIgnoreCase(""))
                    group_edit.setError("This field can not be blank");
                else {

                    AlertDialog.Builder alert = new AlertDialog.Builder(Add_class.this);
                    alert.setTitle("Excel Sheet Import");
                    alert.setMessage(" Points to be noted-\n\n" +
                            "- The Excel file MUST be a XLS file not a XLSX file.\n\n" +
                            "- The Excel sheet has 3 columns (Left to Right)-\n \t* Major / Matriculation\n \t* Name\n \t* Phone-Number / Email\n\n" +
                            "- The Excel Sheet preferably should be created in Microsoft Excel, and not opened in the Android device.\n\n" +
                            "*****VERY IMPORTANT***** \n\n"+
                            "- 1-GET THE IMAGE OF EACH STUDENT AND GIVE EACH IMAGE THE NAME OF ITS OWNER.  \n\n"+
                            "- NB:THE NAME SHOULD BE COPIED FROM THE EXCEL SHEET. \n\n"+
                            "- 2-PUT ALL THOSE IMAGES IN A FOLDER CALLED 'images' AND PUT THIS FOLDER IN THESAME DIRECTORY WHERE THE EXCEL SHEET IS FOUND\n\n" +
                            "- 3-THE IMAGE SIZE SHOULD BE <=300KB \n\n");

                    alert.setPositiveButton("Select Excel File", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            year = Integer.parseInt(semester_edit.getSelectedItem().toString().trim());

                            //lets check if The same Subjectcode already exists
                            dbsubjectcode = subjectcode_edit.getText().toString().trim();
                            checkBatch = realm.where(Register.class).equalTo("SubjectCode", dbsubjectcode).findAll();  //Checking if The same Subjectcode already exists
                            if (checkBatch.size() == 0) {

                                //lets now check if The same BatchID already exists
                                Add_class.this.generateBatchID();
                                checkBatch = realm.where(Register.class).equalTo("BatchID", batchid).findAll();  //Checking if The same BatchID already exists
                                if (checkBatch.size() == 0) {
                                    Intent intent = new Intent(Add_class.this, FilePickerActivity.class);
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                                    intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                                    intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

                                    //intent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                                    startActivityForResult(intent, FILE_SELECT_CODE);
                                } else {
                                    Toast.makeText(Add_class.this, "Information Of Subject already exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                    Toast.makeText(Add_class.this, "SUBJECT CODE ALREADY EXISTS", Toast.LENGTH_SHORT).show();
                                }
                        }
                    });
                    alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.show();

                }
            }
        });
    }

    //set spinner error
    private void setSpinnerError(Spinner spinner, String error){
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError("error"); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            selectedTextView.setText(error); // actual error message
            spinner.performClick(); // to open the spinner list if error is found.

        }
    }

    //show sample excellsheet details function
    private void displayDoc() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(Add_class.this);
        alert.setTitle("EXCELL SHEET DETAILS");
alert.setIcon(R.drawable.excellsheetsamples);

        alert.setMessage("THE EXCELL SHEET SHOULD BE STRUCTURED AS FOLLOWS: \n\n" +
                "COLUMN A = MAJOR/MATRICULE\n\n" +
                "COLUMN B = NAME\n\n" +
                "COLUMN C = PHONE_NUMBER/E-MAIL\n\n" +
                "*****VERY IMPORTANT*****\n\n" +
                "PLEASE DO NOT PUT ANY SPACE BEFORE AND AFTER A NAME\n\n" +
                "e.g if my name is MOUSA TALOM, the name should be written in the cell like this:\n" +
                "'MOUSA TALOM' without any space.\n\n" +
                "And it should not be:\n\n" +
                "'   MOUSSA TALOM' \n\n" +
                "'MOUSA TALOM   ' \n\n" +
                "'   MOUSA TALOM   ' \n\n");

       /* alert.setPositiveButton("VIEW IMAGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Button btn = (Button)findViewById(R.id.btnclose);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });*/

        alert.setNegativeButton("CLOSE",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();


    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Course");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
////
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.action_about){
            Intent intent = new Intent(Add_class.this,About.class);
            startActivity(intent);

            return true;
        }
        else
        if(id==R.id.help){
            Intent intent = new Intent(Add_class.this,Help.class);
            startActivity(intent);

            return true;
        }
        else if(id==R.id.manuel){
            Intent intent = new Intent(Add_class.this,Manual.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);

    }
//////


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK)
        {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false))
            {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    ClipData clip = data.getClipData();
                    if (clip != null)
                    {
                        for (int i = 0; i < clip.getItemCount(); i++)
                        {
                            Uri uri = clip.getItemAt(i).getUri();
                            Toast.makeText(this,""+uri.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    // For Ice Cream Sandwich
                }
                else
                {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null)
                    {
                        for (String path: paths)
                        {
                            Uri uri = Uri.parse(path);
                            Toast.makeText(this,""+uri.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
            else
            {
                Getsubjectcode = subjectcode_edit.getText().toString().trim();//getting subjectcode to add to matricule_number to make a unique record
                Uri uri = data.getData();
                Excel_sheet_access.readExcelFile(this,uri,batchid,subject,subjectcode,batchI,semesterI,stream,section,group,Getsubjectcode);
                Toast.makeText(Add_class.this, "Course Added!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Add_class.this,Attendance.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void generateBatchID()    //Concatenate Batch Name and Subject to create final batch name
    {
        batch=batch_edit.getSelectedItem().toString().trim();
        batchI = Integer.parseInt(batch);
        subject=subject_edit.getText().toString().trim();
        subjectcode = subjectcode_edit.getText().toString().trim();
        semester = semester_edit.getSelectedItem().toString().trim();
        semesterI = Integer.parseInt(semester);
        stream = stream_edit.getText().toString().trim();
        section = section_edit.getSelectedItem().toString().trim();
        if(groupB)
            group = group_edit.getText().toString().trim();
        else
            group = "No Group";

        batchid = subject;
        batchid = batchid.concat(subjectcode);
        batchid = batchid.concat(batch);
        batchid = batchid.concat(semester);
        batchid = batchid.concat(stream);
        batchid = batchid.concat(section);
        if (groupB)
            batchid.concat(group);
        batchid = batchid.replaceAll("\\s+","");    //remove spaces from batch name
        batchid = batchid.toLowerCase();
    }



    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
