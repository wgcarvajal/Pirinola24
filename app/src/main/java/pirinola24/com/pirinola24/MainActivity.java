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

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.facebook.login.LoginManager;
import com.viewpagerindicator.PageIndicator;

import java.util.*;

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
        pager.setPageTransformer(true, new CubeOutTransformer());
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
        {for(Subcategoria sub: AppUtil.listaSubcategorias)
            {
                switch (sub.getTipoFragment())
                {
                    case Subcategoria.CONDESCRIPCION:
                        ProductoFragment productoFragment = new ProductoFragment();
                        productoFragment.init(sub.getObjectId(),sub.getImgTitulo());
                        data.add(productoFragment);
                    break;
                    case Subcategoria.SINDESCRIPCION:
                        ProductoGridFragment productoGridFragment = new ProductoGridFragment();
                        productoGridFragment.init(sub.getObjectId(),sub.getImgTitulo());
                        data.add(productoGridFragment);
                    break;
                }

            }
            adapter.notifyDataSetChanged();
            Menu m=navView.getMenu();
            mostrandoMenu(m);

            BackendlessUser currentUser = Backendless.UserService.CurrentUser();
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
        BackendlessDataQuery dataQuerysubcategoria= new BackendlessDataQuery();
        QueryOptions queryOptionsSubcategoria= new QueryOptions();
        queryOptionsSubcategoria.setPageSize(100);
        queryOptionsSubcategoria.addSortByOption("posicion ASC");
        dataQuerysubcategoria.setQueryOptions(queryOptionsSubcategoria);
        Backendless.Persistence.of(Subcategoria.class).find(dataQuerysubcategoria, new AsyncCallback<BackendlessCollection<Subcategoria>>() {
            @Override
            public void handleResponse(BackendlessCollection<Subcategoria> response) {
                AppUtil.listaSubcategorias= response.getData();
                BackendlessDataQuery dataQueryProductos= new BackendlessDataQuery();
                QueryOptions queryOptionsProductos= new QueryOptions();
                queryOptionsProductos.setPageSize(100);
                queryOptionsProductos.addSortByOption("posicion ASC");
                dataQueryProductos.setQueryOptions(queryOptionsProductos);
                Backendless.Persistence.of(Producto.class).find(dataQueryProductos, new AsyncCallback<BackendlessCollection<Producto>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Producto> prods)
                    {
                        AppUtil.data=prods.getData();



                        if(prods.getTotalObjects()>100)
                        {
                            prods.nextPage(new AsyncCallback<BackendlessCollection<Producto>>() {
                                @Override
                                public void handleResponse(BackendlessCollection<Producto> response) {
                                    List<Producto> pds=response.getData();
                                    for(Producto p :pds)
                                    {
                                        AppUtil.data.add(p);
                                    }

                                    crearFragments();
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    mostrarMensajeComprobarConexion();
                                }
                            });
                        }
                        else
                        {
                            crearFragments();
                        }
                    }
                    @Override
                    public void handleFault(BackendlessFault fault)
                    {
                        mostrarMensajeComprobarConexion();
                    }
                });
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                mostrarMensajeComprobarConexion();
            }
        });
    }

    private void crearFragments()
    {
        for (Subcategoria sub : AppUtil.listaSubcategorias)
        {

            switch (sub.getTipoFragment())
            {
                case Subcategoria.CONDESCRIPCION:
                    ProductoFragment productoFragment = new ProductoFragment();
                    productoFragment.init(sub.getObjectId(), sub.getImgTitulo());
                    data.add(productoFragment);
                    break;
                case Subcategoria.SINDESCRIPCION:
                    ProductoGridFragment productoGridFragment = new ProductoGridFragment();
                    productoGridFragment.init(sub.getObjectId(), sub.getImgTitulo());
                    data.add(productoGridFragment);
                    break;
            }

        }
        adapter.notifyDataSetChanged();
        final Menu m = navView.getMenu();
        mostrandoMenu(m);
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {

                if (response) {
                    String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
                    Backendless.Data.of(BackendlessUser.class).findById(currentUserObjectId, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response)
                        {
                            Backendless.UserService.setCurrentUser(response);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault)
                        {
                            m.getItem(2).setVisible(false);
                            Menu men = m.getItem(2).getSubMenu();
                            men.getItem(0).setVisible(false);

                        }
                    });
                } else {
                    m.getItem(2).setVisible(false);
                    Menu men = m.getItem(2).getSubMenu();
                    men.getItem(0).setVisible(false);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault)
            {
                m.getItem(2).setVisible(false);
                Menu men = m.getItem(2).getSubMenu();
                men.getItem(0).setVisible(false);
            }
        });

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
            BackendlessUser currentUser = Backendless.UserService.CurrentUser();
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
        Typeface TF = FontCache.get(fontStackyard,this);

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
            Backendless.UserService.logout();
            LoginManager.getInstance().logOut();
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
