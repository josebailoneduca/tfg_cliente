package josebailon.ensayos.cliente.model.sincronizacion;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import josebailon.ensayos.cliente.model.database.entity.AudioEntity;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.NotaEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.network.model.entidades.AudioApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.CancionApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.GrupoApiEnt;
import josebailon.ensayos.cliente.model.network.model.entidades.NotaApiEnt;

/**
 * Mediador entre entidades de Room y Retrofit.
 *
 * @author Jose Javier Bailon Ortiz
 */
public class MediadorDeEntidades {

    /**
     * Transforma un grupo de Retrofit a Room
     * @param grupoApi El grupo como Retrofit Entity
     * @return El grupo como Room Entity
     */
    public static GrupoEntity grupoApiEntToGrupoEntity(GrupoApiEnt grupoApi){
            GrupoEntity g = new GrupoEntity();
            g.setId(UUID.fromString(grupoApi.getId()));
            g.setNombre(grupoApi.getNombre());
            g.setDescripcion(grupoApi.getDescripcion());
            g.setVersion(grupoApi.getVersion());
        try {
            g.setFecha(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(grupoApi.getFecha()));
        } catch (ParseException e) {
            g.setFecha(new Date(System.currentTimeMillis()));
        }
        g.setBorrado(false);
            g.setEditado(false);
            g.setAbandonado(false);
            return g;
    }

    /**
     * Crea una entidad Usuario de room a partir de grupo y email
     * @return El usuario
     */
    public static UsuarioEntity crearUsuarioEntityParaGrupo(String idGrupo, String email){
        UsuarioEntity u = new UsuarioEntity();
        u.setGrupo(UUID.fromString(idGrupo));
        u.setEmail(email);
        return u;
    }

    /**
     * Transforma una cancion de Retrofit a Room
     * @param idGrupo UUID del grupo al que asignar lacancion
     * @param cancionRemota Cancion Retrofit
     *
     * @return El cancion como Room Entity
     */
    public static CancionEntity cancionApiEntToCancionEntity(String idGrupo, CancionApiEnt cancionRemota) {
        CancionEntity c = new CancionEntity();
        c.setId(UUID.fromString(cancionRemota.getId()));
        c.setNombre(cancionRemota.getNombre());
        c.setDescripcion(cancionRemota.getDescripcion());
        c.setDuracion(cancionRemota.getDuracion());
        c.setVersion(cancionRemota.getVersion());
        try {
            c.setFecha(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cancionRemota.getFecha()));
        } catch (ParseException e) {
            c.setFecha(new Date(System.currentTimeMillis()));
        }
        c.setBorrado(false);
        c.setEditado(false);
        c.setGrupo(UUID.fromString(idGrupo));
        return c;
    }

    /**
     * Transfroma una nota retrofit a Room
     * @param idCancion UUID de la cancion a la que asignar la nota
     * @param notaRemota Nota retrofit
     * @return Nota Room
     */
    public static NotaEntity notaApiEntToNotaEntity(String idCancion, NotaApiEnt notaRemota) {
        NotaEntity n = new NotaEntity();
        n.setId(UUID.fromString(notaRemota.getId()));
        n.setNombre(notaRemota.getNombre());
        n.setTexto(notaRemota.getTexto());
        n.setVersion(notaRemota.getVersion());
        try {
            n.setFecha(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(notaRemota.getFecha()));
        } catch (ParseException e) {
            n.setFecha(new Date(System.currentTimeMillis()));
        }
        n.setBorrado(false);
        n.setEditado(false);
        n.setCancion(UUID.fromString(idCancion));
        return n;
    }

    /**
     * Transforma un audio Retrofit a Room
     * @param audioRemoto Audio retrofit
     * @return Audio Room
     */
    public static AudioEntity audioApiEntToAudioEntity(AudioApiEnt audioRemoto) {
        AudioEntity a = new AudioEntity();
        a.setNota_id(UUID.fromString(audioRemoto.getId()));
        a.setArchivo(audioRemoto.getNombreArchivo());
        a.setVersion(audioRemoto.getVersion());
        try {
            a.setFecha(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(audioRemoto.getFecha()));
        } catch (ParseException e) {
            a.setFecha(new Date(System.currentTimeMillis()));
        }
        a.setBorrado(false);
        a.setEditado(false);
        return a;
    }
}
