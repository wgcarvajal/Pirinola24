package pirinola24.com.pirinola24.modelo;


public class Subcategoria
{
    public static final int DOSFILASCONDESCRIPCION=1;
    public static final int SINDESCRIPCION=2;
    public static final int TRESFILASCONDESCRIPCION=3;
    public static final int ANUNCIO=4;

    public static String TABLA="Subcategoria";
    public static String ID="objectId";
    public static String IMGTITULO="imgTitulo";
    public static String POSICION="posicion";
    public static String NOMBRE="subcatnombre";
    public static String TIPOFRAGMENT="tipoFragment";


    private String objectId;
    private String imgTitulo;
    private int posicion;
    private String subcatnombre;
    private int domicilio;
    private int minimopedido;
    private int tipoFragment;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectID) {
        this.objectId = objectID;
    }

    public String getImgTitulo() {
        return imgTitulo;
    }

    public void setImgTitulo(String imgTitulo) {
        this.imgTitulo = imgTitulo;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getSubcatnombre() {
        return subcatnombre;
    }

    public void setSubcatnombre(String subcatnombre) {
        this.subcatnombre = subcatnombre;
    }

    public int getTipoFragment() {
        return tipoFragment;
    }

    public void setTipoFragment(int tipoFragment) {
        this.tipoFragment = tipoFragment;
    }

    public int getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(int domicilio) {
        this.domicilio = domicilio;
    }

    public int getMinimopedido() {
        return minimopedido;
    }

    public void setMinimopedido(int minimopedido) {
        this.minimopedido = minimopedido;
    }
}
