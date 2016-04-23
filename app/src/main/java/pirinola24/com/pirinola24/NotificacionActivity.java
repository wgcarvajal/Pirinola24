package pirinola24.com.pirinola24;


import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pirinola24.com.pirinola24.util.FontCache;

public class NotificacionActivity extends AppCompatActivity implements View.OnClickListener {
    private String font_path="font/A_Simple_Life.ttf";
    private String matura_mt="font/matura_mt.ttf";
    private TextView titulo;
    private TextView mensaje;
    private Button cerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion);

        titulo=(TextView)findViewById(R.id.tituloNotificacion);
        mensaje=(TextView)findViewById(R.id.mensaje);
        cerrar=(Button)findViewById(R.id.cerrar_notifiacion);

        Typeface TF=FontCache.get(matura_mt,this);
        titulo.setTypeface(TF);
        cerrar.setTypeface(TF);
        TF = FontCache.get(font_path, this);
        mensaje.setTypeface(TF);

        String contentitulo=getIntent().getStringExtra("contentitulo");
        String message= getIntent().getStringExtra("message");

        if(contentitulo!=null && message!=null)
        {
            titulo.setText(contentitulo);
            mensaje.setText(message);
        }

        cerrar.setOnClickListener(this);

    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.cerrar_notifiacion:
                finish();
            break;
        }
    }
}
