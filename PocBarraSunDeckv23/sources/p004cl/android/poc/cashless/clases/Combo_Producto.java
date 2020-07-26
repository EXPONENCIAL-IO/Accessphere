package p004cl.android.poc.cashless.clases;

import java.util.ArrayList;

/* renamed from: cl.android.poc.cashless.clases.Combo_Producto */
public class Combo_Producto {
    String id_producto_padre;
    int max;
    int min;
    ArrayList<Producto> productos_combo;
    int quantity;
    String tags;

    public Combo_Producto() {
        this.id_producto_padre = "";
        this.quantity = 0;
        this.min = 0;
        this.max = 0;
        this.tags = "";
        this.productos_combo = new ArrayList<>();
    }

    public Combo_Producto(String id_producto_padre2, int quantity2, int min2, int max2, String tags2, ArrayList<Producto> productos_combo2) {
        this.id_producto_padre = id_producto_padre2;
        this.quantity = quantity2;
        this.min = min2;
        this.max = max2;
        this.tags = tags2;
        this.productos_combo = productos_combo2;
    }

    public String getId_producto_padre() {
        return this.id_producto_padre;
    }

    public void setId_producto_padre(String id_producto_padre2) {
        this.id_producto_padre = id_producto_padre2;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity2) {
        this.quantity = quantity2;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min2) {
        this.min = min2;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max2) {
        this.max = max2;
    }

    public String getTags() {
        return this.tags;
    }

    public void setTags(String tags2) {
        this.tags = tags2;
    }

    public ArrayList<Producto> getProductos_combo() {
        return this.productos_combo;
    }

    public void setProductos_combo(ArrayList<Producto> productos_combo2) {
        this.productos_combo = productos_combo2;
    }
}
