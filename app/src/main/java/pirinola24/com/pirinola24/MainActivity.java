package pirinola24.com.pirinola24;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.viewpagerindicator.PageIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pirinola24.com.pirinola24.adaptadores.PagerAdapter;
import pirinola24.com.pirinola24.fragments.FragmentGeneric;
import pirinola24.com.pirinola24.fragments.ProductoFragment;
import pirinola24.com.pirinola24.fragments.ProductoGridFragment;
import pirinola24.com.pirinola24.modelo.Producto;
import pirinola24.com.pirinola24.modelo.Subcategoria;
import pirinola24.com.pirinola24.typeface.CustomTypefaceSpan;
import pirinola24.com.pirinola24.util.AppUtil;
import pirinola24.com.pirinola24.util.FontCache;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, ProductoGridFragment.OnComunicationFragmentGrid, ProductoFragment.OnComunicationFragment {

    public final static int MI_REQUEST_CODE = 1;


    private ViewPager pager;
    private PageIndicator pagerIndicator;
    private PagerAdapter adapter;
    private List<FragmentGeneric> data= new ArrayList<>();
    private NavigationView navView;
    private DrawerLayout drawer;
    private TextView tituloMenuHeader;
    private TextView veinticuatro;
    private TextView text_compruebe_conexion;
    private Button btnRecargarVista;
    private String font_path = "font/2-4ef58.ttf";
    private String font_path_ASimple="font/A_Simple_Life.ttf";
    private String fontStackyard="font/Stackyard.ttf";
    private ProgressDialog pd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tituloMenuHeader=(TextView)findViewById(R.id.titulo_header_menu);
        veinticuatro=(TextView)findViewById(R.id.veinticuatro);
        text_compruebe_conexion=(TextView)findViewById(R.id.txt_sin_conexion);


        btnRecargarVista=(Button)findViewById(R.id.volver_cargar);

        drawer=(DrawerLayout)findViewById(R.id.drawer);
        pager= (ViewPager)findViewById(R.id.pager);
        pagerIndicator=(PageIndicator)findViewById(R.id.pagerIndicator);
        navView = (NavigationView) findViewById(R.id.nav);



        navView.setNavigationItemSelectedListener(this);
        btnRecargarVista.setOnClickListener(this);


        text_compruebe_conexion.setVisibility(View.GONE);
        btnRecargarVista.setVisibility(View.GONE);

        Typeface TF = FontCache.get(fontStackyard,this); //Typeface.createFromAsset(getAssets(), fontStackyard);

        tituloMenuHeader.setTypeface(TF);
        TF = FontCache.get(font_path_ASimple,this); //Typeface.createFromAsset(getAssets(), font_path_ASimple);
        veinticuatro.setTypeface(TF);
        text_compruebe_conexion.setTypeface(TF);
        btnRecargarVista.setTypeface(TF);

        adapter = new PagerAdapter(getSupportFragmentManager(), data);
        pager.setAdapter(adapter);
        //pager.setPageTransformer(true, new CubeOutTransformer());
        pagerIndicator.setViewPager(pager);
        Log.i("cuantas paginas",pager.getOffscreenPageLimit()+"");
        Menu m = navView.getMenu();
        aplicandoTipoLetraItemMenu(m, fontStackyard);
        ocultandoMenu(m);
        loadData();


    }
    private void ocultandoMenu(Menu m)
    {
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    subMenuItem.setVisible(false);
                }
            }
            mi.setVisible(false);

        }
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

    private void aplicandoTipoLetraItemMenu(Menu m,String tipoLetra)
    {

        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem, tipoLetra);
                }
            }
            applyFontToMenuItem(mi, tipoLetra);

        }
    }
    private void loadData()
    {
        if(AppUtil.data.size()>0)
        {
            for(Subcategoria sub: AppUtil.listaSubcategorias)
            {
                switch (sub.getTipoFragment())
                {
                    case Subcategoria.CONDESCRIPCION:
                        ProductoFragment productoFragment = new ProductoFragment();
                        productoFragment.init(sub.getNombre(),sub.getUrlImagenTitulo());
                        data.add(productoFragment);
                    break;
                    case Subcategoria.SINDESCRIPCION:
                        ProductoGridFragment productoGridFragment = new ProductoGridFragment();
                        productoGridFragment.init(sub.getNombre(),sub.getUrlImagenTitulo());
                        data.add(productoGridFragment);
                    break;
                }

            }
            adapter.notifyDataSetChanged();
            Menu m=navView.getMenu();
            mostrandoMenu(m);

            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser == null)
            {
                m.getItem(2).setVisible(false);
                Menu men=m.getItem(2).getSubMenu();
                men.getItem(0).setVisible(false);
            }
        }
        else
        {
            CargarDatosRemotosTask cargarDatosRemotosTask=new CargarDatosRemotosTask();
            cargarDatosRemotosTask.execute();
        }
    }

    public void cargarDatosParse()
    {
        ParseQuery<ParseObject> querySubcategoria= new ParseQuery<>(Subcategoria.TABLA);
        querySubcategoria.orderByAscending(Subcategoria.POSICION);
        querySubcategoria.selectKeys(Arrays.asList(Subcategoria.ID,Subcategoria.NOMBRE,Subcategoria.IMGTITULO,Subcategoria.TIPOFRAGMENT));
        querySubcategoria.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> subcategorias, ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> queryProductos = new ParseQuery<>(Producto.TABLA);
                    queryProductos.selectKeys(Arrays.asList(Producto.ID, Producto.NOMBRE,Producto.DESCRIPCION,Producto.SUBCATEGORIA, Producto.PRECIO,Producto.IMAGEN));
                    queryProductos.setLimit(200);
                    queryProductos.orderByAscending(Producto.POSICION);
                    queryProductos.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> productos, ParseException e) {
                            if (e == null) {

                                for (ParseObject prod : productos) {
                                    Producto producto = new Producto();
                                    producto.setId(prod.getObjectId());
                                    producto.setNombre(prod.getString(Producto.NOMBRE));
                                    producto.setDescripcion(prod.getString(Producto.DESCRIPCION));
                                    producto.setPrecio(prod.getInt(Producto.PRECIO));
                                    producto.setUrlImagen(prod.getParseFile(Producto.IMAGEN).getUrl());

                                    for (ParseObject sub : subcategorias) {
                                        if (sub.getObjectId().equals(prod.getString(Producto.SUBCATEGORIA))) {
                                            producto.setSubcategoria(sub.getString(Subcategoria.NOMBRE));
                                            AppUtil.data.add(producto);
                                            break;
                                        }
                                    }

                                }
                                for (ParseObject sub : subcategorias) {

                                    Subcategoria subcategoria = new Subcategoria();
                                    subcategoria.setNombre(sub.getString(Subcategoria.NOMBRE));
                                    subcategoria.setTipoFragment(sub.getInt(Subcategoria.TIPOFRAGMENT));
                                    subcategoria.setUrlImagenTitulo(sub.getParseFile(Subcategoria.IMGTITULO).getUrl());
                                    AppUtil.listaSubcategorias.add(subcategoria);
                                }
                                for (Subcategoria sub : AppUtil.listaSubcategorias) {

                                    switch (sub.getTipoFragment())
                                    {
                                        case Subcategoria.CONDESCRIPCION:
                                            ProductoFragment productoFragment = new ProductoFragment();
                                            productoFragment.init(sub.getNombre(),sub.getUrlImagenTitulo());
                                            data.add(productoFragment);
                                        break;
                                        case Subcategoria.SINDESCRIPCION:
                                            ProductoGridFragment productoGridFragment = new ProductoGridFragment();
                                            productoGridFragment.init(sub.getNombre(),sub.getUrlImagenTitulo());
                                            data.add(productoGridFragment);
                                        break;
                                    }

                                }
                                adapter.notifyDataSetChanged();
                                Menu m = navView.getMenu();
                                mostrandoMenu(m);
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                if (currentUser == null) {
                                    m.getItem(2).setVisible(false);
                                    Menu men = m.getItem(2).getSubMenu();
                                    men.getItem(0).setVisible(false);
                                }
                            } else {
                                mostrarMensajeComprobarConexion();
                            }

                        }
                    });
                } else {
                    mostrarMensajeComprobarConexion();
                }
            }});
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {

            case R.id.btn_menu_principal:
                    drawer.openDrawer(GravityCompat.START);
            break;


            case R.id.volver_cargar:

                volverAcargar();

            break;

        }
    }

    private void volverAcargar()
    {
        text_compruebe_conexion.setVisibility(View.GONE);
        btnRecargarVista.setVisibility(View.GONE);
        CargarDatosRemotosTask cargarDatosRemotosTask=new CargarDatosRemotosTask();
        cargarDatosRemotosTask.execute();
    }

    private void applyFontToMenuItem(MenuItem mi,String rutaTipoLetra)
    {
        Typeface font = FontCache.get(rutaTipoLetra,this);//Typeface.createFromAsset(getAssets(), rutaTipoLetra);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem)
    {
        Intent intent;
        switch (menuItem.getItemId())
        {
            case R.id.nav_mi_pedido:
                intent = new Intent(this,PedidoActivity.class);
                startActivityForResult(intent, MI_REQUEST_CODE);
            break;
            case R.id.nav_contacto_sugerencias:
                intent = new Intent(this,ContactoActivity.class);
                startActivity(intent);
            break;
            case R.id.nav_cerrar_sesion:
                cerrarSesion();
            break;
        }
        drawer.closeDrawers();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MI_REQUEST_CODE)
        {
            int  tamano= pager.getAdapter().getCount();
            int posicionActual=pager.getCurrentItem();
            FragmentGeneric frag=(FragmentGeneric)((PagerAdapter)pager.getAdapter()).getItem(posicionActual);

            if(frag!=null)
            {

                frag.actualizarData();
                if((posicionActual+1)<tamano)
                {
                    frag=(FragmentGeneric)((PagerAdapter)pager.getAdapter()).getItem(posicionActual+1);
                    frag.actualizarData();
                }
                if((posicionActual-1)>=0)
                {
                    frag=(FragmentGeneric)((PagerAdapter)pager.getAdapter()).getItem(posicionActual - 1);
                    frag.actualizarData();
                }

            }

            Menu m=navView.getMenu();
            mostrandoMenu(m);
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser == null)
            {
                m.getItem(2).setVisible(false);
                Menu men=m.getItem(2).getSubMenu();
                men.getItem(0).setVisible(false);
            }
        }
    }

    private void irAPedido()
    {
        Intent intent = new Intent(this,PedidoActivity.class);
        startActivityForResult(intent, MI_REQUEST_CODE);
    }
    private void abrirMenuPrincipal()
    {
        drawer.openDrawer(GravityCompat.START);
    }
    @Override
    public void onIrAlPedidoFragmenGrid()
    {
        irAPedido();
    }

    @Override
    public void onAbrirMenuPrincipalFragmentGrid()
    {
        abrirMenuPrincipal();
    }

    @Override
    public void onIrAlPedidoFragment() {
        irAPedido();
    }

    @Override
    public void onAbrirMenuPrincipalFragment() {
        abrirMenuPrincipal();
    }

    @Override
    public void onAbrirDescripcionProducto(String nombre,String descripcion)
    {
        abrirDescripcionProducto(nombre,descripcion);
    }

    private void abrirDescripcionProducto(String nombre,String descripcion)
    {
        final Dialog dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.template_descripcion_producto);
        Typeface TF = FontCache.get(fontStackyard,this);//Typeface.createFromAsset(getAssets(), fontStackyard);

        Button btnCerrar=(Button)dialog.findViewById(R.id.btn_cerrar_descripcion);
        TextView txtdescripcion =(TextView)dialog.findViewById(R.id.descricionProductoDescripcion);
        TextView txtnombre=(TextView)dialog.findViewById(R.id.nombreProductoDescripcion);
        txtdescripcion.setText(descripcion);
        txtnombre.setText(nombre);
        txtnombre.setTypeface(TF);
        txtdescripcion.setTypeface(TF);
        btnCerrar.setTypeface(TF);
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class CargarDatosRemotosTask extends AsyncTask<Void, Void, Boolean>
    {


        @Override
        protected Boolean doInBackground(Void... params)
        {
            return hayConexionInternet();
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);

            Log.i("hay conexion:",""+resultado);
            if(resultado)
            {
                cargarDatosParse();
            }
            else
            {
                mostrarMensajeComprobarConexion();
            }

        }
    }

    private void  mostrarMensajeComprobarConexion()
    {
        text_compruebe_conexion.setVisibility(View.VISIBLE);
        btnRecargarVista.setVisibility(View.VISIBLE);
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
    private void cerrarSesion()
    {
        pd = ProgressDialog.show(this,"", getResources().getString(R.string.por_favor_espere), true, false);

        CerrarSesionTask cst= new CerrarSesionTask();
        cst.execute();

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

    public class CerrarSesionTask extends  AsyncTask<Void,Void,Void>
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
            if(pd!=null)
            {
                pd.dismiss();
            }
        }
    }

}
