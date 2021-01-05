package com.example.buscamascotas.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buscamascotas.Entity.Dispositivo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import com.example.buscamascotas.Activities.PrincipalActivity;
import com.example.buscamascotas.R;
public class ListaArchivosAdapter extends RecyclerView.Adapter<ListaArchivosAdapter.ViewHolder> {

    //private ArrayList<StorageReference> references = new ArrayList<>() ;
    private List<Dispositivo> listaDispositivos ;
    private ArrayList<StorageReference> imgRef= new ArrayList<>();
    Context context;
    int umbral;

    //Se inicializan los atributos
    public ListaArchivosAdapter(List<Dispositivo> listaDispositivos, Context context, ArrayList<StorageReference> imgRef)
    {
        this.listaDispositivos = listaDispositivos;
        this.context = context;
        this.imgRef = imgRef;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_dispositivo,parent,false);
        ListaArchivosAdapter.ViewHolder viewHolder = new ListaArchivosAdapter.ViewHolder(itemView);
        ViewHolder vHolder = new ViewHolder(itemView);
        return vHolder;
    }

    @Override
    public int getItemCount() {
        return listaDispositivos.size();
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        //final StorageReference reference = references.get(position);
        final Dispositivo dispositivo = listaDispositivos.get(position);

        holder.txtNombre.setText(dispositivo.getNombre());
        holder.txtCaracteristicas.setText(dispositivo.getCaracteristicas());
        //holder.reference = reference;

        Glide.with(context).load(dispositivo.getImagen()).into(holder.imagenDispositivo);


        StorageReference imagen;
        //obtener la imagen
        for (StorageReference sr : imgRef)
        {
                imagen = sr;
                Log.d("infoAPPP", "SE GUARDA LA IMAGEN :" + sr.getName());
                if(sr.getName().equals(dispositivo.getId())){
                    Glide.with(context).load(imagen).into(holder.imagenDispositivo);
                    break;
                }


        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCaracteristicas;
        Button detalles, editarDispositivo, eliminarDispositivo;
        ImageView imagenDispositivo;
        StorageReference reference;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Se llena el View Holder
            imagenDispositivo = itemView.findViewById(R.id.imgDispositivo);
            txtNombre= itemView.findViewById(R.id.nombreEnLista);
            txtCaracteristicas = itemView.findViewById(R.id.caracteristicasEnLista);
        }
    }


}
