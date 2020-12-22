package com.example.chattingapplication;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TextView userName,email,status;
    private CircleImageView photo;
    private ImageButton pback_button;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;
    private FloatingActionButton change_photo;
    private BottomSheetDialog bottomSheetDialog , bsDialogEditName,BsDialogEditStatus;
    private ProgressDialog progressDialog;
    private Uri imageUri;
    private LinearLayout edit_Name,edit_about;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userName = findViewById(R.id.username);
        email= findViewById(R.id.email);
        status = findViewById(R.id.status);
        photo = findViewById(R.id.img_profile);
        change_photo = findViewById(R.id.change_pic);
        edit_Name = findViewById(R.id.edit_name);
        edit_about = findViewById(R.id.edit_about);
        pback_button = findViewById(R.id.back_button_profile);


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photo.invalidate();
                Drawable dr = photo.getDrawable();
                Common.IMAGE_BITMAP = ((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileActivity.this, photo, "image");
                Intent intent = new Intent(ProfileActivity.this, ViewImageActivity.class);
                startActivity(intent, activityOptionsCompat.toBundle());
            }
        });
        pback_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                ProfileActivity.this.finish();
            }
        });

        fAuth =FirebaseAuth.getInstance();
        fStore =FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        userId =fAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        getInfo();
        iniActionClick();
    }

    private void getInfo(){
        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                userName.setText(documentSnapshot.getString("userName"));
                email.setText(documentSnapshot.getString("email"));
                status.setText(documentSnapshot.getString("status"));
                Glide.with(ProfileActivity.this)
                        .load(documentSnapshot.getString("imageProfile"))
                        .into(photo);

            }
        });
    }


    private void iniActionClick(){
        change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetPickPhoto();
            }
        });
        edit_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetEditName();
            }
        });
        edit_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetEditStatus();
            }
        });
    }

    private void showBottomSheetPickPhoto() {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet,null);

        ((View) view.findViewById(R.id.in_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opengallery();
                bottomSheetDialog.dismiss();
            }
            private void opengallery() {
                Intent intent = new Intent()
                        .setType("image/*")
                        .setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 0);
            }
        });
        ((View) view.findViewById(R.id.in_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"camera",Toast.LENGTH_SHORT).show();
                openCamera();
                bottomSheetDialog.dismiss();
            }
            private void openCamera(){
                // codes for opening camera and click an image
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // code for returning back to this activate with the click image
                startActivityForResult(cameraIntent,1);
            }
        });

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                bottomSheetDialog = null;
            }
        });
        bottomSheetDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final String TAG = "ProfileActivity";
        switch(requestCode){
            case 0:
                if(resultCode==RESULT_OK) {
                    imageUri = data.getData(); //The uri with the location of the file
                    photo.setImageURI(imageUri);
                    uploadGalleryToFirebase();
                }
                break;
            case 1:
                if(resultCode==RESULT_OK) {
                    Bitmap img = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG,90,bytes);
                    byte bb[]= bytes.toByteArray();
                    //set the image
                    photo.setImageBitmap(img);
                    uploadCameraToFirebase(bb);
                }
                break;
        }
    }

    private void showBottomSheetEditName(){
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name,null);

        ((View) view.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsDialogEditName.dismiss();
            }
        });
        final EditText edit_username = view.findViewById(R.id.ed_username);
        ((View) view.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edit_username.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Name can not be empty",Toast.LENGTH_SHORT).show();
                }else {
                    updateName(edit_username.getText().toString());
                    bsDialogEditName.dismiss();
                }
            }
        });

        bsDialogEditName = new BottomSheetDialog(this);
        bsDialogEditName.setContentView(view);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Objects.requireNonNull(bsDialogEditName.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bsDialogEditName.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                bsDialogEditName = null;
            }
        });
        bsDialogEditName.show();
    }

    private void showBottomSheetEditStatus(){
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_status,null);

        ((View) view.findViewById(R.id.btn_cancel1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BsDialogEditStatus.dismiss();
            }
        });

        final EditText edit_Status = view.findViewById(R.id.ed_status);
        ((View) view.findViewById(R.id.btn_save1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edit_Status.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Status can not be empty",Toast.LENGTH_SHORT).show();
                }else {
                    updateStatus(edit_Status.getText().toString());
                    BsDialogEditStatus.dismiss();
                }
            }
        });

        BsDialogEditStatus = new BottomSheetDialog(this);
        BsDialogEditStatus.setContentView(view);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Objects.requireNonNull(BsDialogEditStatus.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        BsDialogEditStatus.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                BsDialogEditStatus = null;
            }
        });
        BsDialogEditStatus.show();
    }

    /*private void uploadToFirebase() {
        if (imageUri != null) {
            progressDialog.setMessage("Uploading....");
            progressDialog.show();

            StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("ImageProfile/" +System.currentTimeMillis() + "." + getFileExtention(imageUri));
            riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                if (downloadUri.toString() == null) {
                                    Toast.makeText(getApplicationContext(),
                                            "Please select an image", Toast.LENGTH_SHORT).show();
                                } else {
                                    // need to store the path in the database
                                }
                                progressDialog.dismiss();
                                // Log.i("path", downloadUri.toString()); // if required check in logcat
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                // Progress Listener for loading percentage on the dialog box
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    Toast.makeText(getApplicationContext(), "Uploaded " + (int) progress + "%", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }*/

    private void uploadGalleryToFirebase() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading....");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + randomKey);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content),"Image Uploaded",Snackbar.LENGTH_LONG).show();
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    if (downloadUri.toString() == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "Please select an image", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // need to store the path in the database
                                        final String sdownload_url = String.valueOf(downloadUri);
                                        HashMap<String,Object> hashMap= new HashMap<>();
                                        hashMap.put("imageProfile",sdownload_url);
                                        fStore.collection("Users").document(userId).update(hashMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getApplicationContext(),"Uploaded Successfully",Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                    pd.dismiss();
                                    // Log.i("path", downloadUri.toString()); // if required check in logcat
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),"Not Uploaded",Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00* snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                pd.setMessage("Progress:"+ (int) progressPercent + "%");
            }
        });
    }

    private void uploadCameraToFirebase(byte[] bb) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading....");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + randomKey);

        riversRef.putBytes(bb)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content),"Image Uploaded",Snackbar.LENGTH_LONG).show();
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    if (downloadUri.toString() == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "Please select an image", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // need to store the path in the database
                                        final String sdownload_url = String.valueOf(downloadUri);
                                        HashMap<String,Object> hashMap= new HashMap<>();
                                        hashMap.put("imageProfile",sdownload_url);
                                        fStore.collection("Users").document(userId).update(hashMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getApplicationContext(),"Uploaded Successfully",Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                    pd.dismiss();
                                    // Log.i("path", downloadUri.toString()); // if required check in logcat
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),"Not Uploaded",Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00* snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                pd.setMessage("Progress:"+ (int) progressPercent + "%");
            }
        });
    }

    private void updateName(String newName){
        fStore.collection("Users").document(userId).update("userName",newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Name Changed",Toast.LENGTH_SHORT).show();
                getInfo();
            }
        });

    }

    private void updateStatus(String newStatus){
        fStore.collection("Users").document(userId).update("status",newStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Status Changed",Toast.LENGTH_SHORT).show();
                getInfo();
            }
        });

    }

}