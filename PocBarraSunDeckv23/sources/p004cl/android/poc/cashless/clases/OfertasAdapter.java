package p004cl.android.poc.cashless.clases;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.p000v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.util.List;
import p004cl.android.poc.cashless.C0200R;

/* renamed from: cl.android.poc.cashless.clases.OfertasAdapter */
public class OfertasAdapter extends BaseAdapter {
    private List<Producto> items_productos;
    private Context mContext;
    private Integer[] mThumbIds = {Integer.valueOf(C0200R.C0201drawable.piscola_light), Integer.valueOf(C0200R.C0201drawable.piscola_normal), Integer.valueOf(C0200R.C0201drawable.piscola_zero)};

    public OfertasAdapter(Context c) {
        this.mContext = c;
    }

    public OfertasAdapter(Context mContext2, List<Producto> items_productos2) {
        this.mContext = mContext2;
        this.items_productos = items_productos2;
    }

    public int getCount() {
        return this.items_productos.size();
    }

    public Object getItem(int position) {
        return this.items_productos.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        Producto item = (Producto) this.items_productos.get(position);
        if (convertView == null) {
            imageView = new ImageView(this.mContext);
        } else {
            imageView = (ImageView) convertView;
        }
        Log.d("carga_fotos", "Nombre Imagen " + item.getNombre_producto());
        if (item.getMonto() > 0) {
            imageView.setImageBitmap(writeOnDrawable(item.getDrawrable_foto(), Utiles.formatear_a_pesos(item.getMonto())));
        } else if (item.getEs_combo().equalsIgnoreCase("SI")) {
            imageView.setImageBitmap(writeOnDrawableCombo(item.getDrawrable_foto(), " COMBO "));
        } else {
            imageView.setImageResource(item.getDrawrable_foto());
        }
        return imageView;
    }

    public Bitmap writeOnDrawable(int drawableId, String text) {
        Bitmap bm = BitmapFactory.decodeResource(this.mContext.getResources(), drawableId).copy(Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint.setTextSize(28.0f);
        new Canvas(bm).drawText(text, (float) (bm.getWidth() / 6), (float) (bm.getHeight() - (bm.getHeight() / 10)), paint);
        return bm;
    }

    public Bitmap writeOnDrawableCombo(int drawableId, String text) {
        Bitmap bm = BitmapFactory.decodeResource(this.mContext.getResources(), drawableId).copy(Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint.setTextSize(28.0f);
        new Canvas(bm).drawText(text, (float) (bm.getWidth() / 12), (float) (bm.getHeight() - (bm.getHeight() / 10)), paint);
        return bm;
    }
}
