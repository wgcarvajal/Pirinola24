package pirinola24.com.pirinola24;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.List;

import pirinola24.com.pirinola24.adaptadores.AdaptadorSpinnerCiudad;
import pirinola24.com.pirinola24.adaptadores.AdaptadorSpinnerFormaPago;
import pirinola24.com.pirinola24.basededatos.AdminSQliteOpenHelper;
import pirinola24.com.pirinola24.modelo.Ciudad;
import pirinola24.com.pirinola24.modelo.Itempedido;
import pirinola24.com.pirinola24.modelo.Pedido;
import pirinola24.com.pirinola24.util.FontCache;

public class NoregistradoActivity extends AppCompatActivity implements View.OnClickListener
{
    private String font_path="font/A_Simple_Life.ttf";
    private String fontStackyard="font/Stackyard.ttf";

    private Spinner spFormapago;
    private Spinner spCiudad;
    private TextView textNombre;
    private TextView textDireccion;
    private TextView textBarrio;
    private TextView textTelefono;
    private ScrollView scrollgeneral;
    private ImageView btnAtras;
    private Button btnEnviarPedido;
    private Button volver_cargar;
    private TextView sinconexion;
    private AdaptadorSpinnerFormaPago adapter;
    private AdaptadorSpinnerCiudad adaptadorCiudad;
    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noregistrado);

        spFormapago=(Spinner)findViewById(R.id.sp_forma_pago);
        spCiudad=(Spinner)findViewById(R.id.sp_ciudad);
        scrollgeneral=(ScrollView)findViewById(R.id.scrollview_no_registrado);

        textNombre=(TextView)findViewById(R.id.txt_nombre);
        textDireccion=(TextView)findViewById(R.id.txt_direccion);
        textBarrio=(TextView)findViewById(R.id.txt_barrio);
        textTelefono=(TextView)findViewById(R.id.txt_telefono);
        sinconexion=(TextView)findViewById(R.id.txt_sin_conexion);



        btnAtras=(ImageView)findViewById(R.id.flecha_atras);
        btnEnviarPedido=(Button)findViewById(R.id.btn_enviar_pedido);
        volver_cargar=(Button)findViewById(R.id.volver_cargar);

        btnAtras.setOnClickListener(this);
        btnEnviarPedido.setOnClickListener(this);
        volver_cargar.setOnClickListener(this);

        Typeface TF = FontCache.get(font_path,this);
        textNombre.setTypeface(TF);
        textDireccion.setTypeface(TF);
        textBarrio.setTypeface(TF);
        textTelefono.setTypeface(TF);

        TF=FontCache.get(fontStackyard,this);
        btnEnviarPedido.setTypeface(TF);
        volver_cargar.setTypeface(TF);
        sinconexion.setTypeface(TF);


        String [] objetos= getResources().getStringArray(R.array.forma_pago);

        int tam= objetos.length;
        List<String> list=new ArrayList<>();
        for(int i=0;i<tam;i++)
        {
            list.add(objetos[i]);
        }
        adapter=new AdaptadorSpinnerFormaPago(this,R.layout.template_spinner_forma_pago,list);
        spFormapago.setAdapter(adapter);

        scrollgeneral.setVisibility(View.GONE);
        sinconexion.setVisibility(View.GONE);
        volver_cargar.setVisibility(View.GONE);
        cargarCiudades();

    }

    private void cargarCiudades()
    {
        pd = ProgressDialog.show(this, getResources().getString(R.string.txt_cargando_datos), getResources().getString(R.string.por_favor_espere), true, false);
        CargarCiudadesTask cargarciudades = new CargarCiudadesTask();
        cargarciudades.execute();
    }

    private void cargarCiudadesBackendless()
    {
        final Context c= this;
        Backendless.Persistence.of(Ciudad.class).find(new AsyncCallback<BackendlessCollection<Ciudad>>() {
            @Override
            public void handleResponse(BackendlessCollection<Ciudad> response) {
                List<Ciudad> ciudades = response.getData();
                List<String> listaCiudades = new ArrayList<>();
                listaCiudades.add("Seleccione una ciudad");
                for (Ciudad ciu : ciudades) {
                    listaCiudades.add(ciu.getNombre());
                }
                adaptadorCiudad = new AdaptadorSpinnerCiudad(c, R.layout.template_spinner_forma_pago, ciudades, listaCiudades);
                spCiudad.setAdapter(adaptadorCiudad);
                scrollgeneral.setVisibility(View.VISIBLE);
                volver_cargar.setVisibility(View.GONE);
                sinconexion.setVisibility(View.GONE);
                if(pd!=null)
                {
                    pd.dismiss();
                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                volver_cargar.setVisibility(View.VISIBLE);
                sinconexion.setVisibility(View.VISIBLE);
                if(pd!=null)
                {
                    pd.dismiss();
                }
                Log.i("error:", fault.getMessage() + " codigo:" + fault.getCode());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        textNombre.setFocusableInTouchMode(true);
        textNombre.requestFocus();
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.flecha_atras:
                finish();
            break;
            case R.id.btn_enviar_pedido:
                enviarPedido();
            break;

            case R.id.volver_cargar:
                cargarCiudades();
            break;
        }
    }

    private void enviarBackendless(String nombre,String direccion,String barrio,String telefono,String formaPago,String idciudad)
    {
        Pedido pedido= new Pedido();
        pedido.setPeddireccion(direccion+" "+barrio);
        pedido.setPedformapago(formaPago);
        pedido.setPedtelefono(telefono);
        pedido.setPedpersonanombre(nombre);
        pedido.setCiudad(idciudad);

        Backendless.Persistence.save(pedido, new AsyncCallback<Pedido>() {
            @Override
            public void handleResponse(Pedido response)
            {
                AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(getApplicationContext(), "admin", null, 1);
                SQLiteDatabase db = admin.getReadableDatabase();
                Cursor fila = db.rawQuery("select prodid,prodcantidad from pedido", null);
                if (fila != null) {

                    if (fila.moveToFirst()) {
                        do {
                            Itempedido itempedido = new Itempedido();
                            itempedido.setPedido(response.getObjectId());
                            itempedido.setProducto(fila.getString(fila.getColumnIndex("prodid")));
                            itempedido.setItemcantidad(fila.getInt(fila.getColumnIndex("prodcantidad")));
                            Backendless.Persistence.save(itempedido, new AsyncCallback<Itempedido>() {
                                @Override
                                public void handleResponse(Itempedido response) {

                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {

                                }
                            });
                        } while (fila.moveToNext());
                        setResult(Activity.RESULT_OK);
                        if (pd != null) {
                            pd.dismiss();
                        }
                        finish();
                    }
                }

            }

            @Override
            public void handleFault(BackendlessFault fault)
            {
                if (pd != null) {
                    pd.dismiss();
                }
                mostrarMensaje(R.string.compruebe_conexion);
            }
        });
    }

    private void enviarPedido()
    {

        String nombre=textNombre.getText().toString();
        String direccion=textDireccion.getText().toString();
        String barrio=textBarrio.getText().toString();
        String telefono=textTelefono.getText().toString();
        String idCiudad=(String)spCiudad.getSelectedView().findViewById(R.id.txt_item_spinner_forma_pago).getTag();
        int indiceSpinerSeleccionado= spFormapago.getSelectedItemPosition();
        if(nombre.equals("")||direccion.equals("")||telefono.equals("")||barrio.equals(""))
        {
            mostrarMensaje(R.string.campos_obligatorios);
        }
        else
        {
            if(idCiudad.equals("0"))
            {
                mostrarMensaje(R.string.seleccione_ciudad);
            }
            else
            {
                if(indiceSpinerSeleccionado==0)
                {
                    mostrarMensaje(R.string.seleccione_forma_pago);
                }
                else
                {
                    String formaPago;
                    if (indiceSpinerSeleccionado == 1) {
                        formaPago = "tc";
                    } else {
                        formaPago = "ef";
                    }

                    String google = "www.google.com.co";
                    String yahoo = "www.yahoo.com";
                    pd = ProgressDialog.show(this, getResources().getString(R.string.enviando_pedido), getResources().getString(R.string.por_favor_espere), true, false);
                    EnviarPedidoTask env = new EnviarPedidoTask();
                    env.execute(nombre, direccion, barrio, telefono, formaPago,idCiudad);
                }
            }
        }


    }

    private void mostrarMensaje(int idmensaje)
    {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.template_mensaje_toast,
                (ViewGroup) findViewById(R.id.toast_layout));

        TextView text = (TextView) layout.findViewById(R.id.txt_mensaje_toast);
        text.setText(getResources().getString(idmensaje));

        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public class CargarCiudadesTask extends  AsyncTask<Void,Void,Boolean>
    {

        @Override
        protected Boolean doInBackground(Void... params) {
            return hayConexionInternet();
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);

            if(resultado)
            {
                cargarCiudadesBackendless();
            }
            else
            {
                if(pd!=null)
                {
                    pd.dismiss();
                    volver_cargar.setVisibility(View.VISIBLE);
                    sinconexion.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public class EnviarPedidoTask extends AsyncTask<String, Void, Boolean>
    {

        private String nombre;
        private String direccion;
        private String barrio;
        private String telefono;
        private String formaPago;
        private String idciudad;

        @Override
        protected Boolean doInBackground(String... params)
        {
            nombre=params[0];
            direccion=params[1];
            barrio=params[2];
            telefono=params[3];
            formaPago=params[4];
            idciudad=params[5];
            return hayConexionInternet();
        }

        @Override
        protected void onPostExecute(Boolean resultado)
        {
            super.onPostExecute(resultado);
            if(resultado)
            {
                enviarBackendless(nombre, direccion, barrio, telefono, formaPago,idciudad);
            }
            else
            {
                if(pd!=null)
                {
                    pd.dismiss();
                    mostrarMensaje(R.string.compruebe_conexion);
                }
            }

        }
    }

    private boolean hayConexionInternet()
    {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork!=null)
        {

            return activeNetwork.isConnectedOrConnecting();
        }
        return false;

    }
}
