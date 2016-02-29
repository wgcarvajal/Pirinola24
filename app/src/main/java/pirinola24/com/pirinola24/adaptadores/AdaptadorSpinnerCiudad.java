package pirinola24.com.pirinola24.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pirinola24.com.pirinola24.R;
import pirinola24.com.pirinola24.modelo.Ciudad;
import pirinola24.com.pirinola24.util.FontCache;

/**
 * Created by geovanny on 27/02/16.
 */
public class AdaptadorSpinnerCiudad extends ArrayAdapter
{
    private String font_path="font/A_Simple_Life.ttf";
    private List<Ciudad> data;
    private List<String> lista;
    private Context context;

    public AdaptadorSpinnerCiudad(Context context, int resource, List<Ciudad> objects,List<String> lista) {
        super(context, resource, lista);

        this.context=context;
        this.data=objects;
        this.lista=lista;
    }


    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {


        View v=null;

        if(convertView == null)
        {
            v = View.inflate(context, R.layout.template_spinner_forma_pago ,null);

        }else
        {
            v = convertView;
        }

        TextView item=(TextView)v.findViewById(R.id.txt_item_spinner_forma_pago);
        Typeface TF = FontCache.get(font_path, context);

        if(position==0)
        {
            item.setText(lista.get(position));
            item.setTag("0");
            item.setTextColor(Color.parseColor("#033a0f"));
        }
        else
        {
            item.setText(data.get(position-1).getNombre());
            item.setTag(data.get(position-1).getObjectId());
        }
        item.setTypeface(TF);
        return v;
    }
}
