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

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.List;

import pirinola24.com.pirinola24.R;
import pirinola24.com.pirinola24.basededatos.AdminSQliteOpenHelper;
import pirinola24.com.pirinola24.modelo.Producto;
import pirinola24.com.pirinola24.util.FontCache;

/**
 * Created by geovanny on 10/01/16.
 */
public class AdaptadorProductoGrid extends BaseAdapter implements View.OnClickListener
{
    private Context context;
    private List<Producto> data;
    private LayoutInflater mInflater;

    private String font_pathOds="font/odstemplik.otf";

    public class ViewHolder
    {

        public ImageView imagenProducto;
        public TextView txtconteo;
        public TextView btnDisminuir;
    }


    public AdaptadorProductoGrid(Context context, List<Producto> data)
    {
        mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.data = data;
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
        ViewHolder viewHolder;

        if(convertView == null)
        {
            v = mInflater.inflate(R.layout.template_producto_grid,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.imagenProducto=(ImageView) v.findViewById(R.id.img_producto);
            viewHolder.txtconteo=(TextView) v.findViewById(R.id.txtconteo);
            viewHolder.btnDisminuir=(TextView) v.findViewById(R.id.btn_disminuir);
            Typeface TF = FontCache.get(font_pathOds,context);
            viewHolder.txtconteo.setTypeface(TF);
            viewHolder.txtconteo.setText("0");
            viewHolder.btnDisminuir.setTag(R.id.txtconteo,viewHolder.txtconteo);
            v.setTag(viewHolder);

        }else
        {
            viewHolder=(ViewHolder)v.getTag();
            viewHolder.txtconteo.setText("0");
        }

        Producto p = (Producto) getItem(position);
        fijarDatos(p, viewHolder, position);



        Glide.with(context)
                .load(p.getImgFile())
                .placeholder(R.drawable.cargando)
                .into(viewHolder.imagenProducto);

        return v;
    }

    private void fijarDatos(Producto producto,ViewHolder viewHolder,int position)
    {

        viewHolder.txtconteo.setVisibility(View.GONE);
        viewHolder.btnDisminuir.setVisibility(View.GONE);
        viewHolder.btnDisminuir.setTag(position);
        viewHolder.btnDisminuir.setOnClickListener(this);
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
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
            if(cantidad!=0)
            {
                viewHolder.txtconteo.setVisibility(View.VISIBLE);
                viewHolder.btnDisminuir.setVisibility(View.VISIBLE);
                viewHolder.txtconteo.setText(cantidad+"");
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        TextView txtconteo=(TextView)v.getTag(R.id.txtconteo);
        String prodid = data.get(Integer.parseInt(v.getTag().toString())).getObjectId();
        DisminuirCantidadTask disminuirCantidadTask= new DisminuirCantidadTask(txtconteo,(TextView)v,context);
        disminuirCantidadTask.execute(data.get(Integer.parseInt(v.getTag().toString())).getObjectId());

    }

    public class DisminuirCantidadTask extends AsyncTask<String,Void,Void>
    {
        private WeakReference<TextView> textViewWeakReference;
        private WeakReference<TextView> imageViewWeakReference;
        private Context context;
        private int cantidad=0;

        public DisminuirCantidadTask(TextView textView,TextView btn,Context context)
        {
            this.textViewWeakReference= new WeakReference<TextView>(textView);
            this.imageViewWeakReference= new WeakReference<TextView>(btn);
            this.context=context;
        }
        @Override
        protected Void doInBackground(String... params)
        {
            MediaPlayer m = MediaPlayer.create(context, R.raw.sonido_click);
            m.start();
            AdminSQliteOpenHelper admin = new AdminSQliteOpenHelper(context,"admin",null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("select prodcantidad from pedido where prodid = '" + params[0] + "'", null);
            if(fila.moveToFirst())
            {
                this.cantidad=fila.getInt(0)-1;
                if(cantidad==0)
                {
                    db.delete("pedido", "prodid ='" + params[0] + "'", null);
                }
                else
                {
                    ContentValues registroPedido= new ContentValues();
                    registroPedido.put("prodcantidad",cantidad);
                    db.update("pedido", registroPedido, "prodid = '" + params[0] + "'", null);
                }
            }
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(cantidad==0)
            {
                imageViewWeakReference.get().setVisibility(View.GONE);
                textViewWeakReference.get().setText(0+"");
                textViewWeakReference.get().setVisibility(View.GONE);
            }
            else
            {
                textViewWeakReference.get().setText(cantidad + "");
            }
        }
    }

}
