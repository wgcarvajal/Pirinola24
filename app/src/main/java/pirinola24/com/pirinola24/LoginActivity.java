package pirinola24.com.pirinola24;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pirinola24.com.pirinola24.modelo.Ciudad;
import pirinola24.com.pirinola24.util.FontCache;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    public final static int MI_REQUEST_CODE_REGISTRARSE = 1;
    public final static int MI_REQUEST_CODE_CONT_NO_REGISTRADO = 2;
    public final static int MI_REQUEST_SE_LOGUIO_USUARIO=101;

    private String font_path="font/A_Simple_Life.ttf";
    private String fontStackyard="font/Stackyard.ttf";
    private ImageView btnAtras;
    private TextView btnIniciarSesion;
    private TextView btnRegistrarse;
    private TextView btnContNoRegistrado;
    private TextView txtrecuperarClave;
    private TextView txtemail;
    private TextView txtpassword;
    private TextView tituloVista;
    private ProgressDialog pd = null;
    private TextView btnFacebook;
    private Dialog dialog;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtrecuperarClave=(TextView)findViewById(R.id.recuperarClave);
        txtemail=(TextView)findViewById(R.id.txt_email);
        txtpassword=(TextView)findViewById(R.id.txt_password);
        tituloVista=(TextView)findViewById(R.id.ingrese);

        btnAtras=(ImageView)findViewById(R.id.flecha_atras);
        btnRegistrarse=(TextView)findViewById(R.id.btnRegistrarse);
        btnIniciarSesion=(TextView)findViewById(R.id.btninciarsesion);
        btnContNoRegistrado=(TextView)findViewById(R.id.btn_continuar_no_registrado);
        btnFacebook=(TextView)findViewById(R.id.btn_facebook);
        btnAtras.setOnClickListener(this);
        btnRegistrarse.setOnClickListener(this);
        btnIniciarSesion.setOnClickListener(this);
        btnContNoRegistrado.setOnClickListener(this);
        txtrecuperarClave.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);

        Typeface TF = FontCache.get(font_path,this);
        txtpassword.setTypeface(TF);
        txtemail.setTypeface(TF);

        TF = FontCache.get(fontStackyard,this);
        txtrecuperarClave.setTypeface(TF);
        tituloVista.setTypeface(TF);
        btnIniciarSesion.setTypeface(TF);
        btnRegistrarse.setTypeface(TF);
        btnFacebook.setTypeface(TF);
        btnContNoRegistrado.setTypeface(TF);

        callbackManager = CallbackManager.Factory.create();
    }


    @Override
    public void onClick(View v)
    {
        Intent intent;
        switch (v.getId())
        {
            case R.id.flecha_atras:
                finish();
            break;

            case R.id.btnRegistrarse:
                intent= new Intent(this,RegistrarseActivity.class);
                startActivityForResult(intent,MI_REQUEST_CODE_REGISTRARSE);
            break;
            case R.id.btn_continuar_no_registrado:

                intent= new Intent(this,NoregistradoActivity.class);
                startActivityForResult(intent,MI_REQUEST_CODE_CONT_NO_REGISTRADO);

            break;

            case R.id.recuperarClave:
                recuperarClave();
            break;

            case R.id.btninciarsesion:
                iniciarSession();
            break;

            case R.id.btn_facebook:
                iniciarSessionFacebook();
            break;
        }

    }

    private void recuperarClave()
    {
        dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.template_dialog_olvidaste_password);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.borde_template_descripcion_producto);


        Typeface TF = FontCache.get(font_path,this);
        TextView btnEnviar=(TextView)dialog.findViewById(R.id.btn_enviar);
        TextView btnCancelar=(TextView)dialog.findViewById(R.id.btn_cancelar);
        final EditText txtemail =(EditText)dialog.findViewById(R.id.txt_email);

        txtemail.setTypeface(TF);
        TF=FontCache.get(fontStackyard,this);
        btnCancelar.setTypeface(TF);
        btnEnviar.setTypeface(TF);
        final Context context=this;

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email=txtemail.getText().toString();

                enviarEmail(email);
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

    private void enviarEmail(String email)
    {
        if(email.equals(""))
        {
            mostrarMensaje(R.string.txt_ingrese_correo);
        }
        else
        {
            if(!validarCorreo(email))
            {
                mostrarMensaje(R.string.campo_correo);
            }
            else
            {
                pd=ProgressDialog.show(this,getResources().getString(R.string.txt_enviando), getResources().getString(R.string.por_favor_espere));
                EnviarEmailTaks evet= new EnviarEmailTaks();
                evet.execute(email);
            }

        }
    }

    public class EnviarEmailTaks extends  AsyncTask <String, Void, Boolean>
    {
        private String email;

        @Override
        protected Boolean doInBackground(String... params) {

            email=params[0];
            return hayConexionInternet();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean)
            {
                enviarEmailBackendless(email);
            }
            else
            {
                pd.dismiss();
                mostrarMensaje(R.string.compruebe_conexion);
            }
        }
    }

    private void enviarEmailBackendless(String email)
    {

        Backendless.UserService.restorePassword(email, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response)
            {
                pd.dismiss();
                dialog.dismiss();
                mostrarMensaje(R.string.confirmacion_envio_email);
            }

            @Override
            public void handleFault(BackendlessFault fault)
            {
                pd.dismiss();
                dialog.dismiss();
                if(fault.getCode()!=null && fault.getCode().equals("3075"))
                {
                    mostrarMensaje(R.string.txt_mensaje_usuario_no_puede_ser_logueado);
                }
                else
                {
                    if(fault.getCode()!=null && fault.getCode().equals("3020"))
                    {
                        mostrarMensaje(R.string.correo_no_se_encuentra);
                    }
                    else
                    {
                        mostrarMensaje(R.string.compruebe_conexion);
                    }
                }
            }
        });
    }

    private void iniciarSession()
    {
        String email=txtemail.getText().toString();
        String password=txtpassword.getText().toString();
        if(verificarCampos(email,password))
        {
            IniciarSesionTask isTask=new IniciarSesionTask();
            isTask.execute(email,password);

        }
    }

    private void iniciarSessionFacebook()
    {
        Map<String, String> facebookFieldMappings = new HashMap<String, String>();
        facebookFieldMappings.put("email", "emailFacebook");
        facebookFieldMappings.put("name", "name");

        List<String> permissions = new ArrayList<String>();
        permissions.add("public_profile");
        permissions.add("email");
        pd=ProgressDialog.show(this,getResources().getString(R.string.txt_iniciando_con_facebook), getResources().getString(R.string.por_favor_espere), true);
        Backendless.UserService.loginWithFacebookSdk(this, facebookFieldMappings, permissions, callbackManager, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                pd.dismiss();
                LoginActivity.this.setResult(MI_REQUEST_SE_LOGUIO_USUARIO);
                LoginActivity.this.finish();
            }

            @Override
            public void handleFault(BackendlessFault fault)
            {
                pd.dismiss();
                LoginManager.getInstance().logOut();
                if(fault.getCode()!=null)
                {
                    if (fault.getCode().equals("3033"))                {
                        mostrarMensaje(R.string.correo_ya_esta);

                    } else
                    {
                        mostrarMensaje(R.string.compruebe_conexion);
                    }
                }
                else
                {
                    if(!fault.getMessage().equals("Facebook login canceled"))
                    {
                        mostrarMensaje(R.string.compruebe_conexion);
                    }

                }

            }
        }, true);
    }

    private void enviarBackendless(String email, String password)
    {
        pd = ProgressDialog.show(this, getResources().getString(R.string.txt_iniciando), getResources().getString(R.string.por_favor_espere), true, false);


        Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response)
            {
                pd.dismiss();
                setResult(MI_REQUEST_SE_LOGUIO_USUARIO);
                finish();
            }

            @Override
            public void handleFault(BackendlessFault fault)
            {
                pd.dismiss();
                if(fault.getCode().equals("3003"))
                {
                    mostrarMensaje(R.string.txt_email_clave_incorrectos);
                }
                else
                {
                    if(fault.getCode().equals("3000"))
                    {
                        mostrarMensaje(R.string.txt_mensaje_usuario_no_puede_ser_logueado);
                    }
                    else
                    {
                        mostrarMensaje(R.string.compruebe_conexion);
                    }

                }
            }
        },true);
    }




    public class IniciarSesionTask extends AsyncTask<String,Void,Boolean>
    {
        String email;
        String password;
        @Override
        protected Boolean doInBackground(String... params)
        {
            email=params[0];
            password=params[1];
            return hayConexionInternet();
        }
        @Override
        protected void onPostExecute(Boolean resultado)
        {
            super.onPostExecute(resultado);
            if(resultado)
            {
                enviarBackendless(email,password);
            }
            else
            {
                mostrarMensaje(R.string.compruebe_conexion);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        //ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case MI_REQUEST_CODE_REGISTRARSE:
                if (resultCode == RESULT_OK)
                {
                    mostrarDialogConfirmarPedido();
                }
            break;

            case MI_REQUEST_CODE_CONT_NO_REGISTRADO:
                if (resultCode == RESULT_OK)
                {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            break;
        }
    }

    private void mostrarDialogConfirmarPedido()
    {
        final Dialog dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.template_dialog_confirmacion_envio);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bordes_redondos);

        Button btnAceptar=(Button)dialog.findViewById(R.id.btn_aceptar);
        TextView mensaje =(TextView) dialog.findViewById(R.id.txtmensaje);
        Typeface TF = FontCache.get(font_path,this);
        mensaje.setText(getResources().getString(R.string.confirmar_registro));
        mensaje.setTypeface(TF);
        btnAceptar.setTypeface(TF);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private boolean verificarCampos(String email,String password)
    {

        if(email.equals("") || password.equals(""))
        {
            mostrarMensaje(R.string.campos_obligatorios_inicio_session);
            return false;
        }
        else
        {
            if(email.equals(""))
            {
                mostrarMensaje(R.string.campo_obligatorio_correo_inicio_session);
                return false;
            }
            else
            {
                if(password.equals(""))
                {
                    mostrarMensaje(R.string.campo_obligatorio_password_inicio_session);
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
                    }
                }
            }
        }
        return true;
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

    private boolean validarCorreo(String email)
    {
        String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        // Match the given input against this pattern
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
