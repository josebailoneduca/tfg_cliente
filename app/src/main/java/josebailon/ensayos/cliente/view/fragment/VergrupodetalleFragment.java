package josebailon.ensayos.cliente.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import josebailon.ensayos.cliente.MainActivity;
import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.model.database.entity.UsuarioEntity;
import josebailon.ensayos.cliente.databinding.FragmentVergrupoDetalleBinding;
import josebailon.ensayos.cliente.view.adapter.CancionesAdapter;
import josebailon.ensayos.cliente.view.adapter.UsuariosAdapter;
import josebailon.ensayos.cliente.view.dialogos.DialogoEditarCancion;
import josebailon.ensayos.cliente.view.dialogos.DialogoEditarGrupo;
import josebailon.ensayos.cliente.viewmodel.VergrupodetalleViewModel;

/**
 * Fragment de vista de detalle de un grupo
 *
 * @author Jose Javier Bailon Ortiz
 */
public class VergrupodetalleFragment extends Fragment {



    private FragmentVergrupoDetalleBinding binding;
    private VergrupodetalleViewModel viewModel;

    private CancionesAdapter adaptadorCanciones;
    private UsuariosAdapter adaptadorUsuarios;
    private List<CancionEntity> cancionesActuales=null;
    private List<UsuarioEntity> usuariosActuales =null;

    private UUID idgrupo;
    private GrupoEntity grupo;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentVergrupoDetalleBinding.inflate(inflater, container, false);
        //recoger id grupo
        idgrupo = UUID.fromString(getArguments().getString("idgrupo"));

        //recycler de canciones
        RecyclerView cancionesRecyclerView = binding.verCancionesRecycleView;
        cancionesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptadorCanciones = new CancionesAdapter(new ArrayList<CancionEntity>(),this);
        cancionesRecyclerView.setAdapter(adaptadorCanciones);
        //recycler de usuarios
        RecyclerView usuariosRecyclerView = binding.verUsuariosRecycleView;
        usuariosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptadorUsuarios = new UsuariosAdapter(new ArrayList<UsuarioEntity>(),this);
        usuariosRecyclerView.setAdapter(adaptadorUsuarios);


        // Inicialización del ViewModel
        viewModel = new ViewModelProvider(this).get(VergrupodetalleViewModel.class);
        //escuchar mensajes
        viewModel.getMensaje().observe(getViewLifecycleOwner(),mensaje -> {
            if (!TextUtils.isEmpty(mensaje)) {
                toast(mensaje.toString());
                viewModel.lipiarMensaje();
            }
        });

        //recoger datos
        viewModel.getGrupo(idgrupo).observe(getViewLifecycleOwner(), datos ->{
            if(datos==null) {
                NavHostFragment.findNavController(this).popBackStack();
                return;
            }
            //refrescar nombre
            this.grupo=datos.grupo;
            binding.lbNombre.setText(datos.grupo.getNombre());
            binding.lbDescripcion.setText(datos.grupo.getDescripcion());
            viewModel.setGrupoId(datos.grupo.getId());
            //refrescar canciones
            cancionesActuales=datos.getCancionesOrdenadas().stream().filter(cancionEntity -> !cancionEntity.isBorrado()).collect(Collectors.toList());
            adaptadorCanciones.setData(cancionesActuales);
            //refrescar usuarios
            adaptadorUsuarios.setData(datos.getUsuariosOrdenados());
            usuariosActuales =datos.getUsuariosOrdenados();
            }
        );

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnAgregarCancion.setOnClickListener(v -> mostrarDialogoCrearCancion());
        binding.btnAgregarUsuario.setOnClickListener(v -> mostrarDialogoAgregarUsuario());
        mostrarMenuSuperior();
 }


    /**
     * Muestra el dialogo para agregar un usuario al grupo
     */
    private void mostrarDialogoAgregarUsuario() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialogo_agregar_usuario);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ((Button) (dialog.findViewById(R.id.btnAceptar))).setOnClickListener(v -> {
            String email = ((EditText) (dialog.findViewById(R.id.inputEmail))).getText().toString();

            if (TextUtils.isEmpty(email))
                toast("El email no puede estar vacío");
            else {
                //guardar Usuario
                viewModel.agregarUsuario(email,grupo);
                dialog.dismiss();
            }
        });
        ((Button) (dialog.findViewById(R.id.btnCancelar))).setOnClickListener(v -> dialog.dismiss());
    }


    /**
     * Muestra el dialogo de creacion de una cancion
     */
    private void mostrarDialogoCrearCancion() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialogo_crear_cancion);
        dialog.show();
        Window window = dialog.getWindow();
        ((TextView)window.findViewById(R.id.tituloventana)).setText("Agregar Canción");
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ((Button) (dialog.findViewById(R.id.btnAceptar))).setOnClickListener(v -> {
            String nombre = ((EditText) (dialog.findViewById(R.id.inputEmail))).getText().toString();
            String descripcion = ((EditText) (dialog.findViewById(R.id.inputDescripcion))).getText().toString();
            String duracion = ((EditText) (dialog.findViewById(R.id.inputDuracion))).getText().toString();

            if (TextUtils.isEmpty(nombre))
                toast("El nombre no puede estar vacío");
            else {
                //guardar Cancion
                viewModel.crearCancion(nombre,  descripcion, duracion, idgrupo);
                dialog.dismiss();
            }
        });
        ((Button) (dialog.findViewById(R.id.btnCancelar))).setOnClickListener(v -> dialog.dismiss());

    }

    /**
     * Muestara el dialogo para editar una cancion
     * @param cancion La cancion a editar
     */
    private void mostrarDialogoEdicionCancion(CancionEntity cancion) {
        DialogoEditarCancion d = new DialogoEditarCancion(getContext(),cancion);
        d.mostrar(v -> {
            String nombre = d.getNombre();
            String descripcion = d.getDescripcion();
            String duracion = d.getDuracion();
            if (TextUtils.isEmpty(nombre))
                toast("El nombre no puede estar vacío");
            else {
                //guardar CANCION
                viewModel.actualizarCancion(cancion,nombre,descripcion,duracion);
                d.dismiss();
            }
        });
    }

    /**
     * Toast de mensaje
     * @param msg
     */
    private void toast(String msg) {
        Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((MainActivity) requireActivity()).getSupportActionBar() != null) {
            ((MainActivity) requireActivity()).getSupportActionBar().show();
        }
    }

    /**
     * Muestra el menu contextual de una cancion
     * @param position La posicion
     * @return true si se ha manejado
     */
    public boolean mostrarMenuCancion(int position) {
        PopupMenu popupMenu = new PopupMenu(getContext() , binding.verCancionesRecycleView.getChildAt(position).findViewById(R.id.nombre));
        // add the menu
        popupMenu.inflate(R.menu.contextmenu);
        // implement on menu item click Listener
        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId()==R.id.itemEditar){
                mostrarDialogoEdicionCancion(cancionesActuales.get(position));
            }
            else if (item.getItemId()==R.id.itemEliminar){
                borrarCancion(cancionesActuales.get(position));
            }
            return true;
        });
        popupMenu.show();
        return true;
    }


    /**
     * Muestra el menu contextual de un usuario
     * @param position La posicion
     * @return true si se ha manejado
     */
    public boolean mostrarMenuUsuario(int position) {
        PopupMenu popupMenu = new PopupMenu(getContext() , binding.verUsuariosRecycleView.getChildAt(position).findViewById(R.id.nombre));
        // add the menu
        popupMenu.inflate(R.menu.contextmenu_delete);
        // implement on menu item click Listener
        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId()==R.id.itemEliminar){
                borrarUsuario(usuariosActuales.get(position));
            }
            return true;
        });
        popupMenu.show();
        return true;
    }


    /**
     * Borra una cancion
     * @param cancionEntity
     */
    private void borrarCancion(CancionEntity cancionEntity) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminación")
                .setMessage("¿Quieres borrar la cancion "+cancionEntity.getNombre()+"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("SI",(dialog, which) -> {
                    viewModel.borrarCancion(cancionEntity);
                })
                .setNegativeButton("NO", null)
                .show();
    }

    /**
     * Borra un usuario
     * @param usuarioEntity
     */
    private void borrarUsuario(UsuarioEntity usuarioEntity) {
        //abandonar grupo
        if (usuarioEntity.getEmail().equals(viewModel.getUsuario())) {

            new AlertDialog.Builder(getContext())
                    .setTitle("Abandonar el grupo")
                    .setMessage("¿Quieres abandonar el grupo?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("SI", (dialog, which) -> {
                       viewModel.abandonarGrupo(usuarioEntity,grupo);
                        NavHostFragment.findNavController(VergrupodetalleFragment.this).popBackStack();
                    })
                    .setNegativeButton("NO", null)
                    .show();
        }
        else{
            //eliminar usuario del grupo
            new AlertDialog.Builder(getContext())
                    .setTitle("Eliminación")
                    .setMessage("¿Quieres eliminar el usuario " + usuarioEntity.getEmail() + " de este grupo?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("SI", (dialog, which) -> {
                        viewModel.borrarUsuario(usuarioEntity,grupo);
                    })
                    .setNegativeButton("NO", null)
                    .show();
        }
    }

    /**
     * Navega a la vista detalle de una cancion
     * @param id
     */
    public void verCancion(UUID id) {
        String uuid = id.toString();
        Bundle bundle = new Bundle();
        bundle.putString("idcancion", uuid);
        NavHostFragment.findNavController(VergrupodetalleFragment.this)
                .navigate(R.id.action_vergrupodetalleFragment_to_vercanciondetalleFragment,bundle);
    }



    /**
     * Muestra el menu de acciones
     */
    private void mostrarMenuSuperior() {
        getActivity().addMenuProvider( new MenuProvider(){

            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_edit_sincro, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id==R.id.action_editar)
                    dialogoEditarGrupo();
                return true;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

    }

    private void dialogoEditarGrupo() {
        DialogoEditarGrupo d = new DialogoEditarGrupo(getContext(),grupo);
        d.mostrar(v -> {
       if (TextUtils.isEmpty(d.getNombre()))
                toast("El nombre no puede estar vacío");
            else {
                //guardar GRUPO
                viewModel.actualizarGrupo(grupo, d.getNombre(), d.getDescripcion());
                d.dismiss();
            }
        });
    }
}