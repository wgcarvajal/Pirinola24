package pirinola24.com.pirinola24;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pirinola24.com.pirinola24.modelo.Usuario;
import pirinola24.com.pirinola24.util.FontCache;

public class RegistrarseActivity extends AppCompatActivity implements View.OnClickListener {
    private String font_path_ASimple="font/A_Simple_Life.ttf";

    private TextView txtnombre;
    private TextView txtemail;
    private TextView txtpassword;
    private TextView txtrepetirpassword;
    private TextView txttelefono;
    private ImageView btnRegistrarse;
    private ImageView btnAtras;
    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        txtnombre=(TextView)findViewById(R.id.txt_nombre);
        txtemail=(TextView)findViewById(R.id.txt_email);
        txtpassword=(TextView)findViewById(R.id.txt_password);
        txtrepetirpassword=(TextView)findViewById(R.id.txt_repetirpassword);
        txttelefono=(TextView)findViewById(R.id.txt_telefono);
        btnRegistrarse=(ImageView)findViewById(R.id.btnRegistrarse);
        btnAtras=(ImageView)findViewById(R.id.flecha_atras);

        Typeface TF = FontCache.get(font_path_ASimple,this);

        txtnombre.setTypeface(TF);
        txtemail.setTypeface(TF);
        txtpassword.setTypeface(TF);
        txtrepetirpassword.setTypeface(TF);
        txttelefono.setTypeface(TF);

        btnAtras.setOnClickListener(this);
        btnRegistrarse.setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.flecha_atras:
                finish();
            break;

            case R.id.btnRegistrarse:
                registrarse();
            break;
        }

    }

    private void registrarse()
    {
        String nombre=txtnombre.getText().toString();
        String email=txtemail.getText().toString();
        String password=txtpassword.getText().toString();
        String repetirPassword=txtrepetirpassword.getText().toString();
        String telefono=txttelefono.getText().toString();

        if(comprobarCampos(nombre,email,password,repetirPassword,telefono))
        {
            pd = ProgressDialog.show(this, getResources().getString(R.string.enviando_datos), getResources().getString(R.string.por_favor_espere), true, false);
            RegistroTask reg= new RegistroTask();
            reg.execute(nombre,email,password,telefono);
        }
    }

    private boolean comprobarCampos(String nombre,String email,String password,String repetirPassword,String telefono)
    {


        if(nombre.equals("") || email.equals("") || password.equals("") || repetirPassword.equals("") || telefono.equals(""))
        {
            mostrarMensaje(R.string.todos_campos_obligatorios);
            return false;
        }
        else
        {
            if(nombre.length()<3)
            {
                mostrarMensaje(R.string.campo_nombre);
                return false;
            }
            else
            {
                if(!validarCorreo(email))
                {
                    mostrarMensaje(R.string.campo_correo);
                    return false;
                }
                else
                {
                    if(password.length()<4 || password.length()>20)
                    {
                        mostrarMensaje(R.string.campo_password);
                        return false;
                    }
                    else
                    {
                        if(!password.equals(repetirPassword))
                        {
                            mostrarMensaje(R.string.campo_repetir_password);
                            return false;
                        }
                        else
                        {
                            if(telefono.length()<6)
                            {
                                mostrarMensaje(R.string.campo_telefono);
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean validarCorreo(String email)
    {
        String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        // Match the given input against this pattern
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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

    class RegistroTask extends AsyncTask<String, Void, Boolean>
    {
        String nombre;
        String email;
        String password;
        String telefono;

        @Override
        protected Boolean doInBackground(String... params)
        {
            nombre=params[0];
            email=params[1];
            password=params[2];
            telefono=params[3];
            return hayConexionInternet();
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado)
            {
                enviarDatosParse(nombre,email,password,telefono);
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

    private void enviarDatosParse(final String nombre, final String email, final String password, final String telefono)
    {
        ParseQuery<ParseObject> queryUser = new ParseQuery<>(Usuario.TABLA);
        queryUser.whereEqualTo(Usuario.EMAIL,email);
        queryUser.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e)
            {
                if(e==null)
                {
                    pd.dismiss();
                    mostrarMensaje(R.string.correo_ya_esta);
                }
                else
                {
                    if(e.getCode()==101)
                    {
                        final ParseUser user = new ParseUser();
                        user.setUsername(email);
                        user.setPassword(password);
                        user.setEmail(email);
                        user.put(Usuario.NOMBRE, nombre);
                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e)
                            {
                                if(e==null)
                                {
                                    ParseObject telefonoParse = new ParseObject(Usuario.TABLATELEFONO);
                                    telefonoParse.put(Usuario.TBLTELEFONONUMERO, telefono);
                                    telefonoParse.put(Usuario.TBLTELEFONOUSER, user.getObjectId());
                                    telefonoParse.saveInBackground();

                                    pd.dismiss();
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                }
                                else
                                {
                                    pd.dismiss();
                                    mostrarMensaje(R.string.compruebe_conexion);
                                }

                            }
                        });

                    }
                    else
                    {
                        pd.dismiss();
                        mostrarMensaje(R.string.compruebe_conexion);
                    }
                }
            }
        });
    }
}
