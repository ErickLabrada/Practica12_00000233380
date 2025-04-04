package erick.labrada.practica12;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

public class PokeAdapter extends RecyclerView.Adapter<PokeAdapter.MyViewHolder> {

    Context context;
    ArrayList<PokeData> pokeList;

    public PokeAdapter(Context context, ArrayList<PokeData> pokeList){
        this.context=context;
        this.pokeList=pokeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pokemon_recycle_view,parent,false);
        return new PokeAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PokeData pokemon = pokeList.get(position);
        Glide.with(context)
                .load(pokemon.getImg())  // Image URL from Firebase
                .into(holder.imageView);

        holder.pokeName.setText(pokeList.get(position).getName());
        holder.pokeType.setText(pokeList.get(position).getType());
        holder.pokeNumber.setText(pokeList.get(position).getNumber());

    }

    @Override
    public int getItemCount() {
        return pokeList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView pokeName;
        TextView pokeType;

        TextView pokeNumber;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.PokemonImg);
            pokeName=itemView.findViewById(R.id.PokemonName);
            pokeType=itemView.findViewById(R.id.PokemonType);
            pokeNumber=itemView.findViewById(R.id.pokemonNumber);
            }
        }
    }
