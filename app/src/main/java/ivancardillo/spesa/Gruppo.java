package ivancardillo.spesa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivanc on 18/09/2017.
 */

public class Gruppo implements Serializable {
    private String nome;
    private String codiceGruppo;
    private String codiceAdmin;
    private ArrayList<String> utenti = null;
    private String scadenzaOra;
    private String scadenzaData;


    public Gruppo(String nome, ArrayList<String> utenti, String scadenzaOra, String scadenzaData, String admin, String gruppo) {
        this.nome = nome;
        this.utenti = utenti;
        this.scadenzaOra = scadenzaOra;
        this.scadenzaData = scadenzaData;
        this.codiceAdmin = admin;
        this.codiceGruppo = gruppo;
    }

    public Gruppo() {
        this.nome = "";
        this.codiceAdmin = "";
        this.codiceGruppo = "";
        this.utenti = null;
        this.scadenzaOra = "";
        this.scadenzaData = "";
    }

    public ArrayList<String> getUtenti() {
        return utenti;
    }

    public String getPartecipanti() {
        String x = "";
        for (Object i : utenti.toArray()) {
            x = x + i.toString() + ", ";
        }
        x = x.substring(0, x.length() - 2);
        return x;
    }

    public String getNome() {
        return nome;
    }

    public String getScadenzaOra() {
        return scadenzaOra;
    }

    public String getScadenzaData() {
        return scadenzaData;
    }

    public String getCodiceGruppo() {
        return codiceGruppo;
    }

    public String getCodiceAdmin() {
        return codiceAdmin;
    }

}
