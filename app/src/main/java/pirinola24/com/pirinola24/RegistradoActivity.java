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
import android.util.Log;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pirinola24.com.pirinola24.adaptadores.AdaptadorSpinnerFormaPago;
import pirinola24.com.pirinola24.basededatos.AdminSQliteOpenHelper;
import pirinola24.com.pirinola24.modelo.Pedido;
import pirinola24.com.pirinola24.modelo.Usuario;
import pirinola24.com.pirinola24.util.FontCache;

public class RegistradoActivity extends AppCompatActivity implements View.OnClickListener {

    private String font_path="font/A_Simple_Life.ttf";

    private ImageView btnFlechaAtras;
    private Spinner spDireccion;
    private ImageView btnAgregarDireccion;
    private Spinner spTelefono;
    private ImageView btnAgregarTelefono;
    private Spinner spFormaPago;
    private EditText txtObservaciones;
    private ImageView btnEnviarPedido;
    private TextView txtSinConexion;
    private Button btnVolverCargar;
    private AdaptadorSpinnerFormaPago adapter;
    private AdaptadorSpinnerFormaPago adapterDireccion;
    private AdaptadorSpinnerFormaPago adapterTelefono;
    private List <String> listaDirecciones;
    private List<String> listaTelfonos;
    private ProgressDialog pd = null;
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
        txtObservaciones=(EditText)findViewById(R.id.txt_observaciones);
        btnEnviarPedido=(ImageView)findViewById(R.id.btn_enviar_pedido);
        txtSinConexion=(TextView)findViewById(R.id.txt_sin_conexion);
        btnVolverCargar=(Button)findViewById(R.id.btn_volver_cargar);

        Typeface TF = FontCache.get(font_path,this);

        txtObservaciones.setTypeface(TF);
        txtSinConexion.setTypeface(TF);
        btnVolverCargar.setTypeface(TF);

        findViewById(R.id.espacio_spinner_direccion).setVisibility(View.GONE);
        findViewById(R.id.espacio_spinner_telefono).setVisibility(View.GONE);
        txtSinConexion.setVisibility(View.GONE);
        btnVolverCargar.setVisibility(View.GONE);
        findViewById(R.id.scrollView_observaciones).setVisibility(View.GONE);
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
        String nombre=ParseUser.getCurrentUser().getString(Usuario.NOMBRE);
        int indiceDireccion=spDireccion.getSelectedItemPosition();
        int indiceTelefono=spTelefono.getSelectedItemPosition();
        int indiceFormaPago=spFormaPago.getSelectedItemPosition();
        String observaciones=txtObservaciones.getText().toString();
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
            evptask.execute(nombre,direccion,telefono,observaciones,formaPago);
        }

    }

    class EnviarPedidoTask extends AsyncTask<String, Void, Boolean>
    {

        private String nombre;
        private String direccion;
        private String telefono;
        private String observaciones;
        private String formaPago;

        @Override
        protected Boolean doInBackground(String... params)
        {
            nombre=params[0];
            direccion=params[1];
            telefono=params[2];
            observaciones=params[3];
            formaPago=params[4];
            return hayConexionInternet();
        }

        @Override
        protected void onPostExecute(Boolean resultado)
        {
            super.onPostExecute(resultado);
            if(resultado)
            {
                enviarParse(nombre,direccion,telefono,observaciones,formaPago);
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

    private void enviarParse(String nombre,String direccion,String telefono,String observaciones,String formaPago)
    {
        /*final ParseObject pedido = new ParseObject(Pedido.TABLA);
        pedido.put(Pedido.PERSONANOMBRE, nombre);
        pedido.put(Pedido.DIRECCION, direccion);
        pedido.put(Pedido.TELEFONO, telefono);
        pedido.put(Pedido.FORMAPAGO, formaPago);
        pedido.put(Pedido.OBSERVACIONES, observaciones);
        pedido.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(getApplicationContext(), "admin", null, 1);
                    SQLiteDatabase db = admin.getReadableDatabase();

                    Cursor fila = db.rawQuery("select prodid,prodcantidad from pedido", null);
                    if (fila != null) {

                        if (fila.moveToFirst()) {
                            do {
                                ParseObject itempedido = new ParseObject(Pedido.TABLAITEMPEDIDO);
                                itempedido.put(Pedido.TBLITEMPEDIDO_PEDIDO, pedido.getObjectId());
                                itempedido.put(Pedido.TBLITEMPEDIDO_PRODUCTO, fila.getString(fila.getColumnIndex("prodid")));
                                itempedido.put(Pedido.TBLITEMPEDIDO_CANTIDAD, fila.getInt(fila.getColumnIndex("prodcantidad")));
                                itempedido.saveInBackground();


                            } while (fila.moveToNext());
                            setResult(Activity.RESULT_OK);
                            if (pd != null) {
                                pd.dismiss();
                            }
                            finish();
                        }
                    }
                } else {
                    if (pd != null) {
                        pd.dismiss();
                    }
                    mostrarMensaje(R.string.compruebe_conexion);
                }
            }
        });*/
    }

    private void agregarTelefono()
    {
        dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.template_agregar_telefono);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.borde_template_descripcion_producto);


        Typeface TF = FontCache.get(font_path,this);
        ImageView btnAceptar=(ImageView)dialog.findViewById(R.id.btn_aceptar);
        ImageView btnCancelar=(ImageView)dialog.findViewById(R.id.btn_cancelar);
        final EditText txtTelefono =(EditText)dialog.findViewById(R.id.txt_telefono);

        txtTelefono.setTypeface(TF);
        final Context context=this;

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String telefono=txtTelefono.getText().toString();

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
                pd = ProgressDialog.show(this, getResources().getString(R.string.txt_enviando), getResources().getString(R.string.por_favor_espere), true, false);
                GuardarTelefono gt= new GuardarTelefono();
                gt.execute(telefono);
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
                guardarTelefonoParse(telefono);
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

    public void guardarTelefonoParse(String telefono)
    {
        Log.i("Entro","parse telefono");
        final ParseObject objtelefono= new ParseObject(Usuario.TABLATELEFONO);
        objtelefono.put(Usuario.TBLTELEFONONUMERO,telefono);
        objtelefono.put(Usuario.TBLTELEFONOUSER,ParseUser.getCurrentUser().getObjectId());
        objtelefono.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e)
            {
                if(e==null)
                {
                    if(pd!=null)
                    {
                        pd.dismiss();
                    }
                    mostrarMensaje(R.string.txt_registro_exitoso);
                    dialog.dismiss();
                    listaTelfonos.add(objtelefono.getString(Usuario.TBLTELEFONONUMERO));
                    adapterTelefono.notifyDataSetChanged();
                    spTelefono.setSelection(adapterTelefono.getCount()-1);
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
        });
    }

    private void agregarDireccion()
    {
        dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.template_agregar_direccion);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.borde_template_descripcion_producto);


        Typeface TF = FontCache.get(font_path,this);
        ImageView btnAceptar=(ImageView)dialog.findViewById(R.id.btn_aceptar);
        ImageView btnCancelar=(ImageView)dialog.findViewById(R.id.btn_cancelar);
        final EditText txtdireccion =(EditText)dialog.findViewById(R.id.txt_direccion);
        final EditText txtbarrio=(EditText)dialog.findViewById(R.id.txt_barrio);
        txtdireccion.setTypeface(TF);
        txtbarrio.setTypeface(TF);
        final Context context=this;

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
            pd = ProgressDialog.show(this, getResources().getString(R.string.txt_enviando), getResources().getString(R.string.por_favor_espere), true, false);
            GuardarDireccion gd= new GuardarDireccion();
            gd.execute(direccion,barrio);
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
                guardarDireccionParse(direccion,barrio);
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

    public void guardarDireccionParse(String direccion,String barrio)
    {
        final ParseObject objdireccion= new ParseObject(Usuario.TABLADIRECCION);
        objdireccion.put(Usuario.TBLDIRECCIONDIRECCION,direccion+" "+barrio);
        objdireccion.put(Usuario.TBLDIRECCIONUSER,ParseUser.getCurrentUser().getObjectId());
        objdireccion.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e)
            {
                if(e==null)
                {
                    if(pd!=null)
                    {
                        pd.dismiss();
                    }
                    mostrarMensaje(R.string.txt_registro_exitoso);
                    dialog.dismiss();
                    listaDirecciones.add(objdireccion.getString(Usuario.TBLDIRECCIONDIRECCION));
                    adapterDireccion.notifyDataSetChanged();
                    spDireccion.setSelection(adapterDireccion.getCount()-1);
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
                cargarDatosParse();
            }
            else
            {
                pd.dismiss();
                txtSinConexion.setVisibility(View.VISIBLE);
                btnVolverCargar.setVisibility(View.VISIBLE);
            }
        }
    }

    private void cargarDatosParse()
    {
        if(pd!=null)
        {
            pd.dismiss();
        }
        /*final Context context =this;
        final ParseUser currentuser= ParseUser.getCurrentUser();
        ParseQuery<ParseObject> queryDireccion= new ParseQuery<>(Usuario.TABLADIRECCION);
        queryDireccion.whereEqualTo(Usuario.TBLDIRECCIONUSER,currentuser.getObjectId());
        queryDireccion.selectKeys(Arrays.asList(Usuario.TBLDIRECCIONDIRECCION));
        queryDireccion.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> direcciones, ParseException e)
            {
                if(e==null)
                {
                    ParseQuery <ParseObject> queryTelefono= new ParseQuery<>(Usuario.TABLATELEFONO);
                    queryTelefono.whereEqualTo(Usuario.TBLTELEFONOUSER,currentuser.getObjectId());
                    queryTelefono.selectKeys(Arrays.asList(Usuario.TBLTELEFONONUMERO));
                    queryTelefono.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> telefonos, ParseException e)
                        {
                            if(e==null)
                            {
                                listaDirecciones=new ArrayList<>();
                                listaDirecciones.add(getResources().getString(R.string.txt_seleccione_direccion));
                                for(ParseObject dir: direcciones)
                                {
                                    listaDirecciones.add(dir.getString(Usuario.TBLDIRECCIONDIRECCION));
                                }
                                adapterDireccion=new AdaptadorSpinnerFormaPago(context,R.layout.template_spinner_forma_pago,listaDirecciones);
                                spDireccion.setAdapter(adapterDireccion);


                                listaTelfonos=new ArrayList<>();
                                listaTelfonos.add(getResources().getString(R.string.txt_seleccione_telefono));
                                for(ParseObject tel: telefonos)
                                {
                                    listaTelfonos.add(tel.getString(Usuario.TBLTELEFONONUMERO));
                                }
                                adapterTelefono=new AdaptadorSpinnerFormaPago(context,R.layout.template_spinner_forma_pago,listaTelfonos);
                                spTelefono.setAdapter(adapterTelefono);

                                if(pd!=null)
                                {
                                    pd.dismiss();
                                }

                                findViewById(R.id.espacio_spinner_direccion).setVisibility(View.VISIBLE);
                                findViewById(R.id.espacio_spinner_telefono).setVisibility(View.VISIBLE);
                                findViewById(R.id.scrollView_observaciones).setVisibility(View.VISIBLE);
                                btnEnviarPedido.setVisibility(View.VISIBLE);
                                findViewById(R.id.icon_sp_forma_pago).setVisibility(View.VISIBLE);
                                spFormaPago.setVisibility(View.VISIBLE);

                            }
                            else
                            {
                                if(pd!=null)
                                {
                                    pd.dismiss();
                                    txtSinConexion.setVisibility(View.VISIBLE);
                                    btnVolverCargar.setVisibility(View.VISIBLE);

                                }
                            }
                        }
                    });
                }
                else
                {
                    if(pd!=null)
                    {
                        pd.dismiss();
                        txtSinConexion.setVisibility(View.VISIBLE);
                        btnVolverCargar.setVisibility(View.VISIBLE);
                    }

                }

            }
        });*/
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
