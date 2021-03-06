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
import pirinola24.com.pirinola24.modelo.Formapago;
import pirinola24.com.pirinola24.util.FontCache;

/**
 * Created by geovanny on 20/01/16.
 */
public class AdaptadorSpinnerFormaPago extends ArrayAdapter {

    private String font_path="font/A_Simple_Life.ttf";
    private List<Formapago> data;
    private Context context;

    public AdaptadorSpinnerFormaPago(Context context, int resource, List objects) {
        super(context, resource, objects);

        this.context=context;
        this.data=objects;
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
        Typeface TF = FontCache.get(font_path,context); //Typeface.createFromAsset(context.getAssets(), font_path);
        item.setText(((Formapago)data.get(position)).getNombre());
        if(position==0)
        {
            item.setTextColor(Color.parseColor("#000000"));
        }
        item.setTypeface(TF);


        return v;
    }
}

