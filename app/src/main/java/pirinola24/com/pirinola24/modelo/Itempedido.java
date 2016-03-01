package pirinola24.com.pirinola24.modelo;

public class Itempedido
{
    private String objectId;
    private int itemcantidad;
    private String pedido;
    private String producto;

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public int getItemcantidad() {
        return itemcantidad;
    }

    public void setItemcantidad(int itemcantidad) {
        this.itemcantidad = itemcantidad;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }


}
