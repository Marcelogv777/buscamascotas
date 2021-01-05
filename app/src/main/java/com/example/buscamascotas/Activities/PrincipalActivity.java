package com.example.buscamascotas.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buscamascotas.Entity.Dispositivo;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import com.example.buscamascotas.Adapters.ListaArchivosAdapter;
import com.example.buscamascotas.R;

public class PrincipalActivity extends AppCompatActivity {

    ArrayList<Dispositivo> dispositivos = new ArrayList<>();
    private ArrayList<StorageReference> imgRefs=new ArrayList<>();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    StorageReference storageReference= FirebaseStorage.getInstance().getReference().child(currentUser.getUid());
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    StorageReference storage = FirebaseStorage.getInstance().getReference().child("fotos");
    //StorageReference storage = FirebaseStorage.getInstance().getReference().child("imagenes");
    String TAG = "tageado";
    StorageReference referenciaCarpeta; //Esto se usa cuando se entra en las carpetas tener un registro

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        referenciaCarpeta=storageReference; //inicializacion

        // Obtener la lista de dispositivos inicialmente
        DatabaseReference referenciaDispositivos = reference.child("dispositivos");
        referenciaDispositivos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Dispositivo dispositivo = ds.getValue(Dispositivo.class);
                        dispositivos.add(dispositivo);
                        //Se hace referencia a las imagenes pormedio de la llave del dispositivo
                        imgRefs.add(storage.child(ds.getKey()));
                        //String text = ds.child("nombre").getValue(String.class);
                        //Log.d("TAG",  text + " / " );
                }
                ListaArchivosAdapter listaArchivosAdapter = new ListaArchivosAdapter( dispositivos, PrincipalActivity.this,imgRefs);
                listarEnRV(listaArchivosAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        //Las opciones del fab
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se crea un popupMenu que muestra las opciones de añadir archivo y carpeta
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),fab);
                popupMenu.getMenuInflater().inflate(R.menu.anadir_archivos,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            //En caso se añada un archivo solo se selecciona
                            case R.id.añadirArchivo:
                            {
                                Intent intent1 =new Intent(PrincipalActivity.this, RegistroMascota.class);
                                startActivity(intent1);
                                return true;

                            }
                            default:return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        //ListaArchivosAdapter listaArchivosAdapter = new ListaArchivosAdapter(dispositivos, PrincipalActivity.this);



    }

    //Cuando se inicia recien se listan los documentos
    @Override
    protected void onStart() {
        super.onStart();
        listarDocumentos();
    }

    //Para listar en el RV
    public void listarEnRV(ListaArchivosAdapter listaArchivosAdapter)
    {
        RecyclerView rv = findViewById(R.id.rv);
        rv.setAdapter(listaArchivosAdapter);
        rv.setLayoutManager(new LinearLayoutManager(PrincipalActivity.this));
    }




    //Esta funcion es para listar los documentos
    public void listarDocumentos()
    {
        //Se hace a partir de la referencia de carpeta pues esta cambia entre las carpetas
        referenciaCarpeta.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                //Se crea un array list que hace referencia a todos los elementos en la carpeta
                //ArrayList<StorageReference> references = new ArrayList<>();
                //ArrayList<Dispositivo> dispositivos=new ArrayList<>();
                //Se define un umbral. Por debajo del umbral el elemento es una carpeta, por encima es un archivo.
                int umbral = 0;

                //Esto es para mostrar cuando no hay nada
                //TextView hayElementos = findViewById(R.id.ceroDocumentos);
                //if (references.size()==0) hayElementos.setVisibility(View.VISIBLE);
                //else hayElementos.setVisibility(View.GONE);

                //Se pone en el recycler view
                /*
                ListaArchivosAdapter listaArchivosAdapter = new ListaArchivosAdapter( dispositivos, PrincipalActivity.this);
                RecyclerView rv = findViewById(R.id.rv);
                rv.setAdapter(listaArchivosAdapter);
                rv.setLayoutManager(new LinearLayoutManager(PrincipalActivity.this));

                 */
            }
        });
    }


    //Esto es para cerrar la sesion
    public void cerrarSesion()
    {
        AuthUI.getInstance().signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //Para validar los permisos de escritura cuando se descarga
    public boolean validadPermisosDeEscritura()
    {
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permiso != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        else
        {
            return true;
        }

        return false;
    }

    //Para pedir los permisos de escritura
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Log.d("infoApp", "Permisos concedidos");

            if(requestCode == 1) { //DM
                validadPermisosDeEscritura();
            }
        } else {
            Log.d("infoApp", "no se brindaron los permisos");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar,menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout: cerrarSesion();
        }
        return super.onOptionsItemSelected(item);
    }
}