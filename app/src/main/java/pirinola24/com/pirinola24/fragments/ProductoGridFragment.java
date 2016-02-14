package pirinola24.com.pirinola24.fragments;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import pirinola24.com.pirinola24.R;
import pirinola24.com.pirinola24.adaptadores.AdaptadorProductoGrid;
import pirinola24.com.pirinola24.basededatos.AdminSQliteOpenHelper;
import pirinola24.com.pirinola24.modelo.Producto;
import pirinola24.com.pirinola24.util.AppUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductoGridFragment extends FragmentGeneric implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String LIST_STATE = "listState";

    private Parcelable mListState = null;
    private String subcategoria;
    private String urlimagen;
    private ImageView titulo;
    private GridView gridProductos;
    private List<Producto> data = new ArrayList<>();
    private AdaptadorProductoGrid adapter;
    private String font_path = "font/2-4ef58.ttf";
    private Typeface TF;
    private ImageView carritoCompras;
    private ImageView menuPrincipal;

    public ProductoGridFragment()
    {

    }

    public interface OnComunicationFragmentGrid
    {
        void onIrAlPedidoFragmenGrid();
        void onAbrirMenuPrincipalFragmentGrid();
    }

    OnComunicationFragmentGrid onComunicationFragmentGrid;


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        onComunicationFragmentGrid=(OnComunicationFragmentGrid)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null)
        {
            subcategoria=savedInstanceState.getString("subcategoria");
            urlimagen=savedInstanceState.getString("urlimagen");
            mListState=savedInstanceState.getParcelable(LIST_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_producto_grid, container, false);

        gridProductos= (GridView) v.findViewById(R.id.gridProductos);
        titulo = (ImageView) v.findViewById(R.id.tituloSubcategoria);
        adapter= new AdaptadorProductoGrid(v.getContext(),data);
        gridProductos.setAdapter(adapter);
        gridProductos.setOnItemClickListener(this);

        carritoCompras=(ImageView)v.findViewById(R.id.carrito_compras);
        menuPrincipal=(ImageView)v.findViewById(R.id.btn_menu_principal);
        carritoCompras.setOnClickListener(this);
        menuPrincipal.setOnClickListener(this);

        Picasso.with(v.getContext())
                .load(Uri.parse(urlimagen))
                .into(titulo);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(data.size()>0)
        {

            adapter.notifyDataSetChanged();
        }
        else
        {
            loadData();
        }
    }

    public void init(String subcategoria,String urlimagen)
    {
        this.subcategoria=subcategoria;
        this.urlimagen=urlimagen;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        mListState = gridProductos.onSaveInstanceState();
        outState.putString("subcategoria", subcategoria);
        outState.putString("urlimagen", urlimagen);
        outState.putParcelable(LIST_STATE, mListState);
        super.onSaveInstanceState(outState);
    }

    public void loadData()
    {
        LoadDataTask loadDataTask= new LoadDataTask();
        loadDataTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        TextView textconteo= (TextView) view.findViewById(R.id.txtconteo);
        ImageView btnDisminuir= (ImageView) view.findViewById(R.id.btn_disminuir);
        AgregarProductoPedidoTask agregarProductoPedidoTask= new AgregarProductoPedidoTask(textconteo,btnDisminuir);
        agregarProductoPedidoTask.execute(data.get(position));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.carrito_compras:
                onComunicationFragmentGrid.onIrAlPedidoFragmenGrid();
            break;

            case R.id.btn_menu_principal:
                onComunicationFragmentGrid.onAbrirMenuPrincipalFragmentGrid();
            break;
        }
    }

    public class AgregarProductoPedidoTask extends AsyncTask<Producto,Void,Integer>
    {
        private WeakReference<TextView> txtcontedo;
        private WeakReference<ImageView> btndisminuir;

        public AgregarProductoPedidoTask(TextView conteo,ImageView disminuir)
        {
            txtcontedo= new WeakReference<TextView>(conteo);
            btndisminuir= new WeakReference<ImageView>(disminuir);
        }
        @Override
        protected Integer doInBackground(Producto... params)
        {
            MediaPlayer m = MediaPlayer.create(getContext(),R.raw.sonido_click);
            m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            m.start();
            AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(getContext(),"admin",null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '" + params[0].getId() + "'", null);
            ContentValues registroPedido= new ContentValues();
            int conteo=1;
            if(fila.moveToFirst())
            {
                conteo=fila.getInt(0)+1;
                registroPedido.put("prodcantidad",conteo);
                db.update("pedido",registroPedido,"prodid = '"+params[0].getId()+"'",null);
            }
            else
            {
                registroPedido.put("prodid",params[0].getId());
                registroPedido.put("prodprecio",params[0].getPrecio());
                registroPedido.put("prodnombre",params[0].getNombre());
                registroPedido.put("proddescripcion",params[0].getDescripcion());
                registroPedido.put("prodcantidad",conteo);
                db.insert("pedido",null,registroPedido);
            }
            db.close();
            return conteo;
        }

        @Override
        protected void onPostExecute(Integer resultado) {
            super.onPostExecute(resultado);
            txtcontedo.get().setText("" + resultado);
            txtcontedo.get().setVisibility(View.VISIBLE);
            btndisminuir.get().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void actualizarData()
    {
        if(adapter!=null)
        {
            adapter.notifyDataSetChanged();
        }
    }

    public class LoadDataTask extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            for(Producto prod: AppUtil.data)
            {
                if(prod.getSubcategoria().equals(subcategoria))
                {
                    data.add(prod);
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if (mListState != null)
            {
                gridProductos.onRestoreInstanceState(mListState);
            }
            adapter.notifyDataSetChanged();
            mListState = null;
        }
    }
}
