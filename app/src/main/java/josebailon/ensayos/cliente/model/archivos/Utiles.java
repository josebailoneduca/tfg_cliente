package josebailon.ensayos.cliente.model.archivos;

import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;

import josebailon.ensayos.cliente.App;

/**
 * Utiles para la gestion de archivos
 *
 * @author Jose Javier Bailon Ortiz
 */
public class Utiles {

    /**
     * Devuelve la extension de una uri
     * @param uri La uri
     * @return La extension
     */
    public static String getExtensionDeUri(Uri uri) {
        String nombre = getNombreDeUri(uri);
        String extension = "";
        int i = nombre.lastIndexOf('.');
        if (i > 0) {
            extension = nombre.substring(i+1);
        }
        return extension;
    }

    /**
     * Devuelve el nombre de una Uri
     * @param uri La uri
     * @return El nombre
     */
    public static String getNombreDeUri(Uri uri){
        String uriString = uri.toString();
        File archivo = new File(uriString);
        String path = archivo.getAbsolutePath();
        String displayName = null;

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = App.getContext().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            Log.i("JJBO","RUTA COMPLETA "+uriString);
            return archivo.getName();
        }
        return "";
    }
}
