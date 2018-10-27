package groups.kma.sharelocation.Chat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import groups.kma.sharelocation.R;
import groups.kma.sharelocation.model.Users;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private TextView displayname, status;
    private CircleImageView mImage;
    Button changeStt, changeImg,changePass;
    private static final int GALLERY_PICK = 1;
    Bitmap thumb_bitmap;
    private DatabaseReference mStatusDatabase;

    private StorageReference mImagesStorage;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        displayname = findViewById(R.id.settingDisplayname);
        status = findViewById(R.id.settingStatus);
        mImage = findViewById(R.id.settingImage);
        changeImg = findViewById(R.id.button2);
        changeStt = findViewById(R.id.button3);
        changePass = findViewById(R.id.buttonDMK);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Thông tin người dùng");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mImagesStorage = FirebaseStorage.getInstance().getReference();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                displayname.setText(users.getUserName());
                status.setText(users.getStatus());
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("photoUrl").getValue().toString();

                if (!image.equals("default")) {
                    //Picasso.get().load(image).placeholder(R.drawable.acc_box).into(mImage);
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.mipmap.ic_launcher).into(mImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.mipmap.ic_launcher).into(mImage);
                        }
                    });

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent changpass = new Intent(SettingsActivity.this,ChangePassActivity.class);
                startActivity(changpass);
            }
        });
        changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
                //crop image - co hình ảnh
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(SettingsActivity.this);

            }
        });
        changeStt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeSttDialog();
            }
        });
    }

    private void showChangeSttDialog() {
        final Dialog dialog = new Dialog(SettingsActivity.this);
        dialog.setTitle("Đổi trạng thái");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_change_stt);
        Button acpt = dialog.findViewById(R.id.accept_change_stt);
        Button decl = dialog.findViewById(R.id.decline_change_stt);
        final EditText editstt = dialog.findViewById(R.id.edit_status);
        editstt.setText(status.getText());
        acpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog = new ProgressDialog(dialog.getContext());
                mProgressDialog.setTitle("Thay đổi trạng thái");
                mProgressDialog.setMessage("Đang thay đổi trạng thái");
                mProgressDialog.show();
                String edit = editstt.getText().toString();
                mStatusDatabase.child("status").setValue(edit).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            mProgressDialog.dismiss();
                        }else{
                            Toast.makeText(SettingsActivity.this, "Thay đổi trạng thái thất bại.", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });
                dialog.dismiss();
            }
        });
        decl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Vui lòng đợi một chút.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                Uri resultUri = result.getUri();

                final File thumb_filePath = new File(resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();

                try {
                    thumb_bitmap = new Compressor(this).setMaxWidth(200).setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();
                StorageReference filepath = mImagesStorage.child("profile_images").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath = mImagesStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                             final String dowload_url = task.getResult().getDownloadUrl().toString();
                             UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    String thumb_download = task.getResult().getDownloadUrl().toString();

                                    if (task.isSuccessful()) {
                                        Map update_hashMap = new HashMap<>();
                                        update_hashMap.put("image",dowload_url);
                                        update_hashMap.put("photoUrl",thumb_download);
                                        mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "Upload thành công.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(SettingsActivity.this, "Đổi ảnh thất bại", Toast.LENGTH_SHORT).show();
                                                    mProgressDialog.dismiss();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(SettingsActivity.this, "Đổi ảnh thất bại", Toast.LENGTH_SHORT).show();
                                        mProgressDialog.dismiss();
                                    }
                                }
                            });


                        } else {
                            Toast.makeText(SettingsActivity.this, "Đổi ảnh thất bại", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
