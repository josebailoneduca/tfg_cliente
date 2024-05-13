package josebailon.ensayos.cliente.model.database.repository;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import josebailon.ensayos.cliente.App;
import josebailon.ensayos.cliente.model.dto.LoginDto;

public class SharedPreferencesRepo {
    private final String K_EMAIL="email";
    private final String K_PASS="pass";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private static volatile SharedPreferencesRepo instancia = null;

    private SharedPreferencesRepo(Context contexto) {
        this.sharedPref = contexto.getSharedPreferences("sharedpref",Context.MODE_PRIVATE);
        this.editor = sharedPref.edit();
    }

    public static SharedPreferencesRepo getInstance() {
        if (instancia == null) {
            synchronized (SharedPreferencesRepo.class) {
                if (instancia == null) {
                    instancia = new SharedPreferencesRepo(App.getContext());
                }
            }
        }
        return instancia;
    }



    public void saveLogin(String email, String pass) {
        this.save(this.K_EMAIL,email);
        this.save(this.K_PASS,pass);
    }
    public LoginDto readLogin() {
        String email = this.read(this.K_EMAIL,"");
        String pass = this.read(this.K_PASS, "");
        return new LoginDto(email,pass);
    }




    public Map<String, ?> readAll() {
        return sharedPref.getAll();
    }
    public void save(String clave, String valor) {
        editor.putString(clave,valor);
        editor.apply();
    }
    public String read(String clave, String defaultValue) {
        return sharedPref.getString(clave, defaultValue);
    }
    public void save(String clave, int valor) {
        editor.putInt(clave,valor);
        editor.apply();
    }
    public int read(String clave, int defaultValue) {
        return sharedPref.getInt(clave, defaultValue);
    }
    public void save(String clave, Long valor) {
        editor.putLong(clave,valor);
        editor.apply();
    }
    public Long read(String clave, Long defaultValue) {
        return sharedPref.getLong(clave, defaultValue);
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}