package groups.kma.sharelocation.LienKetAction;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import groups.kma.sharelocation.R;

public class LienKetActivity extends Fragment {
    private View view;
    private TextView malienket;
    private Button btnCreate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_lien_ket, container, false);
        malienket = view.findViewById(R.id.malienket);
        btnCreate = view.findViewById(R.id.btnCreate);
        CreateRandom();
        return view;
    }
    // random key
    public void CreateRandom(){
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rnd = new Random();
                int numLetters = 10;

                String randomLetters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

                String text1 = ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()))+
                                ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()))+
                                ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()))+
                                ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()));
                String text2 = ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()))+
                        ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()))+
                        ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()))+
                        ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()));
                String text3 = ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()))+
                        ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()))+
                        ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()))+
                        ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()));
                String text4 = ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()))+
                        ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()))+
                        ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()))+
                        ""+randomLetters.charAt(rnd.nextInt(randomLetters.length()));
                malienket.setText(text1+"-"+text2+"-"+text3+"-"+text4);
            }
        });

    }

}
