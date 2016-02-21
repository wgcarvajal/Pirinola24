package pirinola24.com.pirinola24;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import pirinola24.com.pirinola24.adaptadores.AdaptadorProductoPedido;
import pirinola24.com.pirinola24.basededatos.AdminSQliteOpenHelper;
import pirinola24.com.pirinola24.modelo.Producto;
import pirinola24.com.pirinola24.typeface.CustomTypefaceSpan;
import pirinola24.com.pirinola24.util.AppUtil;
import pirinola24.com.pirinola24.util.FontCache;

public class PedidoActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, AdaptadorProductoPedido.OnDisminuirTotal {

    public final static int MI_REQUEST_CODE = 1;
    public final static int MI_REQUEST_CODE_REGISTRADO = 2;
    public final static int MI_REQUEST_SE_LOGUIO_USUARIO=101;


    private String font_path = "font/2-4ef58.ttf";
    private String font_path_ASimple="font/A_Simple_Life.ttf";
    private String fontStackyard="font/Stackyard.ttf";
    private String fontURW="font/urwbookmanl.ttf";
    private TextView tituloMenuHeader;
    private TextView veinticuatro;
    private TextView textTotalPedido;
    private TextView textDomicilio;
    private  TextView textValorTotalPedido;
    private ImageView btnMenuPrincipal;
    private DrawerLayout drawer;
    private NavigationView navView;
    private Button btnFinalizarPedido;
    private GridView gridProductosPedido;
    private ImageView flechaAtras;
    private AdaptadorProductoPedido adapter;
    private List<Producto> data= new ArrayList<>();
    private ProgressDialog pd = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        tituloMenuHeader=(TextView)findViewById(R.id.titulo_header_menu);
        textTotalPedido=(TextView)findViewById(R.id.txt_total_pedido);
        textValorTotalPedido=(TextView)findViewById(R.id.txt_valor_total_pedido);
        textDomicilio=(TextView)findViewById(R.id.txt_domicilio);
        veinticuatro=(TextView)findViewById(R.id.veinticuatro);
        btnMenuPrincipal=(ImageView)findViewById(R.id.btn_menu_principal);
        btnFinalizarPedido=(Button)findViewById(R.id.btn_finalizar_pedido);
        flechaAtras=(ImageView)findViewById(R.id.flecha_atras);

        gridProductosPedido=(GridView)findViewById(R.id.grid_productos_pedido);

        drawer=(DrawerLayout)findViewById(R.id.drawer);
        navView = (NavigationView) findViewById(R.id.nav);

        btnMenuPrincipal.setOnClickListener(this);
        btnFinalizarPedido.setOnClickListener(this);
        navView.setNavigationItemSelectedListener(this);
        flechaAtras.setOnClickListener(this);
        Menu m = navView.getMenu();

        ParseUser currentUser = ParseUser.getCurrentUser();

        if(!hayProductos())
        {
            m.getItem(1).setVisible(false);
            btnFinalizarPedido.setVisibility(View.GONE);
            if(currentUser !=null)
            {
                flechaAtras.setVisibility(View.GONE);
            }
            else
            {
                btnMenuPrincipal.setVisibility(View.GONE);
                m.getItem(2).setVisible(false);
                MenuItem mI= m.getItem(2);
                Menu submenu= mI.getSubMenu();
                submenu.getItem(0).setVisible(false);
            }

        }
        else
        {
            flechaAtras.setVisibility(View.GONE);
            if(currentUser==null)
            {
                m.getItem(2).setVisible(false);
                MenuItem mI= m.getItem(2);
                Menu submenu= mI.getSubMenu();
                submenu.getItem(0).setVisible(false);
            }
        }

        Typeface TF = FontCache.get(font_path_ASimple, this);
        textDomicilio.setTypeface(TF);
        veinticuatro.setTypeface(TF);

        TF= FontCache.get(fontStackyard,this);
        textTotalPedido.setTypeface(TF);
        btnFinalizarPedido.setTypeface(TF);
        tituloMenuHeader.setTypeface(TF);
        aplicandoTipoLetraItemMenu(m, fontStackyard);
        TF=FontCache.get(fontURW,this);
        textValorTotalPedido.setTypeface(TF);
        adapter= new AdaptadorProductoPedido(this,data);
        gridProductosPedido.setAdapter(adapter);
        gridProductosPedido.setOnItemClickListener(this);
        loadData();
    }

    private void loadData()
    {
        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(this,"admin",null,1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor fila = db.rawQuery("select prodid, prodcantidad from pedido", null);
        if (fila != null) {

            if (fila.moveToFirst())
            {

                int contador=2000;
                do {

                    for(Producto p: AppUtil.data)
                    {
                        if(p.getId().equals(fila.getString(fila.getColumnIndex("prodid"))))
                        {
                            data.add(p);
                            contador=contador+(fila.getInt(fila.getColumnIndex("prodcantidad"))*p.getPrecio());
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }

                } while (fila.moveToNext());
                DecimalFormat format= new DecimalFormat("###,###.##");
                String valorTotal=format.format(contador);
                valorTotal=valorTotal.replace(",",".");
                textValorTotalPedido.setText("$"+valorTotal);
            }
        }
        db.close();
    }

    private void aplicandoTipoLetraItemMenu(Menu m,String tipoLetra)
    {

        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem,tipoLetra);
                }
            }
            applyFontToMenuItem(mi,tipoLetra);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_menu_principal:
                drawer.openDrawer(GravityCompat.START);
            break;

            case R.id.btn_finalizar_pedido:
                finalizarPedido();
            break;

            case R.id.flecha_atras:
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            break;
        }
    }

    private void finalizarPedido()
    {
        String substring=textValorTotalPedido.getText().toString().substring(1);
        substring =substring.replace(".","");
        int total= Integer.parseInt(substring);
        if(total<10000)
        {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.template_mensaje_toast,
                    (ViewGroup) findViewById(R.id.toast_layout));

            TextView text = (TextView) layout.findViewById(R.id.txt_mensaje_toast);
            text.setText(getResources().getString(R.string.mensaje_pedido_minimo));

            Toast toast = new Toast(this);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
        else
        {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null)
            {
                Intent intent = new Intent(this,RegistradoActivity.class);
                startActivityForResult(intent, MI_REQUEST_CODE);
            }
            else
            {
                Intent intent = new Intent(this,LoginActivity.class);
                startActivityForResult(intent,MI_REQUEST_CODE);
            }

        }

    }

    private void applyFontToMenuItem(MenuItem mi,String rutaTipoLetra)
    {
        Typeface font = FontCache.get(rutaTipoLetra,this);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.nav_la_carta:

                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            break;
            case R.id.nav_vaciar_pedido:
                vaciarPedido();
            break;
            case R.id.nav_cerrar_sesion:
                cerrarSesion();
            break;

        }
        drawer.closeDrawers();
        return false;

    }

    private void cerrarSesion()
    {
        pd = ProgressDialog.show(this, "", getResources().getString(R.string.por_favor_espere), true, false);

        CerrarSesionTask cst= new CerrarSesionTask();
        cst.execute();

    }

    public class CerrarSesionTask extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            ParseUser.logOut();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            Menu m=navView.getMenu();
            m.getItem(2).setVisible(false);
            Menu men=m.getItem(2).getSubMenu();
            men.getItem(0).setVisible(false);
            mostrarMensaje(R.string.txt_sesion_cerrada);
            if(!m.getItem(1).isVisible())
            {
                btnMenuPrincipal.setVisibility(View.GONE);
                flechaAtras.setVisibility(View.VISIBLE);
            }

            if(pd!=null)
            {
                pd.dismiss();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        MediaPlayer m = MediaPlayer.create(getApplicationContext(), R.raw.sonido_click);
        m.start();
        String valorTotalpedido=textValorTotalPedido.getText().toString();
        String substring = valorTotalpedido.substring(1);
        substring =substring.replace(".","");
        int contador= Integer.parseInt(substring);
        contador= contador+ data.get(position).getPrecio();

        DecimalFormat format= new DecimalFormat("###,###.##");
        String valorTotal=format.format(contador);
        valorTotal=valorTotal.replace(",",".");
        textValorTotalPedido.setText("$"+valorTotal);

        TextView textconteo= (TextView) view.findViewById(R.id.txtconteo);
        int conteo = Integer.parseInt(textconteo.getText().toString());
        conteo= conteo+1;
        textconteo.setText(conteo+"");

        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(this,"admin",null,1);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues registroPedido= new ContentValues();
        registroPedido.put("prodcantidad",conteo);

        int cant= db.update("pedido",registroPedido,"prodid = '"+data.get(position).getId()+"'",null);

        db.close();

    }

    @Override
    public void onDisminuirTotal(int precio)
    {
        String valorTotalpedido=textValorTotalPedido.getText().toString();
        String substring = valorTotalpedido.substring(1);
        substring =substring.replace(".","");
        int contador= Integer.parseInt(substring)-precio;
        if(contador==2000)
        {
            contador=0;
            Menu m = navView.getMenu();
            m.getItem(1).setVisible(false);
            btnFinalizarPedido.setVisibility(View.GONE);
            btnMenuPrincipal.setVisibility(View.GONE);
            flechaAtras.setVisibility(View.VISIBLE);

        }
        DecimalFormat format= new DecimalFormat("###,###.##");
        String valorTotal=format.format(contador);
        valorTotal=valorTotal.replace(",",".");
        textValorTotalPedido.setText("$" + valorTotal);
    }

    private void vaciarPedido()
    {
        final Dialog dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.template_dialog_vaciar_pedido);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bordes_redondos);


        Button btnAceptar=(Button)dialog.findViewById(R.id.btn_aceptar);
        Button btnCancelar=(Button)dialog.findViewById(R.id.btn_cancelar);
        TextView mensaje =(TextView)dialog.findViewById(R.id.txtmensaje);

        final Context context=this;
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(context,"admin",null,1);
                SQLiteDatabase db = admin.getWritableDatabase();

                db.delete("pedido", "", null);

                textValorTotalPedido.setText("$0");

                data.removeAll(data);
                adapter.notifyDataSetChanged();
                db.close();
                dialog.dismiss();

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.template_mensaje_toast,
                        (ViewGroup) findViewById(R.id.toast_layout));

                TextView text = (TextView) layout.findViewById(R.id.txt_mensaje_toast);
                text.setText(context.getResources().getString(R.string.mensaje_pedido_vacio));

                Toast toast = new Toast(context);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
                Menu m = navView.getMenu();
                mostrandoMenu(m);
                m.getItem(1).setVisible(false);
                btnFinalizarPedido.setVisibility(View.GONE);
                ParseUser currentUser = ParseUser.getCurrentUser();
                if(currentUser==null)
                {
                    flechaAtras.setVisibility(View.VISIBLE);
                    btnMenuPrincipal.setVisibility(View.GONE);
                    m.getItem(2).setVisible(false);
                    MenuItem mI=m.getItem(2);
                    Menu submenu=mI.getSubMenu();
                    submenu.getItem(0).setVisible(false);
                }
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

    private boolean hayProductos()
    {
        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(this,"admin",null,1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor fila = db.rawQuery("select prodid from pedido ", null);
        if(fila.moveToFirst())
        {
            db.close();
            return true;
        }
        db.close();
        return false;
    }

    private void mostrandoMenu(Menu m)
    {
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    subMenuItem.setVisible(true);
                }
            }
            mi.setVisible(true);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("requestcode:", " " + requestCode);
        if (requestCode == MI_REQUEST_CODE) {

            if (resultCode == RESULT_OK)
            {
                vaciarPedidoDespuesDeEnviar();
                mostrarDialogConfirmarPedido();
            }
            else
            {
                if(resultCode==MI_REQUEST_SE_LOGUIO_USUARIO)
                {
                    Menu m = navView.getMenu();
                    mostrandoMenu(m);
                    Intent intent = new Intent(this,RegistradoActivity.class);
                    startActivityForResult(intent, MI_REQUEST_CODE_REGISTRADO);
                }
            }
        }
        if(requestCode==MI_REQUEST_CODE_REGISTRADO)
        {
            if(resultCode==RESULT_OK)
            {
                vaciarPedidoDespuesDeEnviar();
                mostrarDialogConfirmarPedido();
            }
        }
    }


    private void vaciarPedidoDespuesDeEnviar()
    {
        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(this,"admin",null,1);
        SQLiteDatabase db = admin.getWritableDatabase();

        db.delete("pedido", "", null);

        textValorTotalPedido.setText("$0");

        data.removeAll(data);
        adapter.notifyDataSetChanged();
        db.close();

        Menu m = navView.getMenu();
        m.getItem(1).setVisible(false);
        btnFinalizarPedido.setVisibility(View.GONE);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser==null)
        {
            flechaAtras.setVisibility(View.VISIBLE);
            btnMenuPrincipal.setVisibility(View.GONE);
            m.getItem(2).setVisible(false);
            MenuItem mI=m.getItem(2);
            Menu submenu=mI.getSubMenu();
            submenu.getItem(0).setVisible(false);
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
        Typeface TF = FontCache.get(font_path_ASimple,this);

        mensaje.setTypeface(TF);
        btnAceptar.setTypeface(TF);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }
}
