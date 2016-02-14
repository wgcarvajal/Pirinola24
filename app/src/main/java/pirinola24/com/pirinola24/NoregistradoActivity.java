package pirinola24.com.pirinola24;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.http.ParseHttpResponse;

import java.util.ArrayList;
import java.util.List;

import bolts.Task;
import pirinola24.com.pirinola24.adaptadores.AdaptadorSpinnerFormaPago;
import pirinola24.com.pirinola24.basededatos.AdminSQliteOpenHelper;
import pirinola24.com.pirinola24.modelo.Pedido;

public class NoregistradoActivity extends AppCompatActivity implements View.OnClickListener
{
    private String font_path="font/A_Simple_Life.ttf";
    private Typeface TF;

    private Spinner spFormapago;
    private TextView textNombre;
    private TextView textDireccion;
    private TextView textBarrio;
    private TextView textTelefono;
    private TextView textObservaciones;
    private ImageView btnAtras;
    private Button btnEnviarPedido;
    private AdaptadorSpinnerFormaPago adapter;
    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noregistrado);

        spFormapago=(Spinner)findViewById(R.id.sp_forma_pago);
        textNombre=(TextView)findViewById(R.id.txt_nombre);
        textDireccion=(TextView)findViewById(R.id.txt_direccion);
        textBarrio=(TextView)findViewById(R.id.txt_barrio);
        textTelefono=(TextView)findViewById(R.id.txt_telefono);
        textObservaciones=(TextView)findViewById(R.id.txt_observaciones);



        btnAtras=(ImageView)findViewById(R.id.flecha_atras);
        btnEnviarPedido=(Button)findViewById(R.id.btn_enviar_pedido);

        btnAtras.setOnClickListener(this);
        btnEnviarPedido.setOnClickListener(this);

        TF = Typeface.createFromAsset(getAssets(), font_path);
        textNombre.setTypeface(TF);
        textDireccion.setTypeface(TF);
        textBarrio.setTypeface(TF);
        textTelefono.setTypeface(TF);
        textObservaciones.setTypeface(TF);
        btnEnviarPedido.setTypeface(TF);

        String [] objetos= getResources().getStringArray(R.array.forma_pago);

        int tam= objetos.length;
        List<String> list=new ArrayList<>();
        for(int i=0;i<tam;i++)
        {
            list.add(objetos[i]);
        }
        adapter=new AdaptadorSpinnerFormaPago(this,R.layout.template_spinner_forma_pago,list);
        spFormapago.setAdapter(adapter);
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
        }
    }

    private void enviarParse(String nombre,String direccion,String barrio,String telefono,String observaciones,String formaPago)
    {
        final ParseObject pedido = new ParseObject(Pedido.TABLA);
        pedido.put(Pedido.PERSONANOMBRE, nombre);
        pedido.put(Pedido.DIRECCION, direccion+" "+barrio);
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
        });
    }

    private void enviarPedido()
    {

        String nombre=textNombre.getText().toString();
        String direccion=textDireccion.getText().toString();
        String barrio=textBarrio.getText().toString();
        String telefono=textTelefono.getText().toString();
        int indiceSpinerSeleccionado= spFormapago.getSelectedItemPosition();
        String observaciones=textObservaciones.getText().toString();
        if(nombre.equals("")||direccion.equals("")||telefono.equals("")||barrio.equals(""))
        {
            mostrarMensaje(R.string.campos_obligatorios);
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
                if(indiceSpinerSeleccionado==1)
                {
                    formaPago="tc";
                }
                else
                {
                    formaPago="ef";
                }

                String google="www.google.com.co";
                String yahoo="www.yahoo.com";
                pd = ProgressDialog.show(this, getResources().getString(R.string.enviando_pedido), getResources().getString(R.string.por_favor_espere), true, false);
                EnviarPedidoTask env= new EnviarPedidoTask();
                env.execute(nombre,direccion,barrio,telefono,observaciones,formaPago);

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

    public class EnviarPedidoTask extends AsyncTask<String, Void, Boolean>
    {

        private String nombre;
        private String direccion;
        private String barrio;
        private String telefono;
        private String observaciones;
        private String formaPago;

        @Override
        protected Boolean doInBackground(String... params)
        {
            nombre=params[0];
            direccion=params[1];
            barrio=params[2];
            telefono=params[3];
            observaciones=params[4];
            formaPago=params[5];
            return hayConexionInternet();
        }

        @Override
        protected void onPostExecute(Boolean resultado)
        {
            super.onPostExecute(resultado);
            if(resultado)
            {
                enviarParse(nombre,direccion,barrio,telefono,observaciones,formaPago);
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
