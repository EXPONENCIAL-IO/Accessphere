package p004cl.android.poc.pocrecarga;

/* renamed from: cl.android.poc.pocrecarga.Rut */
public class Rut {
    public static String formatear(String rut) {
        int cont = 0;
        if (rut.length() == 0) {
            return "";
        }
        if (rut.length() == 1) {
            return rut;
        }
        String rut2 = rut.replace(".", "").replace("-", "");
        String format = "-" + rut2.substring(rut2.length() - 1);
        for (int i = rut2.length() - 2; i >= 0; i--) {
            format = rut2.substring(i, i + 1) + format;
            cont++;
            if (cont == 3 && i != 0) {
                format = "." + format;
                cont = 0;
            }
        }
        return format;
    }

    public static String formatear_2(String rut) {
        int cont = 0;
        String format = "";
        String rut2 = rut.replace(".", "").replace("-", "");
        for (int i = rut2.length() - 2; i >= 0; i--) {
            format = rut2.substring(i, i + 1) + format;
            cont++;
            if (cont == 3 && i != 0) {
                format = "." + format;
                cont = 0;
            }
        }
        return format;
    }

    public static boolean validar(String rut) {
        try {
            String rut2 = rut.toUpperCase().replace(".", "").replace("-", "");
            int rutAux = Integer.parseInt(rut2.substring(0, rut2.length() - 1));
            char dv = rut2.charAt(rut2.length() - 1);
            int s = 1;
            int m = 0;
            while (rutAux != 0) {
                s = (((rutAux % 10) * (9 - (m % 6))) + s) % 11;
                rutAux /= 10;
                m++;
            }
            if (dv == ((char) (s != 0 ? s + 47 : 75))) {
                return true;
            }
            return false;
        } catch (Exception | NumberFormatException e) {
            return false;
        }
    }
}
