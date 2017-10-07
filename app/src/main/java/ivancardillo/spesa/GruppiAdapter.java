package ivancardillo.spesa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by ivanc on 18/09/2017.
 */

public class GruppiAdapter extends RecyclerView.Adapter<GruppiAdapter.ViewHolder> {

    private List<Gruppo> mContacts;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public GruppiAdapter(Context context, List<Gruppo> gruppi) {
        mContacts = gruppi;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public GruppiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.layout_riga, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(GruppiAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Gruppo contact = mContacts.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nomeGruppo;
        textView.setText(contact.getNome());
        TextView textView2 = viewHolder.elencoPartecipanti;
        textView2.setText(contact.getPartecipanti());
        ImageView imageView=viewHolder.thumbnailFoto;
        if(!contact.getImagePath().equals(""))
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap imageBitmap;
            imageBitmap = BitmapFactory.decodeFile(contact.getImagePath(), options);
            imageView.setImageBitmap(imageBitmap);
        }
        else
        {
            imageView.setImageResource(R.drawable.ic_no_profile);
        }



    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nomeGruppo;
        public TextView elencoPartecipanti;
        public ImageView thumbnailFoto;
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        CardView cv;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView);
            nomeGruppo = (TextView) itemView.findViewById(R.id.nomeGruppo);
            elencoPartecipanti = (TextView) itemView.findViewById(R.id.partecipanti);
            thumbnailFoto=(ImageView) itemView.findViewById(R.id.thumbnail);

        }
    }

}
