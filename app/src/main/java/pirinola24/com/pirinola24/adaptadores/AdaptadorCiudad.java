package pirinola24.com.pirinola24.adaptadores;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pirinola24.com.pirinola24.R;
import pirinola24.com.pirinola24.modelo.Ciudad;
import pirinola24.com.pirinola24.util.FontCache;

/**
 * Created by geovanny on 29/02/16.
 */
public class AdaptadorCiudad extends BaseAdapter
{
    private Context context;
    private List<Ciudad> data;
    private LayoutInflater mInflater;
    private String fontStackyard="font/Stackyard.ttf";


    public AdaptadorCiudad(Context context, List<Ciudad> data)
    {
        mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data=data;
        this.context=context;
    }

    public class ViewHolder
    {
        public TextView txtnombreciudad;
    }

    @Override
    public int getCount() {
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
            Typeface TF= FontCache.get(fontStackyard,context);
            viewHolder= new ViewHolder();
            v = mInflater.inflate(R.layout.template_ciudad,parent,false);
            viewHolder.txtnombreciudad=(TextView)v.findViewById(R.id.nombreciudad);
            viewHolder.txtnombreciudad.setTypeface(TF);
            v.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder)v.getTag();
        }
        Ciudad ciudad= data.get(position);
        viewHolder.txtnombreciudad.setText(ciudad.getNombre());
        return v;
    }
}
