package com.gazlaws.nitcgpa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements StudentConstants {
    private static final int READ_REQUEST_CODE = 42;
    private JsonObject sem_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        }

        PDFBoxResourceLoader.init(this);
        final Button buttonOpen = (Button) findViewById(R.id.btnFilePicker);
        buttonOpen.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              new MaterialFilePicker()
                                                      .withActivity(MainActivity.this)
                                                      .withRequestCode(1000)
                                                      .withFilter(Pattern.compile(".*\\.pdf$")) // Filtering files and directories by file name using regexp
                                                      // .withFilterDirectories(true) // Set directories filterable (false by default)
                                                      .withHiddenFiles(true) // Show hidden files and folders
                                                      .start();
                                          }
                                      }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final TextView filepathTextView = (TextView) findViewById(R.id.filepath);
        final Button btnParse = (Button) findViewById(R.id.btnParse);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            final String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            btnParse.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                filepathTextView.setText(filePath);
                                                getDetails(filePath);
                                            }
                                        }
            );
        }
    }


    public void getDetails(String filename) {

        final TextView nameTextView = (TextView) findViewById(R.id.name);
        final TextView cgpaTextView = (TextView) findViewById(R.id.cgpa);
        final TextView sgpaListTextView = (TextView) findViewById(R.id.each_sem_gpa);
//        File root = android.os.Environment.getExternalStorageDirectory();
        Student me = new NITCService(filename).process();
        StringBuilder list_sgpa = new StringBuilder();

        if (me != null) {
            JsonObject student_json = me.getAsJSON();
            nameTextView.setText(me.getName());
            cgpaTextView.setText(String.format("%.2f", me.getCGPA()));
            JsonObject all_semesters = student_json.getAsJsonObject("SEMESTER_INFO");

            //{"SEMESTER_I":{"SGPA":7.94},
            //"SEMESTER_II":{"SGPA":8.23}}
            Map<String, JsonElement> attributes = new HashMap<String, JsonElement>();
            Set<Map.Entry<String, JsonElement>> entrySet = all_semesters.entrySet();
            Map<Integer, Double> sem_map = new TreeMap<>();

            for (Map.Entry<String, JsonElement> entry : entrySet) {
                attributes.put(entry.getKey(), all_semesters.get(entry.getKey()));
            }
            for (Map.Entry<String, JsonElement> att : attributes.entrySet()) {
                String key = att.getKey();
                JsonElement value = att.getValue(); //{"sgpa":8.23}
                JsonObject sem_detail = value.getAsJsonObject();

                Set<Map.Entry<String, JsonElement>> sem_set = sem_detail.entrySet();
                Map<String, JsonElement> sem_att = new HashMap<String, JsonElement>();
                for (Map.Entry<String, JsonElement> sem_entry : sem_set) {
                    sem_att.put(sem_entry.getKey(), sem_detail.get(sem_entry.getKey()));
                }
                for (Map.Entry<String, JsonElement> each_sem_att : sem_att.entrySet()) {
                    String sem_key = each_sem_att.getKey();
                    JsonElement sem_value = each_sem_att.getValue();
                    if (sem_key.equals("SGPA")) {
                        switch (key) {
                            case SEMESTER_1:
                                sem_map.put(1, Double.parseDouble(sem_value.toString()));
                                break;
                            case SEMESTER_2:
                                sem_map.put(2, Double.parseDouble(sem_value.toString()));
                                break;
                            case SEMESTER_3:
                                sem_map.put(3, Double.parseDouble(sem_value.toString()));
                                break;
                            case SEMESTER_4:
                                sem_map.put(4, Double.parseDouble(sem_value.toString()));
                                break;
                            case SEMESTER_5:
                                sem_map.put(5, Double.parseDouble(sem_value.toString()));
                                break;
                            case SEMESTER_6:
                                sem_map.put(6, Double.parseDouble(sem_value.toString()));
                                break;
                            case SEMESTER_7:
                                sem_map.put(7, Double.parseDouble(sem_value.toString()));
                                break;
                            case SEMESTER_8:
                                sem_map.put(8, Double.parseDouble(sem_value.toString()));
                                break;
                            case SEMESTER_9:
                                sem_map.put(9, Double.parseDouble(sem_value.toString()));
                                break;
                            case SEMESTER_10:
                                sem_map.put(10, Double.parseDouble(sem_value.toString()));
                                break;
                            case SEMESTER_11:
                                sem_map.put(11, Double.parseDouble(sem_value.toString()));
                                break;
                            case SEMESTER_12:
                                sem_map.put(12, Double.parseDouble(sem_value.toString()));
                                break;
                            case SEMESTER_13:
                                sem_map.put(13, Double.parseDouble(sem_value.toString()));
                                break;
                            case SEMESTER_14:
                                sem_map.put(14, Double.parseDouble(sem_value.toString()));
                                break;
                        }
                    }
                }
            }

            for (Integer key : sem_map.keySet()) {
                list_sgpa.append(key).append(":").append(sem_map.get(key).toString()).append("\n");
            }
//            sgpaListTextView.setText(list_sgpa.toString());
            int size = sem_map.size();
            DataPoint[] values = new DataPoint[size];

            for (Integer key : sem_map.keySet()) {
                Double yi = sem_map.get(key);
                DataPoint v = new DataPoint(key, yi);
                values[key - 1] = v;
            }
            GraphView graph = (GraphView) findViewById(R.id.graph);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(values);
//            graph.removeAllSeries();
            graph.addSeries(series);
        }
    }
}
