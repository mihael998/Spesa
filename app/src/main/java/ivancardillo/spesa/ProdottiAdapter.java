package ivancardillo.spesa;

import android.content.Context;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by ivanc on 18/09/2017.
 */

public class ProdottiAdapter extends RecyclerView.Adapter<ProdottiAdapter.ViewHolder> {

    private SparseBooleanArray clicked;
    private List<Prodotto> mContacts;
    private Context mContext;
    private Boolean visibility;
    ImageView imageView;

    // Pass in the contact array into the constructor
    public ProdottiAdapter(Context context, List<Prodotto> prodotti) {
        mContacts = prodotti;
        mContext = context;
        clicked = new SparseBooleanArray();
        visibility = false;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public ProdottiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.layout_riga_prodotti, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ProdottiAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Prodotto contact = mContacts.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nomeProdotto;
        textView.setText(contact.getNome());
        imageView = viewHolder.image;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nomeProdotto;
        public ImageView image;
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        CardView cv;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView2);
            nomeProdotto = (TextView) itemView.findViewById(R.id.nomeProdotto);
            image = (ImageView) itemView.findViewById(R.id.check);

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clicked.get(getAdapterPosition(), false) && image.getVisibility() == View.VISIBLE) {
                        clicked.delete(getAdapterPosition());
                        image.setImageResource(R.drawable.ic_add_round_grey);
                    } else
                        if (clicked.get(getAdapterPosition(), true) && image.getVisibility() == View.VISIBLE){
                        clicked.put(getAdapterPosition(), true);
                        image.setImageResource(R.drawable.ic_add_round_primary);
                    }
                }
            });
        }



    }



}
