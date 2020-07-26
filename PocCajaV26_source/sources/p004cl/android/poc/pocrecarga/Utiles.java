package p004cl.android.poc.pocrecarga;

import java.text.NumberFormat;
import java.util.Locale;

/* renamed from: cl.android.poc.pocrecarga.Utiles */
public class Utiles {
    public static String formatear_a_pesos(int monto) {
        String str = "0";
        try {
            return NumberFormat.getCurrencyInstance(new Locale("es", "CL")).format((long) monto);
        } catch (Exception e) {
            return "0";
        }
    }
}
