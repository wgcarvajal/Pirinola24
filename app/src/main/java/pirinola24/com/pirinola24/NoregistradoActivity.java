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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.Messaging;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import pirinola24.com.pirinola24.adaptadores.AdaptadorSpinnerCiudad;
import pirinola24.com.pirinola24.adaptadores.AdaptadorSpinnerFormaPago;
import pirinola24.com.pirinola24.basededatos.AdminSQliteOpenHelper;
import pirinola24.com.pirinola24.modelo.Ciudad;
import pirinola24.com.pirinola24.modelo.Formapago;
import pirinola24.com.pirinola24.modelo.Itempedido;
import pirinola24.com.pirinola24.modelo.Pedido;
import pirinola24.com.pirinola24.modelo.Producto;
import pirinola24.com.pirinola24.util.AppUtil;
import pirinola24.com.pirinola24.util.FontCache;

public class NoregistradoActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private String font_path="font/A_Simple_Life.ttf";
    private String fontStackyard="font/Stackyard.ttf";
    private String matura_mt="font/matura_mt.ttf";

    private Spinner spFormapago;
    private Spinner spCiudad;
    private TextView textNombre;
    private TextView textDireccion;
    private TextView textBarrio;
    private TextView textTelefono;
    private TextView observaciones;
    private TextView titulo;
    private ImageView iconospinerciudad;
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
        iconospinerciudad=(ImageView)findViewById(R.id.iconospinerciudad);
        observaciones=(TextView)findViewById(R.id.txt_observaciones);

        textNombre=(TextView)findViewById(R.id.txt_nombre);
        textDireccion=(TextView)findViewById(R.id.txt_direccion);
        textBarrio=(TextView)findViewById(R.id.txt_barrio);
        textTelefono=(TextView)findViewById(R.id.txt_telefono);
        sinconexion=(TextView)findViewById(R.id.txt_sin_conexion);
        titulo=(TextView)findViewById(R.id.titulonoregistrado);


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
        observaciones.setTypeface(TF);
        sinconexion.setTypeface(TF);

        TF=FontCache.get(matura_mt,this);
        btnEnviarPedido.setTypeface(TF);
        volver_cargar.setTypeface(TF);
        titulo.setTypeface(TF);


        String [] objetos= getResources().getStringArray(R.array.forma_pago);

        int tam= objetos.length;
        List<String> list=new ArrayList<>();
        for(int i=0;i<tam;i++)
        {
            list.add(objetos[i]);
        }
        adapter=new AdaptadorSpinnerFormaPago(this,R.layout.template_spinner_forma_pago,list);
        spFormapago.setAdapter(adapter);
        spCiudad.setOnItemSelectedListener(this);

        cargarCiudades();

    }

    private void cargarCiudades()
    {
        scrollgeneral.setVisibility(View.GONE);
        spCiudad.setVisibility(View.GONE);
        sinconexion.setVisibility(View.GONE);
        volver_cargar.setVisibility(View.GONE);
        iconospinerciudad.setVisibility(View.GONE);
        pd = ProgressDialog.show(this, getResources().getString(R.string.txt_cargando_datos), getResources().getString(R.string.por_favor_espere), true, false);
        CargarCiudadesTask cargarciudades = new CargarCiudadesTask();
        cargarciudades.execute();
    }

    private void cargarCiudadesBackendless()
    {
        final Context c= this;
        BackendlessDataQuery dataQueryciudades= new BackendlessDataQuery();
        List<String>ciudadSelect=new ArrayList<>();
        ciudadSelect.add("objectId");
        ciudadSelect.add("nombre");
        ciudadSelect.add("email");
        dataQueryciudades.setProperties(ciudadSelect);
        dataQueryciudades.setWhereClause("activado = TRUE");
        Backendless.Persistence.of(Ciudad.class).find(dataQueryciudades,new AsyncCallback<BackendlessCollection<Ciudad>>() {
            @Override
            public void handleResponse(BackendlessCollection<Ciudad> response) {
                List<Ciudad> ciudades = response.getData();
                List<Ciudad> listaCiudades = new ArrayList<>();
                Ciudad seleccioneCiudad = new Ciudad();
                seleccioneCiudad.setNombre("Seleccione una ciudad");
                listaCiudades.add(seleccioneCiudad);
                for (Ciudad ciu : ciudades) {
                    listaCiudades.add(ciu);
                }
                adaptadorCiudad = new AdaptadorSpinnerCiudad(c, R.layout.template_spinner_forma_pago, listaCiudades);
                spCiudad.setAdapter(adaptadorCiudad);
                spCiudad.setVisibility(View.VISIBLE);
                iconospinerciudad.setVisibility(View.VISIBLE);
                if (pd != null) {
                    pd.dismiss();
                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                volver_cargar.setVisibility(View.VISIBLE);
                sinconexion.setVisibility(View.VISIBLE);
                if (pd != null) {
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

    private void enviarBackendless(String nombre,String direccion,String barrio,String telefono,String formaPago,String observacion,String idciudad, final String correo, final String ciudad)
    {
        Pedido pedido= new Pedido();
        pedido.setPeddireccion(direccion+" "+barrio);
        pedido.setPedformapago(formaPago);
        pedido.setPedtelefono(telefono);
        pedido.setPedpersonanombre(nombre);
        pedido.setPedobservaciones(observacion);
        pedido.setCiudad(idciudad);

        Backendless.Persistence.save(pedido, new AsyncCallback<Pedido>() {
            @Override
            public void handleResponse(Pedido response)
            {
                Messaging.DEVICE_ID=response.getObjectId();
                Backendless.Messaging.registerDevice("892757423063", new AsyncCallback<Void>()
                {
                    @Override
                    public void handleResponse(Void response) {
                        Log.i("device:", "registrado");
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.i("device error:",fault.getMessage());
                    }
                });

                ArrayList<String> recipients = new ArrayList<String>();
                recipients.add(correo);
                String asunto = "Nuevo pedido";
                String fp;
                if (response.getPedformapago().equals("tc")) {
                    fp = "Tarjeta";
                } else {
                    fp = "Efectivo";
                }
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ft.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));

                String mailBody = "<h1>Nuevo pedido</h1>" +
                        "<b>Fecha y hora : </b>" + ft.format(response.getCreated()) + "<br>" +
                        "<b>Nombre cliente : </b>" + response.getPedpersonanombre() + "<br>" +
                        "<b>Teléfono : </b>" + response.getPedtelefono() + "<br>" +
                        "<b>Dirección : </b>" + response.getPeddireccion() + "<br>" +
                        "<b>Ciudad : </b>" + ciudad + "<br>" +
                        "<b>Forma de Pago : </b>" + fp + "<br>" +
                        "<b>Observaciones : </b>" + response.getPedobservaciones() + "<br><br><br>" +
                        "<table border='1'>" +
                        "<tr>" +
                        "<th>Producto</th>" +
                        "<th>cantidad</th>" +
                        "<th>Precio</th>" +
                        "<th>Total</th>" +
                        "</tr>";

                int totalPedido = 0;
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
                            Producto producto = new Producto();

                            for (Producto p : AppUtil.data) {
                                if (p.getObjectId().equals(fila.getString(fila.getColumnIndex("prodid")))) {
                                    producto = p;
                                    break;
                                }
                            }
                            int total = producto.getPrecio() * fila.getInt(fila.getColumnIndex("prodcantidad"));
                            totalPedido = totalPedido + total;
                            mailBody = mailBody +
                                    "<tr>" +
                                    "<td>" + producto.getProdnombre() + "</td>" +
                                    "<td>" + fila.getInt(fila.getColumnIndex("prodcantidad")) + "</td>" +
                                    "<td>" + producto.getPrecio() + "</td>" +
                                    "<td>" + total + "</td>" +
                                    "</tr>";
                        } while (fila.moveToNext());

                        mailBody = mailBody + "</table>" +
                                "<h2>Costo domicilio :  </h2> 2.000" +
                                "<h2>Subtotal :  </h2>" + totalPedido +
                                "<h2>Total Pedido:</h2>" + (totalPedido + 2000);
                        Backendless.Messaging.sendHTMLEmail(asunto, mailBody, recipients, new AsyncCallback<Void>() {
                            @Override
                            public void handleResponse(Void response) {

                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                    Log.i("error mensaje:",fault.getMessage());
                            }
                        });

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
                Log.i("error al eviar:",fault.getMessage());
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
        String idCiudad=((Ciudad)spCiudad.getSelectedItem()).getObjectId();
        String correo=((Ciudad)spCiudad.getSelectedItem()).getEmail();
        String ciudad=((Ciudad)spCiudad.getSelectedItem()).getNombre();
        String observacion=observaciones.getText().toString();
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
                    String formaPago=((Formapago)spFormapago.getSelectedItem()).getAbreviatura();
                    pd = ProgressDialog.show(this, getResources().getString(R.string.enviando_pedido), getResources().getString(R.string.por_favor_espere), true, false);
                    EnviarPedidoTask env = new EnviarPedidoTask();
                    env.execute(nombre, direccion, barrio, telefono,formaPago,observacion,idCiudad,correo,ciudad);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if(position==0)
        {
            scrollgeneral.setVisibility(View.GONE);
        }
        else
        {
            scrollgeneral.setVisibility(View.GONE);
            cargarFormulario(((Ciudad)spCiudad.getSelectedItem()).getObjectId());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void cargarFormulario(String ciudadid)
    {
        BackendlessDataQuery dataQueryformapago= new BackendlessDataQuery();
        List<String>formapagoSelect=new ArrayList<>();
        formapagoSelect.add("objectId");
        formapagoSelect.add("nombre");
        formapagoSelect.add("abreviatura");
        dataQueryformapago.setProperties(formapagoSelect);
        dataQueryformapago.setWhereClause("activado = TRUE AND ciudad='"+ciudadid+"'");
        pd = ProgressDialog.show(this, getResources().getString(R.string.txt_cargando_vista), getResources().getString(R.string.por_favor_espere), true, false);

        final Context context= this;
        Backendless.Persistence.of(Formapago.class).find(dataQueryformapago, new AsyncCallback<BackendlessCollection<Formapago>>() {
            @Override
            public void handleResponse(final BackendlessCollection<Formapago> fp) {



                Formapago fm = new Formapago();
                fm.setNombre("Seleccione forma de pago");
                List<Formapago> lista = new ArrayList<>();
                lista.add(fm);
                for (Formapago fpago : fp.getData()) {
                    lista.add(fpago);
                }
                adapter = new AdaptadorSpinnerFormaPago(context, R.layout.template_spinner_forma_pago, lista);
                spFormapago.setAdapter(adapter);
                scrollgeneral.setVisibility(View.VISIBLE);
                if (pd != null) {
                    pd.dismiss();
                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                if (pd != null) {
                    pd.dismiss();
                }
                sinconexion.setVisibility(View.VISIBLE);
                volver_cargar.setVisibility(View.VISIBLE);
                spCiudad.setVisibility(View.GONE);
                iconospinerciudad.setVisibility(View.GONE);
            }
        });
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
        private String observacion;
        private String idciudad;
        private String correo;
        private String ciudad;

        @Override
        protected Boolean doInBackground(String... params)
        {
            nombre=params[0];
            direccion=params[1];
            barrio=params[2];
            telefono=params[3];
            formaPago=params[4];
            observacion=params[5];
            idciudad=params[6];
            correo=params[7];
            ciudad=params[8];
            return hayConexionInternet();
        }

        @Override
        protected void onPostExecute(Boolean resultado)
        {
            super.onPostExecute(resultado);
            if(resultado)
            {
                enviarBackendless(nombre, direccion, barrio, telefono, formaPago,observacion,idciudad,correo,ciudad);
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
