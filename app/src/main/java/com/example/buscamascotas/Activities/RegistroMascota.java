package com.example.buscamascotas.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.buscamascotas.Adapters.ListaArchivosAdapter;
import com.example.buscamascotas.Entity.Dispositivo;
import com.example.buscamascotas.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class RegistroMascota extends AppCompatActivity {

    private Uri rutaDeArchivo;
    boolean galeria = false ;
    byte[] imbytes ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_mascota);
        // Creando carpeta mascotas (solo la primera vez) y agregando mascotas a esta

        final EditText nombre, caracteristicas, celular;
        final Button agregarDispositivo, cargarFoto, tomarFoto;
        ImageView imagenDispositivo;

        final DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("dispositivos");

        nombre = (EditText) findViewById(R.id.nombre);
        celular = (EditText) findViewById(R.id.celular);
        caracteristicas = (EditText) findViewById(R.id.caracteristicas);
        imagenDispositivo = (ImageView) findViewById(R.id.imagenDeDispositivoAAgregar);
        agregarDispositivo = (Button) findViewById(R.id.agregarDispositivoAInventario);
        cargarFoto = (Button) findViewById(R.id.cargarFoto);
        tomarFoto = (Button) findViewById(R.id.tomarFoto);





        agregarDispositivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //registarr una mascota perdida
                //refererncia de la base de datos
                //Datos llenos y completos
                //push a la base de datos


                if(nombre.getText()!=null && caracteristicas.getText()!=null&&celular.getText()!=null){
                    Dispositivo d = new Dispositivo();
                    d.setCaracteristicas(caracteristicas.getText().toString());
                    d.setNombre(nombre.getText().toString());
                    d.setCelular(celular.getText().toString());
                    //userDatabase.push().setValue(d);
                    DatabaseReference newRef =  userDatabase.push();
                    d.setId(newRef.getKey());
                    newRef.setValue(d);
                    final String nombreCarpetaDispositivo = newRef.getKey();;

                    StorageReference stReference = FirebaseStorage.getInstance().getReference();
                    final StorageReference fotoRef = stReference.child("fotos").child(nombreCarpetaDispositivo);
                    if(galeria==true){
                        fotoRef.putFile(rutaDeArchivo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(), "Mascota registrada", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if(galeria==false){
                        fotoRef.putBytes(imbytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(), "Mascota registrada ", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }




                }
                //push a la base de datos


                //Regresar a la vista principal
                Intent intent1 =new Intent(RegistroMascota.this, PrincipalActivity.class);
                startActivity(intent1);


            }
        });
        cargarFoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                galeria= true;
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"Seleccione una imagen"),1);
            }
        });

        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galeria= false;
                if (RegistroMascota.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    int permiso = ContextCompat.checkSelfPermission(RegistroMascota.this, Manifest.permission.CAMERA);
                    int permiso2 = ContextCompat.checkSelfPermission(RegistroMascota.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    int permiso3 = ContextCompat.checkSelfPermission(RegistroMascota.this, Manifest.permission.READ_EXTERNAL_STORAGE);

                    if (permiso == PackageManager.PERMISSION_GRANTED) {
                        tomarFoto();
                    } else {
                        ActivityCompat.requestPermissions(RegistroMascota.this, new String[]{Manifest.permission.CAMERA},3);
                    }

                    if (permiso2 == PackageManager.PERMISSION_GRANTED) {
                        tomarFoto();
                    } else {
                        ActivityCompat.requestPermissions(RegistroMascota.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},3);
                    }

                    if (permiso3 == PackageManager.PERMISSION_GRANTED) {
                        tomarFoto();
                    } else {
                        ActivityCompat.requestPermissions(RegistroMascota.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},3);
                    }


                }else {
                    Toast.makeText(getApplicationContext(), "Error: este dispositivo no tiene camara", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void tomarFoto() {
        Intent tomarFotex = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(tomarFotex,2);
        }catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(), "Error: no es posible tomar una foto", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null && data.getData() != null){
            rutaDeArchivo = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),rutaDeArchivo);
                ImageView imagenDispositivo = (ImageView) findViewById(R.id.imagenDeDispositivoAAgregar);
                imagenDispositivo.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 2 ){
            //&& data != null && data.getData() != null
            try {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                ImageView imagenDispositivo = findViewById(R.id.imagenDeDispositivoAAgregar);
                imagenDispositivo.setImageBitmap(imageBitmap);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                assert imageBitmap != null;
                imageBitmap.compress(Bitmap.CompressFormat.PNG,0,bos);
                imbytes = bos.toByteArray();
                imagenDispositivo.setVisibility(View.VISIBLE);

            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error: debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }

        }
    }

}