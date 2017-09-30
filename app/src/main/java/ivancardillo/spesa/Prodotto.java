package ivancardillo.spesa;

import android.icu.util.Calendar;
import android.media.Image;

import java.text.SimpleDateFormat;
import java.util.UUID;


/**
 * Created by ivanc on 29/09/2017.
 */

public class Prodotto {

    private String nome;
    private String note;
    private String codiceProdotto;
    private String dataRichiesta;
    private String codiceRichiedente;
    private String codiceGruppo;
    private String imgProdotto;

    public Prodotto(){
        this.nome = "";
        this.codiceRichiedente = "";
        this.codiceGruppo = "";
        this.dataRichiesta = "";
        this.codiceProdotto = UUID.randomUUID().toString().substring(0, 8);
        this.note = "";
        this.imgProdotto = "";
    }

    public Prodotto(String nome, String codiceRichiedente, String codiceGruppo, String note, String imgProdotto) {
        this.nome = nome;
        this.codiceRichiedente = codiceRichiedente;
        this.codiceGruppo = codiceGruppo;
        this.dataRichiesta = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        this.codiceProdotto = UUID.randomUUID().toString().substring(0, 8);
        this.note = note;
        this.imgProdotto = imgProdotto;
    }

    public Prodotto(String nome, String codiceRichiedente, String codiceGruppo, String note, String imgProdotto, String codiceProdotto) {
        this.nome = nome;
        this.codiceRichiedente = codiceRichiedente;
        this.codiceGruppo = codiceGruppo;
        this.dataRichiesta = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        this.codiceProdotto = codiceProdotto;
        this.note = note;
        this.imgProdotto = imgProdotto;
    }

    public String getImgProdotto() {
        return imgProdotto;
    }

    public String getCodiceGruppo() {
        return codiceGruppo;
    }

    public String getCodiceProdotto() {
        return codiceProdotto;
    }

    public String getCodiceRichiedente() {
        return codiceRichiedente;
    }

    public String getDataRichiesta() {
        return dataRichiesta;
    }

    public String getNome() {
        return nome;
    }

    public String getNote() {
        return note;
    }

    public void setCodiceGruppo(String codiceGruppo) {
        this.codiceGruppo = codiceGruppo;
    }

    public void setCodiceProdotto(String codiceProdotto) {
        this.codiceProdotto = codiceProdotto;
    }

    public void setCodiceRichiedente(String codiceRichiedente) {
        this.codiceRichiedente = codiceRichiedente;
    }

    public void setDataRichiesta(String dataRichiesta) {
        this.dataRichiesta = dataRichiesta;
    }

    public void setDataRichiesta(){
        this.dataRichiesta = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
    }

    public void setImgProdotto(String imgProdotto) {
        this.imgProdotto = imgProdotto;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
