package test.yespinoza.androidproject.View.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import test.yespinoza.androidproject.Adapter.CardViewAdapter;
import test.yespinoza.androidproject.Adapter.CommentAdapter;
import test.yespinoza.androidproject.Model.Entity.CardView;
import test.yespinoza.androidproject.Model.Entity.Place;
import test.yespinoza.androidproject.Project;
import test.yespinoza.androidproject.R;

public class PlaceFileActivity extends AppCompatActivity {
    private int ACTIVITY_CHOOSE_FILE = 42;
    public static Place place;
    //FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private RecyclerView.LayoutManager lManager;
    private RecyclerView recycler;
    private List<CardView> items;
    private CardViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_file);
        getSupportActionBar().setTitle("Archivos del Sitio");
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.btnlogin_shape));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Project.getInstance().setCurrentActivity(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        recycler = findViewById(R.id.recycler_files);
        getFiles();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void uploadFile() {

        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        StorageReference riversRef = mStorageRef.child("images/rivers.jpg");

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    private void downloadFile() {
        try {
            File localFile = File.createTempFile("images", "jpg");
            StorageReference riversRef = mStorageRef.child("images/rivers.jpg");
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            // ...
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                }
            });
        } catch
                (Exception ex) {
        }
    }

    private void getFiles() {
        items = new ArrayList<>();
        String image = "iVBORw0KGgoAAAANSUhEUgAAAGAAAABgCAYAAADimHc4AAACa0lEQVR42u3dvUsjURTG4RnXj12/WkEsFgmkGAjkiyRdWjvRDeyisPaClYWFSBAsYhRBEfW/9RwYOyWK0TnnzO+FS7Cbe5877yQScpOEEEIIIYSQOOn3+7O9Xu9XlmXLRY1arbY0HA5nSrXwnU5nrdVq7TabzRN5vZDXcVGj0WiM5Bq2SrHwlUplQSY9kHEvk36yNOr1+lboO0EWfU5226GMR2uLr0Ou7yEyQioLv2Nx4V9DCLf68qDbkMndWgcIW0eWdr9W4KQaDFdHMqFTQzVzLQAHpamj/OE7MgQwlrfBq/K6XYo6koku6qQtAegHMF1U+fuP7vTQdWQVQK9tMBj8UITQdWQZQKM7O3QdWQd4QQhbRx4AQteRF4CwdeQJIGQdeQMIV0ceAULVkVeAMHXkGSBEHXkHcF9HEQBc11EUALd1VK1WV+SCriIAuKyj/H/v11EAPlpH+VdeUgCmCPDBOhrrl88AmDLAe+so/wLYIgBfAPCeOgLgiwEm1REABsABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAALwAWPvJsgKOTBnpIRZJgUnlAs5LfAecJkVHdsFeWQH0xMDCAdrt9qbF0/O+oX7u9AivxEDSkt4FuvtTCwB6cupPuQuOrR5lOO2zymSuR0U/fN96R/R/0s89On/o6o+2/tUNl1hNt9v9LbtkXy72TC76UsaN55G/1dTD6v7J4q8nXpJl2bweqKwf1jwPnYO5uiGEEEIIIeRzeQZDmz8NDT+OVgAAAABJRU5ErkJggg==";

        items.add(new CardView("Archivo Prueba","Prueba I", image));
        items.add(new CardView("Archivo Prueba II","Prueba II", image));
        items.add(new CardView("Archivo Prueba II","Prueba III", image));
        recycler.setHasFixedSize(true);
        lManager = new LinearLayoutManager(Project.getInstance().getCurrentActivity());
        recycler.setLayoutManager(lManager);
        adapter = new CardViewAdapter(items);
        adapter.setOnItemClickListener(new CardViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Toast.makeText(getApplicationContext(),"Descargando...",Toast.LENGTH_LONG).show();
            }
        });
        recycler.setAdapter(adapter);
    }

    public void onBrowse(View view) {
        Intent chooseFile;
        Intent intent;
        /*chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType( "* / *");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        */
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        String path     = "";
        if(requestCode == ACTIVITY_CHOOSE_FILE)
        {
            Uri uri = data.getData();
            String FilePath = getRealPathFromURI(uri); // should the path be here in this string
            System.out.print("Path  = " + FilePath);

        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String [] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query( contentUri, proj, null, null,null);
        if (cursor == null) return null;
        int column_index    = cursor.getColumnIndexOrThrow(proj[0]);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);
        cursor.close();
        return imagePath;
    }
}