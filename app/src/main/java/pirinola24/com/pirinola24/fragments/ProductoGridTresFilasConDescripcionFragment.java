package pirinola24.com.pirinola24.fragments;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import pirinola24.com.pirinola24.R;
import pirinola24.com.pirinola24.adaptadores.AdaptadorProductoGridConDescripcion;
import pirinola24.com.pirinola24.basededatos.AdminSQliteOpenHelper;
import pirinola24.com.pirinola24.modelo.Producto;
import pirinola24.com.pirinola24.util.AppUtil;
import pirinola24.com.pirinola24.util.FontCache;


public class ProductoGridTresFilasConDescripcionFragment extends FragmentGeneric implements AdapterView.OnItemClickListener, View.OnClickListener,AdaptadorProductoGridConDescripcion.OnComunicationAdaptador{

    private static final String LIST_STATE = "listState";

    private Parcelable mListState = null;
    private String subcategoria;
    private String subcategoriaNombre;
    private TextView titulo;
    private GridView gridProductos;
    private List<Producto> data= new ArrayList<>();
    private AdaptadorProductoGridConDescripcion adapter;
    private ImageView carritoCompras;
    private ImageView menuPrincipal;
    private String fontStackyard="font/Stackyard.ttf";
    private String matura_mt="font/matura_mt.ttf";

    public ProductoGridTresFilasConDescripcionFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onComunicationFragment=(OnComunicationFragmentGridCondescripcion)context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.carrito_compras:
                onComunicationFragment.onIrAlPedidoFragmentGridCondescripcion();
                break;

            case R.id.btn_menu_principal:
                onComunicationFragment.onAbrirMenuPrincipalFragmentGridCondescripcion();
                break;
        }
    }

    @Override
    public void onMostrarDescripcionProducto(String nombre,String descripcion)
    {
        onComunicationFragment.onAbrirDescripcionProductoGridCondescripcion(nombre, descripcion);
    }

    public interface OnComunicationFragmentGridCondescripcion
    {
        void onIrAlPedidoFragmentGridCondescripcion();
        void onAbrirMenuPrincipalFragmentGridCondescripcion();
        void onAbrirDescripcionProductoGridCondescripcion(String nombre,String descripcion);
    }
    OnComunicationFragmentGridCondescripcion onComunicationFragment;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null)
        {
            subcategoria=savedInstanceState.getString("subcategoria");
            subcategoriaNombre=savedInstanceState.getString("subcategoriaNombre");
            mListState=savedInstanceState.getParcelable(LIST_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v= inflater.inflate(R.layout.fragment_producto_grid_tres_filas_con_descripcion, container, false);
        gridProductos= (GridView) v.findViewById(R.id.gridProductos);
        titulo=(TextView)v.findViewById(R.id.tituloSubcategoria);

        adapter= new AdaptadorProductoGridConDescripcion(v.getContext(),data,this);
        gridProductos.setAdapter(adapter);
        gridProductos.setOnItemClickListener(this);

        carritoCompras=(ImageView)v.findViewById(R.id.carrito_compras);
        menuPrincipal=(ImageView)v.findViewById(R.id.btn_menu_principal);
        carritoCompras.setOnClickListener(this);
        menuPrincipal.setOnClickListener(this);

        titulo.setText(subcategoriaNombre);
        Typeface TF= FontCache.get(matura_mt, v.getContext());
        titulo.setTypeface(TF);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
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

    public void init(String subcategoria,String subcategoriaNombre)
    {

        this.subcategoria=subcategoria;
        this.subcategoriaNombre=subcategoriaNombre;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        mListState = gridProductos.onSaveInstanceState();
        outState.putString("subcategoria", subcategoria);
        outState.putString("subcategoriaNombre", subcategoriaNombre);
        outState.putParcelable(LIST_STATE, mListState);
        super.onSaveInstanceState(outState);
    }

    private void loadData()
    {
        LoadDataTask loadDataTask= new LoadDataTask();
        loadDataTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        TextView textconteo= (TextView) view.findViewById(R.id.txtconteo);
        TextView btnDisminuir= (TextView) view.findViewById(R.id.btn_disminuir);
        AgregarProductoPedidoTask agregarProductoPedidoTask= new AgregarProductoPedidoTask(textconteo,btnDisminuir,getContext());
        agregarProductoPedidoTask.execute(data.get(position));
    }

    public class AgregarProductoPedidoTask extends AsyncTask<Producto,Void,Integer>
    {
        private WeakReference<TextView> txtcontedo;
        private WeakReference<TextView> btndisminuir;
        private  Context context;

        public AgregarProductoPedidoTask(TextView conteo,TextView disminuir, Context context)
        {
            txtcontedo= new WeakReference<>(conteo);
            btndisminuir= new WeakReference<>(disminuir);
            this.context=context;
        }
        @Override
        protected Integer doInBackground(Producto... params)
        {
            MediaPlayer m = MediaPlayer.create(context,R.raw.sonido_click);
            m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            m.start();
            AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(getContext(),"admin",null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '" + params[0].getObjectId() + "'", null);
            ContentValues registroPedido= new ContentValues();
            int conteo=1;
            if(fila.moveToFirst())
            {
                conteo=fila.getInt(0)+1;
                registroPedido.put("prodcantidad",conteo);
                db.update("pedido",registroPedido,"prodid = '"+params[0].getObjectId()+"'",null);
            }
            else
            {
                registroPedido.put("prodid",params[0].getObjectId());
                registroPedido.put("prodprecio",params[0].getPrecio());
                registroPedido.put("prodnombre",params[0].getProdnombre());
                registroPedido.put("proddescripcion",params[0].getProddescripcion());
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
