package ivancardillo.spesa;

import android.content.Context;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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


   /* public GruppiAdapter(Context context, List<Gruppo> gruppi){
        super (context, 0, gruppi);
    }

    @Override
    public View getView(int position, View v, ViewGroup vg){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.layout_riga, null);
        TextView txt1 = (TextView) v.findViewById(R.id.nomeGruppo);
        TextView txt2 = (TextView) v.findViewById(R.id.partecipanti);
        TextView txt3 = (TextView) v.findViewById(R.id.scadenzaSpesa);
        Gruppo g = (Gruppo) getItem(position);
        txt1.setText(g.getNome());
        txt2.setText(g.getPartecipanti());
        txt3.setText(g.getScadenza());
        return v;
    }
*/

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
        TextView textView1 = viewHolder.dataScadenza;
        textView1.setText(contact.getScadenzaData());
        TextView textView9 = viewHolder.oraScadenza;
        textView9.setText(contact.getScadenzaOra());
        TextView textView2 = viewHolder.elencoPartecipanti;
        textView2.setText(contact.getPartecipanti());
        TextView codiceGruppo = viewHolder.codiceGruppo;
        codiceGruppo.setText(contact.getCodiceGruppo());
        TextView codiceAdmin = viewHolder.codiceAdmin;
        codiceAdmin.setText(contact.getCodiceAdmin());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nomeGruppo;
        public TextView elencoPartecipanti;
        public TextView dataScadenza;
        public TextView oraScadenza;
        public TextView codiceGruppo;
        public TextView codiceAdmin;
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
            dataScadenza = (TextView) itemView.findViewById(R.id.scadenzaData);
            oraScadenza = (TextView) itemView.findViewById(R.id.scadenzaOra);
            codiceGruppo = (TextView) itemView.findViewById(R.id.codiceGruppo);
            codiceAdmin = (TextView) itemView.findViewById(R.id.codiceAdmin);
        }
    }

}
