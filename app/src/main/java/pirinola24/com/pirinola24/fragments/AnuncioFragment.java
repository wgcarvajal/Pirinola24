package pirinola24.com.pirinola24.fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pirinola24.com.pirinola24.R;
import pirinola24.com.pirinola24.modelo.Anuncio;
import pirinola24.com.pirinola24.util.AppUtil;
import pirinola24.com.pirinola24.util.FontCache;


public class AnuncioFragment extends FragmentGeneric
{
    private String title;
    private String subcategoriaId;
    private TextView titulo;
    private ImageView anuncio;
    private ImageView placeholder;

    private String fontStackyard="font/Stackyard.ttf";

    public AnuncioFragment()
    {
        // Required empty public constructor
    }

    public void init(String title,String subcategoriaId)
    {
        this.title=title;
        this.subcategoriaId=subcategoriaId;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putString("title", title);
        outState.putString("subcategoriaId",subcategoriaId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null)
        {
            title=savedInstanceState.getString("title");
            subcategoriaId=savedInstanceState.getString("subcategoriaId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_anuncio, container, false);

        titulo=(TextView) v.findViewById(R.id.tituloAnuncio);
        anuncio=(ImageView)v.findViewById(R.id.idanuncio);
        placeholder=(ImageView)v.findViewById(R.id.placeholder);

        Typeface TF= FontCache.get(fontStackyard,inflater.getContext());
        titulo.setTypeface(TF);
        titulo.setText(title);

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        loadAnuncio();
    }

    private void loadAnuncio()
    {
        int bandera=0;
        for(Anuncio a : AppUtil.listaAnuncios)
        {
            if(a.getSubcategoria().equals(subcategoriaId))
            {
                Log.i("ya habia", "un anuncio guardado");
                bandera=1;

               placeholder.setVisibility(View.VISIBLE);
               placeholder.setImageResource(R.drawable.carga);

                Picasso.with(getContext())
                        .load(a.getUrlImagen())
                        .into(anuncio, new Callback() {

                            @Override
                            public void onSuccess()
                            {
                                placeholder.setImageDrawable(null);
                                placeholder.setVisibility(View.GONE);

                            }
                            @Override
                            public void onError()
                            {

                            }
                        });

            }

        }
        if(bandera==0)
        {
            cargarDatoRemoto();
        }
    }

    private void cargarDatoRemoto()
    {
        BackendlessDataQuery dataQueryanuncio= new BackendlessDataQuery();
        List<String> anuncioSelect=new ArrayList<>();
        anuncioSelect.add("objectId");
        anuncioSelect.add("urlImagen");
        anuncioSelect.add("subcategoria");

        dataQueryanuncio.setProperties(anuncioSelect);
        dataQueryanuncio.setWhereClause("subcategoria='"+subcategoriaId+"'");
        QueryOptions queryOptionsAnuncio= new QueryOptions();
        queryOptionsAnuncio.setPageSize(100);
        dataQueryanuncio.setQueryOptions(queryOptionsAnuncio);
        Backendless.Persistence.of(Anuncio.class).findFirst(new AsyncCallback<Anuncio>() {
            @Override
            public void handleResponse(Anuncio response)
            {
                AppUtil.listaAnuncios.add(response);

                placeholder.setVisibility(View.VISIBLE);
                placeholder.setImageResource(R.drawable.carga);

                Picasso.with(getContext())
                        .load(response.getUrlImagen())
                        .into(anuncio, new Callback() {

                            @Override
                            public void onSuccess() {
                                placeholder.setImageDrawable(null);
                                placeholder.setVisibility(View.GONE);

                            }

                            @Override
                            public void onError() {

                            }
                        });
            }

            @Override
            public void handleFault(BackendlessFault fault)
            {

            }
        });


    }

    @Override
    public void actualizarData()
    {

    }
}
