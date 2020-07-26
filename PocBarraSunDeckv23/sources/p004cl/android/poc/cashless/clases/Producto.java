package p004cl.android.poc.cashless.clases;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

/* renamed from: cl.android.poc.cashless.clases.Producto */
public class Producto implements Parcelable {
    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };
    Drawable drawable_producto;
    int drawrable_foto;
    int drawrable_foto_des;
    String es_combo;
    String id_combo;
    String id_producto;
    int monto;
    String nombre_producto;
    String ruta_foto;
    String tag;
    String tipo_producto;

    public Producto() {
        this.monto = 0;
        this.drawrable_foto = 0;
        this.nombre_producto = "";
        this.id_producto = "";
        this.ruta_foto = "";
        this.tipo_producto = "";
        this.tag = "";
        this.drawrable_foto_des = 0;
        this.id_combo = "";
        this.es_combo = "";
    }

    public Producto(int monto2, int drawrable_foto2, String nombre_producto2, String id_producto2, String tipo_producto2, String tag2, int drawrable_foto_des2, String id_combo2, String es_combo2) {
        this.monto = monto2;
        this.drawrable_foto = drawrable_foto2;
        this.nombre_producto = nombre_producto2;
        this.id_producto = id_producto2;
        this.tipo_producto = tipo_producto2;
        this.tag = tag2;
        this.drawrable_foto_des = drawrable_foto_des2;
        this.id_combo = id_combo2;
        this.es_combo = es_combo2;
    }

    public Producto(int monto2, Drawable drawable_producto2, String nombre_producto2, String id_producto2) {
        this.monto = monto2;
        this.drawable_producto = drawable_producto2;
        this.nombre_producto = nombre_producto2;
        this.id_producto = id_producto2;
    }

    public Producto(Parcel in) {
        this.monto = in.readInt();
        this.drawrable_foto = in.readInt();
        this.nombre_producto = in.readString();
        this.id_producto = in.readString();
        this.tipo_producto = in.readString();
        this.tag = in.readString();
        this.drawrable_foto_des = in.readInt();
        this.id_combo = in.readString();
        this.es_combo = in.readString();
    }

    public int getMonto() {
        return this.monto;
    }

    public void setMonto(int monto2) {
        this.monto = monto2;
    }

    public int getDrawrable_foto() {
        return this.drawrable_foto;
    }

    public void setDrawrable_foto(int drawrable_foto2) {
        this.drawrable_foto = drawrable_foto2;
    }

    public String getNombre_producto() {
        return this.nombre_producto;
    }

    public void setNombre_producto(String nombre_producto2) {
        this.nombre_producto = nombre_producto2;
    }

    public Drawable getDrawable_producto() {
        return this.drawable_producto;
    }

    public void setDrawable_producto(Drawable drawable_producto2) {
        this.drawable_producto = drawable_producto2;
    }

    public String getId_producto() {
        return this.id_producto;
    }

    public void setId_producto(String id_producto2) {
        this.id_producto = id_producto2;
    }

    public String getRuta_foto() {
        return this.ruta_foto;
    }

    public void setRuta_foto(String ruta_foto2) {
        this.ruta_foto = ruta_foto2;
    }

    public String getTipo_producto() {
        return this.tipo_producto;
    }

    public void setTipo_producto(String tipo_producto2) {
        this.tipo_producto = tipo_producto2;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag2) {
        this.tag = tag2;
    }

    public int getDrawrable_foto_des() {
        return this.drawrable_foto_des;
    }

    public void setDrawrable_foto_des(int drawrable_foto_des2) {
        this.drawrable_foto_des = drawrable_foto_des2;
    }

    public String getId_combo() {
        return this.id_combo;
    }

    public void setId_combo(String id_combo2) {
        this.id_combo = id_combo2;
    }

    public String getEs_combo() {
        return this.es_combo;
    }

    public void setEs_combo(String es_combo2) {
        this.es_combo = es_combo2;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.monto);
        dest.writeInt(this.drawrable_foto);
        dest.writeString(this.nombre_producto);
        dest.writeString(this.id_producto);
        dest.writeString(this.tipo_producto);
        dest.writeString(this.tag);
        dest.writeInt(this.drawrable_foto_des);
        dest.writeString(this.id_combo);
        dest.writeString(this.es_combo);
    }
}
