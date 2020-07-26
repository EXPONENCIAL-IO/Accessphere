package p004cl.android.poc.cashless.clases;

import java.text.NumberFormat;
import java.util.Locale;

/* renamed from: cl.android.poc.cashless.clases.Utiles */
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
