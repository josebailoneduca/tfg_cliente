package josebailon.ensayos.cliente.model.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Entidad RetroFit para la el envio de peticion de login y registro
 *
 * @author Jose Javier Bailon Ortiz
 */
public class LoginRequest {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
