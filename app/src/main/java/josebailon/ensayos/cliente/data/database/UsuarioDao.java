package josebailon.ensayos.cliente.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.data.database.entity.UsuarioEntity;


@Dao
public interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertUsuario(UsuarioEntity usuarioModel);

    @Update
    int updateUsuario(UsuarioEntity usuarioModel);

    @Delete
    void deleteUsuario(UsuarioEntity usuarioModel);

    @Query("SELECT * from usuario ORDER BY email")
    LiveData<List<UsuarioEntity>> getAllUsuarios();

    @Query("SELECT * from usuario where email=:email AND grupo=:grupo ORDER BY email")
    LiveData<UsuarioEntity> getUsuarioByEmailGrupo(String email, UUID grupo);

    @Query("SELECT * from usuario where grupo=:grupo ORDER BY email")
    LiveData<UsuarioEntity> getUsuarioByGrupo( UUID grupo);
}
