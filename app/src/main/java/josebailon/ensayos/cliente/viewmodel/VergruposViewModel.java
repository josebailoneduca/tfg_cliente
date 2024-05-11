package josebailon.ensayos.cliente.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.model.database.repository.SharedPreferencesRepo;
import josebailon.ensayos.cliente.model.database.service.DatosLocalesServicio;
import josebailon.ensayos.cliente.model.database.service.impl.DatosLocalesAsincronos;

public class VergruposViewModel extends ViewModel {

    private DatosLocalesServicio servicio = new DatosLocalesAsincronos();
    private SharedPreferencesRepo sharedRepo = SharedPreferencesRepo.getInstance();
    public void crear(String nombre, String descripcion) {
        GrupoEntity g = new GrupoEntity();
        g.setId(UUID.randomUUID());
        g.setNombre(nombre);
        g.setDescripcion(descripcion);
        g.setVersion(0);


        UsuarioEntity u = new UsuarioEntity();
        u.setEmail(sharedRepo.readLogin().getEmail());
        u.setGrupo(g.getId());
        servicio.insertGrupoUsuario(g,u);
    }
    public LiveData<List<GrupoEntity>> getGrupos() {
        return servicio.getAllGrupos();
    }

    public void actualizar(GrupoEntity grupo, String nombre, String descripcion) {
        grupo.setNombre(nombre);
        grupo.setDescripcion(descripcion);
        grupo.setEditado(true);
        servicio.updateGrupo(grupo);
    }

    public void borrar(GrupoEntity grupo) {
        grupo.setBorrado(true);
        grupo.setEditado(true);
        if(grupo.getVersion()==0)
            servicio.deleteGrupo(grupo);
        else
            servicio.borrardoLogicoGrupo(grupo);
    }
}