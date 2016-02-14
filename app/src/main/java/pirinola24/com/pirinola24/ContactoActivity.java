package pirinola24.com.pirinola24;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnFlechaAtras;
    private EditText textEmail;
    private EditText textNombre;
    private EditText textMensaje;
    private TextView textOpinion;
    private Button btnEnviar;
    private String font_path_ASimple="font/A_Simple_Life.ttf";
    private Typeface TF;
    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        btnFlechaAtras=(ImageView)findViewById(R.id.flecha_atras);
        btnEnviar=(Button)findViewById(R.id.btn_enviar_pedido);

        textEmail=(EditText)findViewById(R.id.txt_email);
        textNombre=(EditText)findViewById(R.id.txt_nombre);
        textMensaje=(EditText)findViewById(R.id.txt_observaciones);
        textOpinion=(TextView)findViewById(R.id.txtOpinion);

        btnFlechaAtras.setOnClickListener(this);
        btnEnviar.setOnClickListener(this);

        TF = Typeface.createFromAsset(getAssets(), font_path_ASimple);
        textOpinion.setTypeface(TF);
        textMensaje.setTypeface(TF);
        textNombre.setTypeface(TF);
        textEmail.setTypeface(TF);

        btnEnviar.setTypeface(TF);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.flecha_atras:
                finish();
            break;
            case R.id.btn_enviar_pedido:
                enviar();
            break;
        }

    }

    private void enviar()
    {
        String email=textEmail.getText().toString();
        String nombre= textNombre.getText().toString();
        String mensaje=textMensaje.getText().toString();
        if(email.equals("")||nombre.equals("")||mensaje.equals(""))
        {
            mostrarMensaje(R.string.todos_campos_obligatorios);
        }
        else
        {
            if(!validarCorreo(email))
            {
                mostrarMensaje(R.string.campo_correo);
            }
            else
            {
                pd = ProgressDialog.show(this, getResources().getString(R.string.enviando_datos), getResources().getString(R.string.por_favor_espere), true, false);
                ParseObject contacto= new ParseObject("Contacto");
                contacto.put("email",email);
                contacto.put("nombre",nombre);
                contacto.put("mensaje",mensaje);
                contacto.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        pd.dismiss();
                        if(e==null)
                        {
                            mostrarMensaje(R.string.txt_mensaje_enviado);
                            textEmail.setText("");
                            textNombre.setText("");
                            textMensaje.setText("");
                        }
                        else
                        {
                            mostrarMensaje(R.string.compruebe_conexion);
                        }
                    }
                });

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

    private boolean validarCorreo(String email)
    {
        String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        // Match the given input against this pattern
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
