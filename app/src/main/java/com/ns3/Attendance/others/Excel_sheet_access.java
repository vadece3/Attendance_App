package com.ns3.Attendance.others;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.ns3.Attendance.R;
import com.ns3.Attendance.realm.DateRegister;
import com.ns3.Attendance.realm.Register;
import com.ns3.Attendance.realm.Student;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.ns3.Attendance.R.id.imageView;

public class Excel_sheet_access
{
    private static String TAG = "Excel_sheet_access";
    public static void readExcelFile(Context context, Uri uri,String BatchID,String Subject,String SubjectCode,int Batch, int Semester,String Stream,String Section,String Group,String GetSubjectCode)
    {
        ProgressDialog progress = new ProgressDialog(context);
        Realm realm;
        RealmList<Student> Student_list= new RealmList<Student>();
        RealmList<DateRegister> Record = new RealmList<DateRegister>();
        Register register;
        Student student;
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        realm = Realm.getInstance(realmConfig);

        try
        {
            progress.setTitle("Setting Up");
            progress.setMessage("Please wait while we set up this class");
            progress.setCancelable(false);
            progress.show();

            // Creating Input Stream
            File file = new File(uri.getPath());
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            if(rowIter==null)
                Toast.makeText(context,"Empty File",Toast.LENGTH_SHORT).show();

            while(rowIter.hasNext())
            {
                HSSFRow myRow = (HSSFRow) rowIter.next();

                Iterator tempIt = myRow.cellIterator();
                HSSFCell tempCell = (HSSFCell)tempIt.next();
                tempCell.setCellType(Cell.CELL_TYPE_STRING);

                if(tempCell.toString().length()<1)
                    break;

                Iterator cellIter = myRow.cellIterator();

                realm.beginTransaction();
                student=new Student();

                HSSFCell myCell = (HSSFCell) cellIter.next();
                myCell.setCellType(Cell.CELL_TYPE_STRING);
                student.setRoll_number(GetSubjectCode+"-"+myCell.toString());

                myCell = (HSSFCell)cellIter.next();
                myCell.setCellType(Cell.CELL_TYPE_STRING);
                String StudentName = myCell.toString();//getting student name
                student.setStudent_name(StudentName);


                myCell = (HSSFCell)cellIter.next();
                myCell.setCellType(Cell.CELL_TYPE_STRING);
                student.setPhone_no(myCell.toString());

                //enter default data in database
                student.setMac_ID1("00:00:00:00:00:00");
                student.setMac_ID2("00:00:00:00:00:01");

                //image path where the student image is found
                String StudentImage = StudentName+".jpg";
                String filepath = file.getParent();//get folder where exel sheet is found

                if(new File(filepath+"/images/"+StudentImage).exists()){
                    //load each student image
                    student.setStudent_Image(loadStudentImage(context,filepath+"/images/"+StudentImage ));
                }
                else{
                    //load default image
                    student.setStudent_Image(loadDefaultImage(context));
                }


                realm.copyToRealmOrUpdate(student);
                realm.commitTransaction();
                Student_list.add(student);
            }
            Log.d(TAG,BatchID);
            realm.beginTransaction();
            register=new Register();
            register.setBatchID(BatchID);
            register.setSubject(Subject);
            register.setSubjectCode(SubjectCode);
            register.setBatch(Batch);
            register.setSemester(Semester);
            register.setStream(Stream);
            register.setSection(Section);
            register.setGroup(Group);
            register.setStudents(Student_list);
            register.setRecord(Record);
            realm.copyToRealmOrUpdate(register);
            realm.commitTransaction();
        }
        catch (Exception e){e.printStackTrace(); }
        finally {
            progress.dismiss();
            realm.close();
        }
        return;
    }


    //encode Studentimage to base64 string
    private static String loadStudentImage(Context contexts,String imagefile) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile(imagefile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }

    //encode Defaultimage to base64 string
    private static String loadDefaultImage(Context contexts) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(contexts.getResources(), R.drawable.defaultimage);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }

    public static void readExcelFile(Context context, Uri uri,String batchID)
    {
        ProgressDialog progress = new ProgressDialog(context);
        Realm realm;
        Student student;
        RealmList<Student> Student_list= new RealmList<Student>();
        Register register;
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        realm = Realm.getInstance(realmConfig);

        try
        {
            progress.setTitle("Setting Up");
            progress.setMessage("Please wait while we set up this class");
            progress.setCancelable(false);
            progress.show();

            // Creating Input Stream
            File file = new File(uri.getPath());
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            if(rowIter==null)
                Toast.makeText(context,"Empty File",Toast.LENGTH_SHORT).show();

            while(rowIter.hasNext())
            {
                HSSFRow myRow = (HSSFRow) rowIter.next();

                Iterator tempIt = myRow.cellIterator();
                HSSFCell tempCell = (HSSFCell)tempIt.next();
                tempCell.setCellType(Cell.CELL_TYPE_STRING);

                if(tempCell.toString().length()<1)
                    break;

                Iterator cellIter = myRow.cellIterator();

                realm.beginTransaction();
                student=new Student();

                HSSFCell myCell = (HSSFCell) cellIter.next();
                myCell.setCellType(Cell.CELL_TYPE_STRING);
                student.setRoll_number(myCell.toString());

                myCell = (HSSFCell)cellIter.next();
                myCell.setCellType(Cell.CELL_TYPE_STRING);
                String StudentName = myCell.toString();//getting student name
                student.setStudent_name(StudentName);

                myCell = (HSSFCell)cellIter.next();
                myCell.setCellType(Cell.CELL_TYPE_STRING);
                student.setPhone_no(myCell.toString());

                //enter default data in database
                student.setMac_ID1("00:00:00:00:00:00");
                student.setMac_ID2("00:00:00:00:00:01");

                //image path where the student image is found
                String StudentImage = StudentName+".jpg";
                String filepath = file.getParent();//get folder where exel sheet is found

                if(new File(filepath+"/images/"+StudentImage).exists()){
                    //load each student image
                    student.setStudent_Image(loadStudentImage(context,filepath+"/images/"+StudentImage ));
                }
                else{
                    //load default image
                    student.setStudent_Image(loadDefaultImage(context));
                }



                realm.copyToRealmOrUpdate(student);
                realm.commitTransaction();

                register = realm.where(Register.class).equalTo("BatchID",batchID).findFirst();
                Student_list = register.getStudents();
                Log.d("hdfb",""+Student_list.size());

                if(!Student_list.contains(student))
                {
                    realm.beginTransaction();
                    register.getStudents().add(student);
                    realm.commitTransaction();
                }
            }
        }
        catch (Exception e){e.printStackTrace(); }
        finally {
            progress.dismiss();
            realm.close();
        }
        return;
    }

    public static boolean saveExcelFile(final Context context, final String fileName, String batchID)
    {
        Realm realm;
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        realm = Realm.getInstance(realmConfig);
        ProgressDialog progress = new ProgressDialog(context);
        boolean success = false;

        RealmList<DateRegister> record = new RealmList<DateRegister>();
        RealmResults<Student> studentList;
        Register register;

        try
        {
            progress.setTitle("Exporting");
            progress.setMessage("Please wait while we are creating the excel Sheet");
            progress.setCancelable(false);
            progress.show();

            register = realm.where(Register.class).equalTo("BatchID",batchID).findFirst();
            record = register.getRecord();
            studentList = register.getStudents().sort("Roll_number");

            //New Workbook
            Workbook wb = new HSSFWorkbook();

            Cell c = null;

            //New Sheet
            Sheet sheet1 = null;
            sheet1 = wb.createSheet("Attendance");

            addFirstRow(sheet1,c,record);
            addSecondRow(sheet1,c,record);
            addStudentData(sheet1,c,record,studentList);
            // Create a path where we will place our List of objects on external storage
            File file = new File(context.getExternalFilesDir(null), fileName);
            FileOutputStream os = null;

            try {
                os = new FileOutputStream(file);
                wb.write(os);
                Log.w("FileUtils", "Writing file" + file);
                success = true;

                final File f = file;
                final Context cx = context;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("File Exported in direcory-\n\n"+file+"\n\nView it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                try {
                                    Intent intent = new Intent();
                                    intent.setAction(android.content.Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(f), "application/vnd.ms-excel");
                                    cx.startActivity(intent);
                                } catch (ActivityNotFoundException e)
                                {
                                    Toast.makeText(context,"No App installed to open Excel Sheets",Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                        .setNeutralButton("SEND", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File filelocation = new File(context.getExternalFilesDir(null), fileName);
                                Uri path = Uri.fromFile(filelocation);
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent .setType("vnd.android.cursor.dir/email");
                                emailIntent .putExtra(Intent.EXTRA_STREAM, path);
                                emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Exported Student Data");
                                context.startActivity(Intent.createChooser(emailIntent , "Send email..."));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {

                            }
                        }).create();
                AlertDialog alert = builder.create();
                alert.show();

            } catch (IOException e) {
                Log.w("FileUtils", "Error writing " + file, e);
            } catch (Exception e) {
                Log.w("FileUtils", "Failed to save file", e);
            } finally {
                try {
                    if (null != os)
                        os.close();
                } catch (Exception ex) {
                }
            }
        }
        catch (Exception e){e.printStackTrace(); }
        finally {
            progress.dismiss();
            realm.close();
        }
        return success;
    }


    public static boolean saveExcelFile(final Context context, final String fileName, String batchID,Date fromDate, Date toDate)
    {
        Realm realm;
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context).build();
        realm = Realm.getInstance(realmConfig);
        ProgressDialog progress = new ProgressDialog(context);
        boolean success = false;

        RealmList<DateRegister> temp = new RealmList<DateRegister>();
        RealmList<DateRegister> record = new RealmList<DateRegister>();
        RealmResults<Student> studentList;
        Register register;

        try
        {
            progress.setTitle("Exporting");
            progress.setMessage("Please wait while we are creating the excel Sheet");
            progress.setCancelable(false);
            progress.show();

            register = realm.where(Register.class).equalTo("BatchID",batchID).findFirst();
            temp = register.getRecord();
            studentList = register.getStudents().sort("Roll_number");

            Date presentDate;

            for(int i=0 ; i<temp.size() ; i++) {
                presentDate = new Date(temp.get(i).getDateToday().getYear()+1900,temp.get(i).getDateToday().getMonth(),temp.get(i).getDateToday().getDate());
                if (presentDate.compareTo(fromDate) >= 0 && presentDate.compareTo(toDate) <= 0)
                    record.add(temp.get(i));
            }

            //New Workbook
            Workbook wb = new HSSFWorkbook();

            Cell c = null;

            //New Sheet
            Sheet sheet1 = null;
            sheet1 = wb.createSheet("Attendance");

            addFirstRow(sheet1,c,record);
            addSecondRow(sheet1,c,record);
            addStudentData(sheet1,c,record,studentList);
            // Create a path where we will place our List of objects on external storage
            File file = new File(context.getExternalFilesDir(null), fileName);
            FileOutputStream os = null;

            try {
                os = new FileOutputStream(file);
                wb.write(os);
                Log.w("FileUtils", "Writing file" + file);
                success = true;

                final File f = file;
                final Context cx = context;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("File Exported in direcory-\n\n"+file+"\n\nView it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                try {
                                    Intent intent = new Intent();
                                    intent.setAction(android.content.Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(f), "application/vnd.ms-excel");
                                    cx.startActivity(intent);
                                }catch (ActivityNotFoundException e)
                                {
                                    Toast.makeText(context,"No App installed to open Excel Sheets",Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                        .setNeutralButton("Email", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File filelocation = new File(context.getExternalFilesDir(null), fileName);
                                Uri path = Uri.fromFile(filelocation);
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent .setType("vnd.android.cursor.dir/email");
                                emailIntent .putExtra(Intent.EXTRA_STREAM, path);
                                emailIntent .putExtra(Intent.EXTRA_SUBJECT,"Simplify Exported Student Data");
                                context.startActivity(Intent.createChooser(emailIntent , "Send email..."));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {

                            }
                        }).create();
                AlertDialog alert = builder.create();
                alert.show();

            } catch (IOException e) {
                Log.w("FileUtils", "Error writing " + file, e);
            } catch (Exception e) {
                Log.w("FileUtils", "Failed to save file", e);
            } finally {
                try {
                    if (null != os)
                        os.close();
                } catch (Exception ex) {
                }
            }
        }
        catch (Exception e){e.printStackTrace(); }
        finally {
            progress.dismiss();
            realm.close();
        }
        return success;
    }

    //FILLING THE EXPORTED EXCEL SHEET
    static void addFirstRow(Sheet sheet1,Cell c,RealmList<DateRegister> record)
    {
        int j;
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Sl.No");

        c = row.createCell(1);
        c.setCellValue("MAJOR / MATRICULE");

        c = row.createCell(2);
        c.setCellValue("STUDENT NAME");

        // Adding Dates
        for(j=3 ; j<record.size()+3 ; j++)
        {
            c = row.createCell(j);
            c.setCellValue(""+record.get(j-3).getDateToday().getDate()+"/"+(record.get(j-3).getDateToday().getMonth()+1)+"/"+(record.get(j-3).getDateToday().getYear()+1900));
        }

        c = row.createCell(record.size()+3);
        c.setCellValue("TOTAL ATTENDANCE MARK");

        c = row.createCell(record.size()+4);
        c.setCellValue("ON 100(%)");
    }

    static void addSecondRow(Sheet sheet1,Cell c,RealmList<DateRegister> record)
    {
        int j,value=0;
        Row row = sheet1.createRow(1);
        c = row.createCell(0);
        c.setCellValue("");

        c = row.createCell(1);
        c.setCellValue("");

        c = row.createCell(2);
        c.setCellValue("");

        for(j=3 ; j<record.size()+3 ; j++)
        {
            c = row.createCell(j);
            c.setCellValue("/"+record.get(j-3).getValue());
            value+=record.get(j-3).getValue();
        }
        c = row.createCell(record.size()+3);
        c.setCellValue("/"+value);

        c = row.createCell(record.size()+4);
        c.setCellValue("");
    }

    static void addStudentData(Sheet sheet1,Cell c,RealmList<DateRegister> record,RealmResults<Student> studentList)
    {
        int i,j;
        int value,present;
        for(i=0 ; i<studentList.size() ; i++)
        {
            value=0;
            present=0;
            Row row = sheet1.createRow(i+2);

            c = row.createCell(0);
            c.setCellValue(i+1);

            c = row.createCell(1);
            c.setCellValue(studentList.get(i).getRoll_number());

            c = row.createCell(2);
            c.setCellValue(studentList.get(i).getStudent_name());

            for(j=3 ; j<record.size()+3 ; j++)
            {
                value+=record.get(j-3).getValue();
                c = row.createCell(j);
                if(record.get(j-3).getStudentPresent().contains(studentList.get(i))) {
                    c.setCellValue(record.get(j - 3).getValue());
                    present+=record.get(j - 3).getValue();
                }
                else
                    c.setCellValue("");
            }
            c = row.createCell(record.size()+3);
            c.setCellValue(present);

            c = row.createCell(record.size()+4);
            c.setCellValue((present*100)/value);
        }
    }


}
