package pirinola24.com.pirinola24.adaptadores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.List;

import pirinola24.com.pirinola24.R;
import pirinola24.com.pirinola24.basededatos.AdminSQliteOpenHelper;
import pirinola24.com.pirinola24.modelo.Producto;
import pirinola24.com.pirinola24.util.FontCache;

/**
 * Created by geovanny on 17/01/16.
 */
public class AdaptadorProductoPedido extends BaseAdapter implements View.OnClickListener
{
    private Context context;
    private List<Producto> data;
    private String font_pathOds="font/odstemplik.otf";
    private LayoutInflater mInflater;


    public interface OnDisminuirTotal
    {
        void onDisminuirTotal(int precio);
        void onAumentarTotal(int precio);
    }

    OnDisminuirTotal onDisminuirTotal;

    public class ViewHolder
    {

        public ImageView imagenProducto;
        public ImageView placeholder;
        public TextView txtconteo;
        public TextView btnDisminuir;
        public TextView btnAumentar;

    }

    public AdaptadorProductoPedido(Context context, List<Producto> data)
    {
        mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.data = data;
        onDisminuirTotal = (OnDisminuirTotal) context;
    }

    @Override
    public int getCount()
    {
        if(data!=null)
        {
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position)
    {
        if(data != null && position >= 0 && position < getCount() )
        {
            return data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        final ViewHolder viewHolder;

        if(convertView == null)
        {
            v = mInflater.inflate(R.layout.template_producto_pedido,parent,false);
            viewHolder=new ViewHolder();

            viewHolder.imagenProducto=(ImageView) v.findViewById(R.id.img_producto);
            viewHolder.placeholder=(ImageView)v.findViewById(R.id.placeholder);
            viewHolder.txtconteo=(TextView) v.findViewById(R.id.txtconteo);
            viewHolder.btnDisminuir=(TextView) v.findViewById(R.id.btn_disminuir);
            viewHolder.btnAumentar=(TextView) v.findViewById(R.id.btn_aumentar);
            Typeface TF = FontCache.get(font_pathOds,context);
            viewHolder.txtconteo.setTypeface(TF);
            viewHolder.txtconteo.setText("0");
            viewHolder.btnDisminuir.setTag(R.id.txtconteo, viewHolder.txtconteo);
            viewHolder.btnAumentar.setTag(R.id.txtconteo,viewHolder.txtconteo);
            v.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder)v.getTag();
        }

        Producto p = (Producto) getItem(position);
        fijarDatos(p, viewHolder, position);

        viewHolder.placeholder.setVisibility(View.VISIBLE);
        viewHolder.placeholder.setImageResource(R.drawable.carga);

        Picasso.with(context)
                .load(p.getImgFile())
                .into(viewHolder.imagenProducto, new Callback() {

                    @Override
                    public void onSuccess() {
                        viewHolder.placeholder.setImageDrawable(null);
                        viewHolder.placeholder.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError() {

                    }
                });

        return v;
    }

    private void fijarDatos(Producto producto,ViewHolder viewHolder,int position)
    {
        viewHolder.btnDisminuir.setTag(position);
        viewHolder.btnAumentar.setTag(position);
        viewHolder.btnDisminuir.setOnClickListener(this);
        viewHolder.btnAumentar.setOnClickListener(this);
        FijarCantidadTask fijarCantidadTask=new FijarCantidadTask(context,viewHolder);
        fijarCantidadTask.execute(producto.getObjectId());
    }

    public class FijarCantidadTask extends AsyncTask<String,Void,Void>
    {
        ViewHolder viewHolder;
        Context context;
        int cantidad=0;

        public FijarCantidadTask(Context context,ViewHolder viewHolder)
        {
            this.viewHolder=viewHolder;
            this.context=context;
        }

        @Override
        protected Void doInBackground(String... params)
        {
            AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(context,"admin",null,1);

            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '"+params[0]+"'",null);
            if(fila.moveToFirst())
            {
                cantidad=fila.getInt(0);
            }
            db.close();
            return  null;
        }

        @Override
        protected void onPostExecute(Void avoid)
        {
            super.onPostExecute(avoid);
            viewHolder.txtconteo.setText(cantidad+"");
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_aumentar:
                TextView conteo=(TextView)v.getTag(R.id.txtconteo);
                Producto prod= data.get(Integer.parseInt(v.getTag().toString()));
                aumentarproducto(prod, conteo);
                break;
            case R.id.btn_disminuir:
                TextView txtconteo=(TextView)v.getTag(R.id.txtconteo);
                Producto p= data.get(Integer.parseInt(v.getTag().toString()));
                disminuirproducto(p,txtconteo,Integer.parseInt(v.getTag().toString()));
            break;
        }

    }

    private void aumentarproducto(Producto producto,TextView txtconteo)
    {
        MediaPlayer m = MediaPlayer.create(context, R.raw.sonido_click);
        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        m.start();
        int conteo = Integer.parseInt(txtconteo.getText().toString());
        conteo= conteo+1;
        txtconteo.setText(conteo+"");

        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(context,"admin",null,1);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues registroPedido= new ContentValues();
        registroPedido.put("prodcantidad",conteo);

        int cant= db.update("pedido",registroPedido,"prodid = '"+producto.getObjectId()+"'",null);

        db.close();
        onDisminuirTotal.onAumentarTotal(producto.getPrecio());
    }

    private void disminuirproducto(Producto producto,TextView txtconteo,int posicion)
    {
        int cantidad=0;
        MediaPlayer m = MediaPlayer.create(context, R.raw.sonido_click);
        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        m.start();

        AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(context,"admin",null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '" + producto.getObjectId() + "'", null);
        if(fila.moveToFirst())
        {
            cantidad=fila.getInt(0)-1;
            if(cantidad==0)
            {
                db.delete("pedido", "prodid ='" + producto.getObjectId() + "'", null);
            }
            else
            {
                ContentValues registroPedido= new ContentValues();
                registroPedido.put("prodcantidad",cantidad);
                db.update("pedido", registroPedido, "prodid = '" + producto.getObjectId() + "'", null);
            }
        }
        db.close();

        if(cantidad==0)
        {
            data.remove(posicion);
            notifyDataSetChanged();
        }
        else
        {
            txtconteo.setText(cantidad + "");
        }
        onDisminuirTotal.onDisminuirTotal(producto.getPrecio());
    }


}
