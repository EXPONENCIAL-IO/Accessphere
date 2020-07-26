package p004cl.android.poc.cashless.clases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import p004cl.android.poc.cashless.C0200R;

/* renamed from: cl.android.poc.cashless.clases.TransaccionesAdapter */
public class TransaccionesAdapter extends BaseAdapter {
    private Context context;
    private List<TransaccionesRealizadas> items = null;

    public TransaccionesAdapter(Context context2, List<TransaccionesRealizadas> items2) {
        this.context = context2;
        this.items = items2;
    }

    public int getCount() {
        return this.items.size();
    }

    public Object getItem(int position) {
        return this.items.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (convertView == null) {
            rowView = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(C0200R.layout.lista_item_trx, parent, false);
        }
        TextView tv_productos = (TextView) rowView.findViewById(C0200R.C0202id.tv_productos);
        TextView tv_monto_compra = (TextView) rowView.findViewById(C0200R.C0202id.tv_monto_compra);
        TextView tv_cant_productos = (TextView) rowView.findViewById(C0200R.C0202id.tv_cant_productos);
        TextView tv_fecha = (TextView) rowView.findViewById(C0200R.C0202id.tv_fecha);
        try {
            TransaccionesRealizadas item = (TransaccionesRealizadas) this.items.get(position);
            ((TextView) rowView.findViewById(C0200R.C0202id.tv_qr)).setText(item.getQr());
            tv_productos.setText(item.getProductos().replace("--", "\n"));
            tv_monto_compra.setText(Utiles.formatear_a_pesos(item.getMonto_compra()));
            tv_cant_productos.setText(item.getCant_productos() + "");
            tv_fecha.setText(item.getFecha());
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
        }
        return rowView;
    }

    public void clearData() {
        this.items.clear();
    }
}
