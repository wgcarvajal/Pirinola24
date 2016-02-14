package pirinola24.com.pirinola24.adaptadores;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.List;

import pirinola24.com.pirinola24.R;
import pirinola24.com.pirinola24.basededatos.AdminSQliteOpenHelper;
import pirinola24.com.pirinola24.modelo.Producto;

/**
 * Created by geovanny on 17/01/16.
 */
public class AdaptadorProductoPedido extends BaseAdapter implements View.OnClickListener
{
    private Context context;
    private List<Producto> data;
    private String font_path = "font/2-4ef58.ttf";
    private String font_pathOds="font/odstemplik.otf";
    private Typeface TF;
    private LayoutInflater mInflater;


    public interface OnDisminuirTotal
    {
        void onDisminuirTotal(int precio);
    }

    OnDisminuirTotal onDisminuirTotal;

    public class ViewHolder
    {

        public ImageView imagenProducto;
        public TextView txtconteo;
        public ImageView btnDisminuir;

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
        ViewHolder viewHolder;

        if(convertView == null)
        {
            v = mInflater.inflate(R.layout.template_producto_pedido,parent,false);
            viewHolder=new ViewHolder();

            viewHolder.imagenProducto=(ImageView) v.findViewById(R.id.img_producto);
            viewHolder.txtconteo=(TextView) v.findViewById(R.id.txtconteo);
            viewHolder.btnDisminuir=(ImageView) v.findViewById(R.id.btn_disminuir);


            TF = Typeface.createFromAsset(context.getAssets(),font_pathOds);
            viewHolder.txtconteo.setTypeface(TF);
            viewHolder.txtconteo.setText("0");
            viewHolder.btnDisminuir.setTag(R.id.txtconteo,viewHolder.txtconteo);
            v.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder)v.getTag();
        }

        final Producto p = (Producto) getItem(position);
        fijarDatos(p, viewHolder, context.getResources().getString(R.string.idioma), position);


        final View fv=v;

        Picasso.with(context)
                .load(Uri.parse(p.getUrlImagenPedido()))
                .into(viewHolder.imagenProducto);
        return v;
    }

    private void fijarDatos(Producto producto,ViewHolder viewHolder,String idioma,int position)
    {



        viewHolder.btnDisminuir.setTag(position);
        viewHolder.btnDisminuir.setOnClickListener(this);
        FijarCantidadTask fijarCantidadTask=new FijarCantidadTask(context,viewHolder);
        fijarCantidadTask.execute(producto.getId());
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
        TextView txtconteo=(TextView)v.getTag(R.id.txtconteo);
        int precio= data.get(Integer.parseInt(v.getTag().toString())).getPrecio();
        String prodid = data.get(Integer.parseInt(v.getTag().toString())).getId();
        DisminuirCantidadTask disminuirCantidadTask= new DisminuirCantidadTask(txtconteo,(ImageView)v,context,Integer.parseInt(v.getTag().toString()),precio);
        disminuirCantidadTask.execute(data.get(Integer.parseInt(v.getTag().toString())).getId());
    }



    public class DisminuirCantidadTask extends AsyncTask<String,Void,Void>
    {
        private WeakReference<TextView> textViewWeakReference;
        private WeakReference<ImageView> imageViewWeakReference;
        private Context context;
        private int posicion;
        private int cantidad=0;
        private int precio=0;

        public DisminuirCantidadTask(TextView textView,ImageView btn,Context context,int posicion,int precio)
        {
            this.textViewWeakReference= new WeakReference<TextView>(textView);
            this.imageViewWeakReference= new WeakReference<ImageView>(btn);
            this.posicion=posicion;
            this.context=context;
            this.precio=precio;
        }
        @Override
        protected Void doInBackground(String... params)
        {
            MediaPlayer m = MediaPlayer.create(context, R.raw.sonido_click);
            m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
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
                data.remove(posicion);
                notifyDataSetChanged();
            }
            else
            {
                textViewWeakReference.get().setText(cantidad + "");
            }
            onDisminuirTotal.onDisminuirTotal(precio);
        }
    }



    //---------------------------------------------------------------------------

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(byte [] data, int offset,int length,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, offset, length, options);
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private byte [] data = null;
        private String prodid="";
        private Producto producto;

        public BitmapWorkerTask(ImageView imageView,byte [] data,String prodid,Producto producto)
        {
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.data=data;
            this.prodid=prodid;
            this.producto=producto;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            Bitmap bitmap= decodeSampledBitmapFromResource(data, 0,data.length,80,80);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            if (isCancelled())
            {
                bitmap = null;
            }
            if (imageViewReference != null && bitmap != null)
            {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null)
                {
                    imageView.setImageBitmap(bitmap);
                   // producto.setImagen(bitmap);
                }
            }
        }
    }
    public void loadBitmap(byte[] data , ImageView imageView,String prodid,Context context,Producto producto)
    {

        if (cancelPotentialWork(prodid, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView,data,prodid,producto);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(context.getResources(), null, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute();
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }

    }

    public static boolean cancelPotentialWork(String prodid, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapProdid = bitmapWorkerTask.prodid;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapProdid.equals("") || !bitmapProdid.equals(prodid)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

}
