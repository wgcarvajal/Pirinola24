package pirinola24.com.pirinola24;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProbandofacebookActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnFacebook;
    Button btnCerrarFacebook;
    Button btnCompartir;
    private Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_probandofacebook);

        btnFacebook=(Button)findViewById(R.id.btn_facebook);
        btnCerrarFacebook=(Button)findViewById(R.id.cerrar_session_facebook);
        btnCompartir=(Button)findViewById(R.id.btncompartir);
        btnFacebook.setOnClickListener(this);
        btnCerrarFacebook.setOnClickListener(this);
        btnCompartir.setOnClickListener(this);


    }



    @Override
    public void onClick(View v)
    {
        /*switch (v.getId())
        {
            case R.id.btn_facebook:
                progressDialog = ProgressDialog.show(this, "Logueando con Facebook", "Espere un momento", true);

                List<String> permissions = Arrays.asList("public_profile", "user_about_me",
                        "user_birthday", "user_location", "email");


                ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {

                    @Override
                    public void done(ParseUser user, ParseException err) {
                        progressDialog.dismiss();


                        if (user == null) {
                            Log.d("MyApp", "El usuario cancelo el Loggin");
                        } else if (user.isNew())
                        {

                            mostrarMensaje("nuevo usuario");
                        } else
                        {
                            mostrarMensaje("usuaro ya registrado");
                        }
                    }
                });
            break;
            case R.id.cerrar_session_facebook:
                ParseUser.logOut();
            break;

            case R.id.btncompartir:

                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://www.facebook.com/24DUFF/?fref=ts"))
                        .build();

                ShareDialog.show(this,content);

                break;
        }*/


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);



    }

    private void mostrarMensaje(String idmensaje)
    {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.template_mensaje_toast,
                (ViewGroup) findViewById(R.id.toast_layout));

        TextView text = (TextView) layout.findViewById(R.id.txt_mensaje_toast);
        text.setText(idmensaje);

        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
