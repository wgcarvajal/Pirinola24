package pirinola24.com.pirinola24;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.List;

import pirinola24.com.pirinola24.adaptadores.AdaptadorCiudad;
import pirinola24.com.pirinola24.modelo.Ciudad;
import pirinola24.com.pirinola24.util.FontCache;

public class SeleccionarciudadActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public final static int MI_REQUEST_CODE_REGISTRADO = 2;
    private ListView listaCiudades;
    private AdaptadorCiudad adaptador;
    private List<Ciudad> data = new ArrayList<>();
    private TextView titulo;
    private TextView seleccionarCiudad;
    private ImageView flechaAtras;
    private String fontStackyard="font/Stackyard.ttf";
    private TextView sinconexion;
    private Button recargarvista;
    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionarciudad);

        listaCiudades=(ListView)findViewById(R.id.listaciudades);
        titulo=(TextView)findViewById(R.id.idlistaciudades);
        sinconexion=(TextView)findViewById(R.id.txt_sin_conexion);
        recargarvista=(Button)findViewById(R.id.btn_volver_cargar);
        seleccionarCiudad=(TextView)findViewById(R.id.seleccionarCiudad);
        flechaAtras=(ImageView)findViewById(R.id.flecha_atras);


        Typeface TF= FontCache.get(fontStackyard,this);
        titulo.setTypeface(TF);
        seleccionarCiudad.setTypeface(TF);
        sinconexion.setTypeface(TF);
        recargarvista.setTypeface(TF);
        recargarvista.setOnClickListener(this);
        flechaAtras.setOnClickListener(this);

        titulo.setVisibility(View.GONE);
        seleccionarCiudad.setVisibility(View.GONE);
        recargarvista.setVisibility(View.GONE);
        sinconexion.setVisibility(View.GONE);
        listaCiudades.setVisibility(View.GONE);

        listaCiudades.setOnItemClickListener(this);

        adaptador= new AdaptadorCiudad(this,data);
        listaCiudades.setAdapter(adaptador);
        loadCiudades();
    }

    private void loadCiudades()
    {
        pd = ProgressDialog.show(this, "", getResources().getString(R.string.por_favor_espere), true, false);
        BackendlessDataQuery dataQueryciudades= new BackendlessDataQuery();
        List<String>ciudadSelect=new ArrayList<>();
        ciudadSelect.add("objectId");
        ciudadSelect.add("nombre");
        ciudadSelect.add("email");
        dataQueryciudades.setProperties(ciudadSelect);
        dataQueryciudades.setWhereClause("activado = TRUE");
        Backendless.Persistence.of(Ciudad.class).find(dataQueryciudades,new AsyncCallback<BackendlessCollection<Ciudad>>() {
        @Override
        public void handleResponse(BackendlessCollection<Ciudad> response)
        {
            if(pd!=null)
            {
                pd.dismiss();
            }
            for(Ciudad ciu:response.getData())
            {
                data.add(ciu);
            }
            adaptador.notifyDataSetChanged();
            titulo.setVisibility(View.VISIBLE);
            seleccionarCiudad.setVisibility(View.VISIBLE);
            recargarvista.setVisibility(View.GONE);
            sinconexion.setVisibility(View.GONE);
            listaCiudades.setVisibility(View.VISIBLE);
        }

        @Override
        public void handleFault(BackendlessFault fault)
        {
            if(pd!=null)
            {
                pd.dismiss();
            }
            recargarvista.setVisibility(View.VISIBLE);
            sinconexion.setVisibility(View.VISIBLE);
        }
    });
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_volver_cargar:
                loadCiudades();
            break;

            case R.id.flecha_atras:
                finish();
            break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent intent = new Intent(this, RegistradoActivity.class);
        intent.putExtra("ciudad", data.get(position));
        startActivityForResult(intent,MI_REQUEST_CODE_REGISTRADO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==MI_REQUEST_CODE_REGISTRADO)
        {
            if(resultCode==RESULT_OK)
            {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        }
    }
}
