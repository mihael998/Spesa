package ivancardillo.spesa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by ivanc on 18/09/2017.
 */

public class Gruppo implements Serializable {
    private String nome;
    private String codiceGruppo;
    private String codiceAdmin;
    private ArrayList<String> utenti = null;
    private String scadenzaData;
    private String imagePath;


    public Gruppo(String nome, ArrayList<String> utenti, String scadenzaData, String admin, String gruppo,String imagePath) {
        this.nome = nome;
        this.utenti = utenti;
        this.scadenzaData = scadenzaData;
        this.codiceAdmin = admin;
        this.codiceGruppo = gruppo;
        this.imagePath=imagePath;
    }

    public Gruppo() {
        this.nome = "";
        this.codiceAdmin = "";
        this.codiceGruppo = "";
        this.utenti = null;
        this.scadenzaData = "";
        this.imagePath="";
    }

    public ArrayList<String> getUtenti() {
        return utenti;
    }
    public void setUtenti(ArrayList<String> utenti){
        this.utenti=utenti;
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


    public String getScadenzaData() {
        return scadenzaData;
    }

    public String getCodiceGruppo() {
        return codiceGruppo;
    }

    public String getCodiceAdmin() {
        return codiceAdmin;
    }
    public String getImagePath(){return this.imagePath;}
    public void setImagePath(String imagePath){this.imagePath=imagePath;}

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Gruppo)) {
            return false;
        }
        Gruppo user = (Gruppo) o;
        return  Objects.equals(nome, user.nome) &&
                Objects.equals(codiceGruppo, user.codiceGruppo)&&
                Objects.equals(codiceAdmin,user.codiceAdmin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome,codiceAdmin,codiceGruppo);
    }

}
