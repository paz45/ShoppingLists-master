package com.example.shoppinglists;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText etFirstName,etLastName,etPassword,etStreet,etCity,etEmail;
    private String firstname,lastname,password,street,city,email;
    Button bRegister;


    public static Uri uri;
    private ImageView ImgPhoto;
    private Bitmap bitmap;

    String userId;
    private Firebase mRootRef;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private Intent intent;
    private static final int CAMERA_PIC_REQUEST = 22;
    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        b=(Button)findViewById(R.id.Photobutton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        Firebase.setAndroidContext(this);
        mRootRef=new Firebase("https://shoppinglists-7f8a8.firebaseio.com/User");

        //////////////////////////////////////////

        mAuth = FirebaseAuth.getInstance();

        etFirstName = (EditText) findViewById(R.id.userfirstnameText);
        etLastName = (EditText) findViewById(R.id.userlastnameText);
        etPassword = (EditText) findViewById(R.id.passwordText);
        etStreet = (EditText) findViewById(R.id.streetText);
        etCity = (EditText) findViewById(R.id.cityText);
        etEmail = (EditText) findViewById(R.id.emaileditText);
        bRegister = (Button) findViewById(R.id.send);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                register();
            }

        });
    }
    public void register(){
        intialize();
        if(!vaildate()){
            Toast.makeText(this,"signup has Failed",Toast.LENGTH_SHORT).show();
        }
        else{
            onSignupSuccess();
        }
    }
    public void onSignupSuccess(){
        //TODO what will go after the vaild input
        Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(i);
    }
    public boolean vaildate(){
        boolean vaild = true;
        if (firstname.isEmpty()||firstname.length()>32){
            etFirstName.setError("Please enter vaild first name");
            vaild = false;
        }
        else if (lastname.isEmpty()||lastname.length()>32){
            etLastName.setError("Please enter vaild last name");
            vaild = false;
        }
        else if(password.length()>32||password.length()<6){
            etPassword.setError("Please enter vaild password");
            vaild = false;
        }
        else if(street.isEmpty()){
            etStreet.setError("Please enter vaild street");
            vaild = false;
        }
        else if(city.isEmpty()){
            etCity.setError("Please enter vaild city");
            vaild = false;
        }
        else if(email.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Please enter vaild E-mail");
            vaild = false;
        }
        else {

            Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        firebaseUser = mAuth.getCurrentUser();
                        userId = firebaseUser.getUid();
                        databaseReference = FirebaseDatabase.getInstance().getReference("User");

//            String id = databaseReference.push().getKey();
//                        Toast.makeText(getApplicationContext(), userId, Toast.LENGTH_LONG).show();
                        String id = userId;
                        User user = new User(password, firstname, lastname, email, city, street);
                        // String emailUser= user.getEmail();
                        databaseReference.child(userId).setValue(user);


//                        Toast.makeText(getApplicationContext(), "User Registered Successfull", Toast.LENGTH_SHORT).show();
                    }
                }
            });


//            Firebase mRefChild = mRootRef.child("First Name");
//            mRefChild.setValue(user.getUserFirstName());
//            mRefChild = mRootRef.child("Last Name");
//            mRefChild.setValue(user.getUserLastName());
//            mRefChild = mRootRef.child("Street");
//            mRefChild.setValue(user.getUserStreet());
//            mRefChild = mRootRef.child("City");
//            mRefChild.setValue(user.getUserCity());
//            mRefChild = mRootRef.child("Password");
//            mRefChild.setValue(user.getUserPassword());
//            mRefChild = mRootRef.child("Email");
//            mRefChild.setValue(user.getUserEmail());


        }
        return vaild;
    }
    public void intialize(){
        firstname = etFirstName.getText().toString().trim();
        lastname = etLastName.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        street = etStreet.getText().toString().trim();
        city = etCity.getText().toString().trim();
        email = etEmail.getText().toString().trim();

    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {

                    ImgPhoto = (ImageView) findViewById(R.id.imageButton);

                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    intent = new Intent();
                    // Show only images, no videos or anything else
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    // Always show the chooser (if there are multiple options available)
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            try {

                uri = data.getData();

                String ext=getContentResolver().getType(uri);
                ext=ext.substring(ext.lastIndexOf("/")+ 1);

                if(ext.equals("jpeg") || ext.equals("png"))
                {
                    ((ImageButton) findViewById(R.id.imageButton)).setImageURI(uri);
                }
                else
                {
                    Toast.makeText(this, "please insert a .jpg or .png", Toast.LENGTH_LONG).show();
                }


            } catch (Exception e) {
                Toast.makeText(this, "Couldn't load photo", Toast.LENGTH_LONG).show();
            }
        }
        else if (resultCode == RESULT_OK) {
            try {
                bitmap = (Bitmap) data.getExtras().get("data");

                ImgPhoto.setImageBitmap(bitmap);

            } catch (Exception e) {
                Toast.makeText(this, "Couldn't load photo", Toast.LENGTH_LONG).show();
            }
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater infalter=getMenuInflater();
        infalter.inflate(R.menu.main_menu, menu);

        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.Login:
                Intent intent1 = new Intent(this, LoginActivity.class);
                this.startActivity(intent1);
                return true;

            case R.id.Maps:
                Intent intent2 = new Intent(this, MapsActivity.class);
                this.startActivity(intent2);
                return true;

            case R.id.Main:
                Intent intent3 = new Intent(this, MainActivity.class);
                this.startActivity(intent3);
                return true;

            case R.id.UserInfo:
                Intent intent4 = new Intent(this, UserInfoActivity.class);
                this.startActivity(intent4);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onClick(View v) {
        //  register();
    }
}