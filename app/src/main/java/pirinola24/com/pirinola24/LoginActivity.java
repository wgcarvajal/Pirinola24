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
import android.util.Log;
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

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pirinola24.com.pirinola24.modelo.Usuario;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    public final static int MI_REQUEST_CODE_REGISTRARSE = 1;
    public final static int MI_REQUEST_CODE_CONT_NO_REGISTRADO = 2;
    public final static int MI_REQUEST_SE_LOGUIO_USUARIO=101;

    private String font_path="font/A_Simple_Life.ttf";
    private String fontStackyard="font/Stackyard.ttf";
    private Typeface TF;
    private ImageView btnAtras;
    private ImageView btnIniciarSesion;
    private ImageView btnRegistrarse;
    private ImageView btnContNoRegistrado;
    private TextView txtrecuperarClave;
    private TextView txtemail;
    private TextView txtpassword;
    private TextView tituloVista;
    private ProgressDialog pd = null;
    private ImageView btnFacebook;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtrecuperarClave=(TextView)findViewById(R.id.recuperarClave);
        txtemail=(TextView)findViewById(R.id.txt_email);
        txtpassword=(TextView)findViewById(R.id.txt_password);

        btnAtras=(ImageView)findViewById(R.id.flecha_atras);
        btnRegistrarse=(ImageView)findViewById(R.id.btnRegistrarse);
        btnIniciarSesion=(ImageView)findViewById(R.id.btninciarsesion);
        btnContNoRegistrado=(ImageView)findViewById(R.id.btn_continuar_no_registrado);
        btnFacebook=(ImageView)findViewById(R.id.btn_facebook);
        btnAtras.setOnClickListener(this);
        btnRegistrarse.setOnClickListener(this);
        btnIniciarSesion.setOnClickListener(this);
        btnContNoRegistrado.setOnClickListener(this);
        txtrecuperarClave.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);

        TF = Typeface.createFromAsset(getAssets(), font_path);
        txtpassword.setTypeface(TF);
        txtemail.setTypeface(TF);

        TF = Typeface.createFromAsset(getAssets(), fontStackyard);
        txtrecuperarClave.setTypeface(TF);
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
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bordes_redondeados_pequenos);


        TF = Typeface.createFromAsset(getAssets(), font_path);
        Button btnEnviar=(Button)dialog.findViewById(R.id.btn_enviar);
        Button btnCancelar=(Button)dialog.findViewById(R.id.btn_cancelar);
        final EditText txtemail =(EditText)dialog.findViewById(R.id.txt_email);

        txtemail.setTypeface(TF);

        btnEnviar.setTypeface(TF);
        btnCancelar.setTypeface(TF);
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
                enviarEmailParse(email);
            }
            else
            {
                pd.dismiss();
                mostrarMensaje(R.string.compruebe_conexion);
            }
        }
    }

    private void enviarEmailParse(String email)
    {
        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                pd.dismiss();
                if (e == null) {
                    dialog.dismiss();
                    mostrarMensaje(R.string.confirmacion_envio_email);
                } else {

                    if(e.getCode()==205)
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
        pd=ProgressDialog.show(this,getResources().getString(R.string.txt_iniciando_con_facebook), getResources().getString(R.string.por_favor_espere), true);
        List<String> permissions = Arrays.asList("public_profile", "user_about_me",
                "user_birthday", "user_location", "email");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback()
        {
            @Override
            public void done(ParseUser user, ParseException err)
            {
                pd.dismiss();
                if (user == null)
                {
                    if(err!=null)
                    {
                        mostrarMensaje(R.string.compruebe_conexion);
                    }
                }
                else
                {
                    if (user.isNew())
                    {
                        makeMeRequest();
                    }
                    else
                    {
                        if(user.getString(Usuario.NOMBRE)==null)
                        {
                            makeMeRequest();
                        }
                    }
                    LoginActivity.this.setResult(MI_REQUEST_SE_LOGUIO_USUARIO);
                    LoginActivity.this.finish();
                }

            }
        });
    }

    private void enviarParse(String email, String password)
    {
        pd = ProgressDialog.show(this, getResources().getString(R.string.txt_iniciando), getResources().getString(R.string.por_favor_espere), true, false);
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    pd.dismiss();
                    setResult(MI_REQUEST_SE_LOGUIO_USUARIO);
                    finish();
                } else {
                    pd.dismiss();
                    if (e.getCode() == 101) {
                        mostrarMensaje(R.string.txt_email_clave_incorrectos);
                    } else {
                        mostrarMensaje(R.string.compruebe_conexion);
                    }
                }
            }
        });
    }

    private void makeMeRequest() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {
                            JSONObject userProfile = new JSONObject();

                            try {

                                ParseUser currentUser = ParseUser.getCurrentUser();
                                userProfile.put("facebookId", jsonObject.getLong("id"));
                                userProfile.put("name", jsonObject.getString("name"));
                                currentUser.put(Usuario.NOMBRE,jsonObject.getString("name"));

                                if (jsonObject.getString("gender") != null)
                                    userProfile.put("gender", jsonObject.getString("gender"));

                                if (jsonObject.getString("email") != null)
                                    userProfile.put("email", jsonObject.getString("email"));
                                currentUser.put(Usuario.EMAILFACEBOOK, jsonObject.getString("email"));

                                currentUser.put("profile", userProfile);
                                currentUser.saveInBackground();

                            } catch (JSONException e) {
                                Log.d("Myapp",
                                        "Error parsing returned user data. " + e);
                            }
                        } else if (graphResponse.getError() != null) {

                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,gender,name");
        request.setParameters(parameters);
        request.executeAsync();
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
                enviarParse(email,password);
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
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
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
        TF = Typeface.createFromAsset(getAssets(), font_path);
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
