package pirinola24.com.pirinola24;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class IntroduccionActivity extends AppCompatActivity {

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduccion);
        context=this;

        IrActiviyPrincipal ir= new IrActiviyPrincipal();
        ir.execute();


    }

    class IrActiviyPrincipal extends AsyncTask<Void, Void, Void>
    {



        @Override
        protected Void doInBackground(Void... params)
        {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent= new Intent(context,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
