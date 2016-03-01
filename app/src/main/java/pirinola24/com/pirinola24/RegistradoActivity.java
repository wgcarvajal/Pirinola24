package pirinola24.com.pirinola24;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.List;

import pirinola24.com.pirinola24.adaptadores.AdaptadorSpinnerFormaPago;
import pirinola24.com.pirinola24.basededatos.AdminSQliteOpenHelper;
import pirinola24.com.pirinola24.modelo.Ciudad;
import pirinola24.com.pirinola24.modelo.Direccion;
import pirinola24.com.pirinola24.modelo.Itempedido;
import pirinola24.com.pirinola24.modelo.Pedido;
import pirinola24.com.pirinola24.modelo.Telefono;
import pirinola24.com.pirinola24.util.FontCache;

public class RegistradoActivity extends AppCompatActivity implements View.OnClickListener {

    private String font_path="font/A_Simple_Life.ttf";
    private String fontStackyard="font/Stackyard.ttf";

    private ImageView btnFlechaAtras;
    private Spinner spDireccion;
    private ImageView btnAgregarDireccion;
    private Spinner spTelefono;
    private ImageView btnAgregarTelefono;
    private Spinner spFormaPago;
    private Button btnEnviarPedido;
    private TextView txtSinConexion;
    private TextView txtlugar;
    private TextView explicacion;
    private Button btnVolverCargar;
    private AdaptadorSpinnerFormaPago adapter;
    private AdaptadorSpinnerFormaPago adapterDireccion;
    private AdaptadorSpinnerFormaPago adapterTelefono;
    private List <String> listaDirecciones;
    private List<String> listaTelfonos;
    private ProgressDialog pd = null;
    private Ciudad ciudad;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrado);

        btnFlechaAtras=(ImageView)findViewById(R.id.flecha_atras);
        spDireccion=(Spinner)findViewById(R.id.sp_direccion);
        btnAgregarDireccion=(ImageView)findViewById(R.id.btn_agregar_direccion);
        spTelefono=(Spinner)findViewById(R.id.sp_telefono);
        btnAgregarTelefono=(ImageView)findViewById(R.id.btn_agregar_telefono);
        spFormaPago=(Spinner)findViewById(R.id.sp_forma_pago);
        btnEnviarPedido=(Button)findViewById(R.id.btn_enviar_pedido);
        txtSinConexion=(TextView)findViewById(R.id.txt_sin_conexion);
        btnVolverCargar=(Button)findViewById(R.id.btn_volver_cargar);
        txtlugar=(TextView)findViewById(R.id.textlugar);
        explicacion=(TextView)findViewById(R.id.txtexplicacion);

        Typeface TF=FontCache.get(fontStackyard,this);
        btnVolverCargar.setTypeface(TF);
        btnEnviarPedido.setTypeface(TF);
        txtSinConexion.setTypeface(TF);
        txtlugar.setTypeface(TF);
        explicacion.setTypeface(TF);

        findViewById(R.id.espacio_spinner_direccion).setVisibility(View.GONE);
        findViewById(R.id.espacio_spinner_telefono).setVisibility(View.GONE);
        txtSinConexion.setVisibility(View.GONE);
        btnVolverCargar.setVisibility(View.GONE);
        btnEnviarPedido.setVisibility(View.GONE);
        findViewById(R.id.icon_sp_forma_pago).setVisibility(View.GONE);
        spFormaPago.setVisibility(View.GONE);

        btnVolverCargar.setOnClickListener(this);
        btnFlechaAtras.setOnClickListener(this);
        btnAgregarDireccion.setOnClickListener(this);
        btnAgregarTelefono.setOnClickListener(this);
        btnEnviarPedido.setOnClickListener(this);


        String [] objetos= getResources().getStringArray(R.array.forma_pago);

        int tam= objetos.length;
        List<String> list=new ArrayList<>();
        for(int i=0;i<tam;i++)
        {
            list.add(objetos[i]);
        }
        adapter=new AdaptadorSpinnerFormaPago(this,R.layout.template_spinner_forma_pago,list);
        spFormaPago.setAdapter(adapter);
        ciudad= (Ciudad)getIntent().getExtras().getSerializable("ciudad");
        txtlugar.setText(ciudad.getNombre());
        loadView();
    }


    private void loadView()
    {
        pd = ProgressDialog.show(this, getResources().getString(R.string.txt_cargando_vista), getResources().getString(R.string.por_favor_espere), true, false);
        CargarVistaTask cvtask= new CargarVistaTask();
        cvtask.execute();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_volver_cargar:
                txtSinConexion.setVisibility(View.GONE);
                btnVolverCargar.setVisibility(View.GONE);
                loadView();
            break;

            case R.id.flecha_atras:
                finish();
            break;

            case R.id.btn_agregar_direccion:
                agregarDireccion();
            break;

            case R.id.btn_agregar_telefono:
                agregarTelefono();
            break;

            case R.id.btn_enviar_pedido:
                enviarPedido();
            break;
        }
    }

    private void enviarPedido()
    {
        String nombre=(String)Backendless.UserService.CurrentUser().getProperty("name");
        int indiceDireccion=spDireccion.getSelectedItemPosition();
        int indiceTelefono=spTelefono.getSelectedItemPosition();
        int indiceFormaPago=spFormaPago.getSelectedItemPosition();
        if(indiceDireccion==0 || indiceTelefono==0 || indiceFormaPago==0)
        {
            mostrarMensaje(R.string.campos_obligatorios);
        }
        else
        {
            String formaPago;
            if(indiceFormaPago==1)
            {
                formaPago="tc";
            }
            else
            {
                formaPago="ef";
            }
            String direccion= spDireccion.getSelectedItem().toString();
            String telefono=spTelefono.getSelectedItem().toString();
            pd = ProgressDialog.show(this, getResources().getString(R.string.enviando_pedido), getResources().getString(R.string.por_favor_espere), true, false);
            EnviarPedidoTask evptask= new EnviarPedidoTask();
            evptask.execute(nombre,direccion,telefono,formaPago);
        }

    }

    class EnviarPedidoTask extends AsyncTask<String, Void, Boolean>
    {

        private String nombre;
        private String direccion;
        private String telefono;
        private String formaPago;

        @Override
        protected Boolean doInBackground(String... params)
        {
            nombre=params[0];
            direccion=params[1];
            telefono=params[2];
            formaPago=params[3];
            return hayConexionInternet();
        }

        @Override
        protected void onPostExecute(Boolean resultado)
        {
            super.onPostExecute(resultado);
            if(resultado)
            {
                enviarBackendless(nombre, direccion, telefono, formaPago);
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

    private void enviarBackendless(String nombre,String direccion,String telefono,String formaPago)
    {
        Pedido pedido= new Pedido();
        pedido.setPeddireccion(direccion);
        pedido.setPedformapago(formaPago);
        pedido.setPedtelefono(telefono);
        pedido.setPedpersonanombre(nombre);
        pedido.setCiudad(ciudad.getObjectId());

        Backendless.Persistence.save(pedido, new AsyncCallback<Pedido>() {
            @Override
            public void handleResponse(Pedido response) {
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
            public void handleFault(BackendlessFault fault) {
                if (pd != null) {
                    pd.dismiss();
                }
                mostrarMensaje(R.string.compruebe_conexion);
            }
        });
    }

    private void agregarTelefono()
    {
        dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.template_agregar_telefono);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.borde_template_descripcion_producto);



        TextView btnAceptar=(TextView)dialog.findViewById(R.id.btn_aceptar);
        TextView btnCancelar=(TextView)dialog.findViewById(R.id.btn_cancelar);
        final EditText txtTelefono =(EditText)dialog.findViewById(R.id.txt_telefono);

        Typeface TF = FontCache.get(font_path,this);
        txtTelefono.setTypeface(TF);
        TF= FontCache.get(fontStackyard,this);
        btnAceptar.setTypeface(TF);
        btnCancelar.setTypeface(TF);

        final Context context=this;

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telefono = txtTelefono.getText().toString();

                guardarTelefono(telefono);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void guardarTelefono(String telefono)
    {

        if(telefono.equals(""))
        {
            mostrarMensaje(R.string.todos_campos_obligatorios);
        }
        else
        {
            if(telefono.length()<6)
            {
                mostrarMensaje(R.string.campo_telefono);
            }
            else
            {
                if(listaTelfonos.contains(telefono))
                {
                    dialog.dismiss();
                    mostrarMensaje(R.string.campo_telefono_ya_esta);
                }
                else
                {
                    pd = ProgressDialog.show(this, getResources().getString(R.string.txt_enviando), getResources().getString(R.string.por_favor_espere), true, false);
                    GuardarTelefono gt= new GuardarTelefono();
                    gt.execute(telefono);
                }


            }

        }

    }

    public class GuardarTelefono extends  AsyncTask <String,Void,Boolean>
    {
        String telefono;
        @Override
        protected Boolean doInBackground(String... params)
        {
            telefono=params[0];
            return hayConexionInternet();
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado)
            {
                guardarTelefonoBackendless(telefono);
            }
            else
            {
                if(pd!=null)
                {
                    pd.dismiss();
                }
                mostrarMensaje(R.string.compruebe_conexion);
            }
        }
    }

    public void guardarTelefonoBackendless(String telefono)
    {
        Telefono phone= new Telefono();
        phone.setIdciudad(ciudad.getObjectId());
        phone.setNumero(telefono);
        phone.setUser(Backendless.UserService.CurrentUser().getObjectId());
        Backendless.Persistence.save(phone, new AsyncCallback<Telefono>() {
            @Override
            public void handleResponse(Telefono response)
            {
                listaTelfonos.add(response.getNumero());
                adapterTelefono.notifyDataSetChanged();
                spTelefono.setSelection(adapterTelefono.getCount()-1);
                if(pd!=null)
                {
                    pd.dismiss();
                }
                mostrarMensaje(R.string.txt_registro_exitoso);
                dialog.dismiss();
            }

            @Override
            public void handleFault(BackendlessFault fault)
            {

                if(pd!=null)
                {
                    pd.dismiss();
                }
                mostrarMensaje(R.string.compruebe_conexion);
            }
        });
    }

    private void agregarDireccion()
    {
        dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.template_agregar_direccion);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.borde_template_descripcion_producto);



        TextView btnAceptar=(TextView)dialog.findViewById(R.id.btn_aceptar);
        TextView btnCancelar=(TextView)dialog.findViewById(R.id.btn_cancelar);
        final EditText txtdireccion =(EditText)dialog.findViewById(R.id.txt_direccion);
        final EditText txtbarrio=(EditText)dialog.findViewById(R.id.txt_barrio);
        Typeface TF = FontCache.get(font_path,this);
        txtdireccion.setTypeface(TF);
        txtbarrio.setTypeface(TF);
        final Context context=this;

        TF= FontCache.get(fontStackyard,this);
        btnAceptar.setTypeface(TF);
        btnCancelar.setTypeface(TF);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String direccion=txtdireccion.getText().toString();
                String barrio=txtbarrio.getText().toString();

                guardarDireccion(direccion,barrio);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void guardarDireccion(String direccion,String barrio)
    {

        if(direccion.equals("") || barrio.equals(""))
        {
            mostrarMensaje(R.string.todos_campos_obligatorios);
        }
        else
        {
            if(listaDirecciones.contains(direccion+" "+barrio))
            {
                dialog.dismiss();
                mostrarMensaje(R.string.campo_direccion_ya_esta);
            }
            else
            {
                pd = ProgressDialog.show(this, getResources().getString(R.string.txt_enviando), getResources().getString(R.string.por_favor_espere), true, false);
                GuardarDireccion gd= new GuardarDireccion();
                gd.execute(direccion,barrio);
            }

        }

    }

    public class GuardarDireccion extends  AsyncTask <String,Void,Boolean>
    {
        String direccion;
        String barrio;
        @Override
        protected Boolean doInBackground(String... params)
        {
            direccion=params[0];
            barrio=params[1];
            return hayConexionInternet();
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado)
            {
                guardarDireccionBackendless(direccion, barrio);
            }
            else
            {
                if(pd!=null)
                {
                    pd.dismiss();
                }
                mostrarMensaje(R.string.compruebe_conexion);
            }
        }
    }

    public void guardarDireccionBackendless(String direccion,String barrio)
    {

        Direccion address= new Direccion();
        address.setIdciudad(ciudad.getObjectId());
        address.setDireccion(direccion+" "+barrio);
        address.setUser(Backendless.UserService.CurrentUser().getObjectId());
        Backendless.Persistence.save(address, new AsyncCallback<Direccion>() {
            @Override
            public void handleResponse(Direccion response)
            {
                listaDirecciones.add(response.getDireccion());
                adapterDireccion.notifyDataSetChanged();
                spDireccion.setSelection(adapterDireccion.getCount()-1);
                if(pd!=null)
                {
                    pd.dismiss();
                }
                mostrarMensaje(R.string.txt_registro_exitoso);
                dialog.dismiss();
            }

            @Override
            public void handleFault(BackendlessFault fault)
            {

                if(pd!=null)
                {
                    pd.dismiss();
                }
                mostrarMensaje(R.string.compruebe_conexion);
            }
        });
    }

    public class CargarVistaTask extends AsyncTask <Void,Void,Boolean>
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
                cargarDatosBackendless();
            }
            else
            {
                pd.dismiss();
                txtSinConexion.setVisibility(View.VISIBLE);
                btnVolverCargar.setVisibility(View.VISIBLE);
            }
        }
    }

    private void cargarDatosBackendless()
    {
        BackendlessDataQuery querydireccion = new BackendlessDataQuery();
        final BackendlessUser user= Backendless.UserService.CurrentUser();
        final Context context =this;
        querydireccion.setWhereClause("user = '" + user.getObjectId() + "' AND idciudad='" + ciudad.getObjectId()+"'");
        QueryOptions optionsdireccion= new QueryOptions();
        optionsdireccion.setPageSize(100);
        querydireccion.setQueryOptions(optionsdireccion);
        Backendless.Persistence.of(Direccion.class).find(querydireccion ,new AsyncCallback<BackendlessCollection<Direccion>>() {
            @Override
            public void handleResponse(final BackendlessCollection<Direccion> direcciones)
            {
                BackendlessDataQuery querytelefono = new BackendlessDataQuery();
                querytelefono.setWhereClause("user = '" + user.getObjectId() + "' AND idciudad='" + ciudad.getObjectId()+"'");
                QueryOptions optionstelefono= new QueryOptions();
                optionstelefono.setPageSize(100);
                querytelefono.setQueryOptions(optionstelefono);
                Backendless.Persistence.of(Telefono.class).find(querytelefono, new AsyncCallback<BackendlessCollection<Telefono>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Telefono> telefonos)
                    {

                        listaDirecciones=new ArrayList<>();
                        listaDirecciones.add(getResources().getString(R.string.txt_seleccione_direccion));
                        for(Direccion dir: direcciones.getData())
                        {
                            listaDirecciones.add(dir.getDireccion());
                        }
                        adapterDireccion=new AdaptadorSpinnerFormaPago(context,R.layout.template_spinner_forma_pago,listaDirecciones);
                        spDireccion.setAdapter(adapterDireccion);
                        listaTelfonos=new ArrayList<>();
                        listaTelfonos.add(getResources().getString(R.string.txt_seleccione_telefono));
                        for(Telefono tel: telefonos.getData()) {
                            listaTelfonos.add(tel.getNumero());                        }
                        adapterTelefono=new AdaptadorSpinnerFormaPago(context,R.layout.template_spinner_forma_pago,listaTelfonos);
                        spTelefono.setAdapter(adapterTelefono);
                        findViewById(R.id.espacio_spinner_direccion).setVisibility(View.VISIBLE);
                        findViewById(R.id.espacio_spinner_telefono).setVisibility(View.VISIBLE);
                        btnEnviarPedido.setVisibility(View.VISIBLE);
                        findViewById(R.id.icon_sp_forma_pago).setVisibility(View.VISIBLE);
                        spFormaPago.setVisibility(View.VISIBLE);
                        if(pd!=null)
                        {
                            pd.dismiss();
                        }
                    }
                    @Override
                    public void handleFault(BackendlessFault fault)
                    {
                        txtSinConexion.setVisibility(View.VISIBLE);
                        btnVolverCargar.setVisibility(View.VISIBLE);
                        if(pd!=null)
                        {
                            pd.dismiss();
                        }
                    }
                });
            }
            @Override
            public void handleFault(BackendlessFault fault)
            {
                txtSinConexion.setVisibility(View.VISIBLE);
                btnVolverCargar.setVisibility(View.VISIBLE);
                if(pd!=null)
                {
                    pd.dismiss();
                }
            }
        });
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


}
