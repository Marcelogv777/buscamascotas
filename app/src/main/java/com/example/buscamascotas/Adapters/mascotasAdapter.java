package com.example.buscamascotas.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buscamascotas.Entity.Dispositivo;
import com.example.buscamascotas.R;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class mascotasAdapter extends RecyclerView.Adapter<mascotasAdapter.ViewHolder> {

    ArrayList<StorageReference> references;
    private List<Dispositivo> mlistaDispositivos;
    private ArrayList<StorageReference> imgRef;
    Context context;

    int umbral;

    //Se inicializan los atributos
    public mascotasAdapter(List<Dispositivo> listaDispositivos, Context context)
    {
        this.mlistaDispositivos = listaDispositivos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_dispositivo,parent,false);
        mascotasAdapter.ViewHolder viewHolder = new mascotasAdapter.ViewHolder(itemView);


        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        //View contactView = inflater.inflate(R.layout.item_contact, parent, false);

        // Return a new holder instance
        //ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final StorageReference reference = references.get(position);
        final Dispositivo dispositivo = mlistaDispositivos.get(position);

        holder.txtNombre.setText(dispositivo.getNombre());
        holder.txtCaracteristicas.setText(dispositivo.getCaracteristicas());
        Glide.with(context).load(dispositivo.getImagen()).into(holder.imagenDispositivo);

        StorageReference imagen;
        //obtener la imagen
        for (StorageReference sr : imgRef)
        {
                imagen = sr;
                //dispositivo.setImagen(imagen);
                //mostrar la imagen
                Glide.with(context).load(imagen).into(holder.imagenDispositivo);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mlistaDispositivos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtCaracteristicas;
        Button detalles, editarDispositivo, eliminarDispositivo;
        ImageView imagenDispositivo;

        public TextView nameTextView;
        public Button messageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Se llena el View Holder
            imagenDispositivo = itemView.findViewById(R.id.imgDispositivo);
            txtNombre= itemView.findViewById(R.id.nombreEnLista);
            txtCaracteristicas = itemView.findViewById(R.id.caracteristicasEnLista);
            //nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            //messageButton = (Button) itemView.findViewById(R.id.message_button);
        }
    }


}
