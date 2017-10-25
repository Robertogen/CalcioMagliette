package org.zefiro.calciomagliette;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class CalcioMagliette_MainActivity extends AppCompatActivity {

    //tag del logger
    private static final String TAG = "Magliette calcio Activity";

    private List<String> fileNameList;
    private List<String> quizShirtList;
    private Map<String, Boolean> leagueMap;
    private String rispostaCorretta;
    private int rispostaData;
    private int rispostaGiusta;
    private int righeSquadre;
    private Random random;
    private Handler handler;//utilizzato per posticipare l'invocazione di un metodo(prendi quello di ios)
    private Animation animation;//contiene l'animazione della maglia (shake)

    //i riferimenti dei componenti grafici
    private TextView lblRisposta;
    private TextView lblNumeroDomanda;
    private ImageView imgShirt;
    private TableLayout buttonTableLayout;

    //riferimenti sound
    private SoundPool soundPool;
    private Map<Integer, Integer> soundMap;
    private final static int CORRECT_SOUND = 0;
    private final static int FAIL_SOUND = 1;
    private final static int NUMBER_OF_SHIRT = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcio_magliette__main);


        fileNameList = new ArrayList<>();
        quizShirtList = new ArrayList<>();
        leagueMap = new HashMap<>();
        righeSquadre = 1;
        random = new Random();
        handler = new Handler();


        //carico l'animazione
        animation = AnimationUtils.loadAnimation(this, R.anim.shake);
        animation.setRepeatCount(3);

        //carico i nomi delle leghe disponibili da strings.xml
        String[] shirtNames = getResources().getStringArray(R.array.shirtList);

        //per default tutte le leghe sono accese
        for (String lega : shirtNames) {
            leagueMap.put(lega, true);
        }

        lblRisposta = (TextView) findViewById(R.id.lblRisposta);
        lblNumeroDomanda=(TextView)findViewById(R.id.lblNumeroDomanda);
        imgShirt=(ImageView)findViewById(R.id.imgShirt);
        buttonTableLayout=(TableLayout)findViewById(R.id.buttonTableLayout);
        lblNumeroDomanda.setText("Domanda" + "1" + "di" + " " +  NUMBER_OF_SHIRT);

        //gestione suoni
        soundPool=new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        soundMap=new HashMap<>();
        soundMap.put(CORRECT_SOUND, soundPool.load(this,R.raw.correct,1));
        soundMap.put(FAIL_SOUND, soundPool.load(this,R.raw.fail,1));
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        resetQuiz();



    }

    //metodo che avvia o riavvia una partita
    private  void resetQuiz()
    {
        //carico le immagini delle magliette
        AssetManager assets=getAssets();

        fileNameList.clear();
        try {
            Set<String> leghe=leagueMap.keySet();
            for(String lega:leghe)
            if (leagueMap.get(lega))
            {
                String[] paths=assets.list(lega);
                for (String path:paths)
                {
                    fileNameList.add(path.replace(".png", ""));
                }
            }

        }catch (Exception ex)
        {
            Log.e(TAG, "Errore nel caricamento delle magliette", ex);
        }

        rispostaData=0;
        rispostaGiusta=0;
        quizShirtList.clear();


        //aggiungo le 5 maglie casuali al gioco
        int shirtCounter=1;
        int numberOfShirt=fileNameList.size();

        while (shirtCounter<=NUMBER_OF_SHIRT)
        {
            int randomIndex=random.nextInt(numberOfShirt);
            String fileName=fileNameList.get(randomIndex);

            if(!quizShirtList.contains(fileName)) {

                quizShirtList.add(fileName);
                shirtCounter++;

            }
        }
        loadNextShirt();

    }

    //recupero il nome della maglia da visualizzare
    //e le rimuovo dalla lista

    private void loadNextShirt() {

        String nextImage=quizShirtList.remove(0);
        rispostaCorretta=nextImage;


        //cancello il vecchio messaggio
        lblNumeroDomanda.setText("");

        //aggiorno la label con il testo domanda x di y
        lblNumeroDomanda.setText("Domanda" +
                (rispostaGiusta+1) + " " + "di" + " " + NUMBER_OF_SHIRT);

        //estraggo la lega dal nome della maglia
        String lega=nextImage.substring(0, nextImage.indexOf('-'));

        //uso l'assetManger per caricare il file immagine
        AssetManager assets=getAssets();

        InputStream stream;

        try {stream=assets.open(lega + "/" + nextImage + ".png");
            Drawable shirt=Drawable.createFromStream(stream, nextImage);
            imgShirt.setImageDrawable(shirt);

        }catch (Exception e)
        {
            Log.e(TAG, "Errore nel caricamento della maglia:" + nextImage, e);
        }

        //rimuovo i bottoni dal layout
        for (int row=0; row<buttonTableLayout.getChildCount(); row++)
        {
            ((TableRow)buttonTableLayout.getChildAt(row)).removeAllViews();
        }

        //mischio le maglie che compongono la partita
        Collections.shuffle(fileNameList);
        //sposto la risposta giusta in coda alla collection
        int correct=fileNameList.indexOf(rispostaCorretta);
        fileNameList.add(fileNameList.remove(correct));

        //ottengo un riferiemnto al servizio di Inflating









    }


}

