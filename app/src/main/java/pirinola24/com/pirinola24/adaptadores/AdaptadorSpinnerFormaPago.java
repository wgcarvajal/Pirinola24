package pirinola24.com.pirinola24.adaptadores;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pirinola24.com.pirinola24.R;

/**
 * Created by geovanny on 20/01/16.
 */
public class AdaptadorSpinnerFormaPago extends ArrayAdapter {

    private String font_path="font/A_Simple_Life.ttf";
    private Typeface TF;
    private List<String> data;
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
        TF = Typeface.createFromAsset(context.getAssets(), font_path);
        item.setText(data.get(position));
        item.setTypeface(TF);


        return v;
    }
}

