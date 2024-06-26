package josebailon.ensayos.cliente.model.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import josebailon.ensayos.cliente.App;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import josebailon.ensayos.cliente.R;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Constructor de objeto Retrofit.
 * Establece GSon como conversor de entidades desde y hacia Json y establece el formato de fecha.
 *
 * @author Jose Javier Bailon Ortiz
 */
public class APIBuilder {
    public static Retrofit getBuilder(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return new Retrofit.Builder()
                .baseUrl(App.getContext().getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(new OkHttpClient.Builder().build())
                .build();
    }
}
