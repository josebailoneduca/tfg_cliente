package josebailon.ensayos.cliente.view.fragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.graphics.drawable.Animatable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.ContextMenu;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import josebailon.ensayos.cliente.MainActivity;
import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.model.database.entity.GrupoEntity;
import josebailon.ensayos.cliente.databinding.FragmentVergruposBinding;
import josebailon.ensayos.cliente.view.adapter.GruposAdapter;
import josebailon.ensayos.cliente.view.dialogos.DialogoEditarGrupo;
import josebailon.ensayos.cliente.viewmodel.VergruposViewModel;

/**
 * Fragmen de visualizacion de grupos
 *
 * @author Jose Javier Bailon Ortiz
 */
public class VergruposFragment extends Fragment {

    private FragmentVergruposBinding binding;
    private VergruposViewModel viewModel;
    private RecyclerView gruposRecyclerView;
    private GruposAdapter adaptador;
    private List<GrupoEntity> gruposActuales = null;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentVergruposBinding.inflate(inflater, container, false);

        gruposRecyclerView = binding.verGruposRecycleView;
        gruposRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptador = new GruposAdapter(new ArrayList<GrupoEntity>(), this);
        gruposRecyclerView.setAdapter(adaptador);
        // Inicialización del ViewModel
        viewModel = new ViewModelProvider(this).get(VergruposViewModel.class);
        viewModel.getGrupos().observe(getViewLifecycleOwner(), grupos -> {
            adaptador.setData(grupos);
            gruposActuales = grupos;
        });
        viewModel.getUsuario().observe(getViewLifecycleOwner(), s -> binding.lbUsuarioActual.setText(s));

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnAgregarGrupo.setOnClickListener(v -> mostrarDialogoCreacion());
        mostrarMenuSuperior();
        registerForContextMenu(gruposRecyclerView);


        ((Animatable) binding.imageView6.getDrawable()).start();


    }

    /**
     * Mostrar el menu de acciones
     */
    private void mostrarMenuSuperior() {
        getActivity().addMenuProvider(new MenuProvider() {

            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_sincro, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

    }


    /**
     * Muestra el dialogo de crear una cancion
     */
    private void mostrarDialogoCreacion() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialogo_crear_grupo);
        dialog.show();
        Window window = dialog.getWindow();
        ((TextView) window.findViewById(R.id.tituloventana)).setText("Agregar Grupo");
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ((Button) (dialog.findViewById(R.id.btnAceptar))).setOnClickListener(v -> {
            String nombre = ((EditText) (dialog.findViewById(R.id.inputEmail))).getText().toString();
            String descripcion = ((EditText) (dialog.findViewById(R.id.inputDescripcion))).getText().toString();

            if (TextUtils.isEmpty(nombre))
                toast("El nombre no puede estar vacío");
            else {
                //guardar GRUPO
                viewModel.crear(nombre, descripcion);
                dialog.dismiss();
            }
        });
        ((Button) (dialog.findViewById(R.id.btnCancelar))).setOnClickListener(v -> dialog.dismiss());

    }

    /**
     * Toast de mensaje
     *
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
     * Navega a la vista detalle de un grupo
     *
     * @param id Id del grupo
     */
    public void verGrupo(UUID id) {
        String uuid = id.toString();
        Bundle bundle = new Bundle();
        bundle.putString("idgrupo", uuid);
        NavHostFragment.findNavController(VergruposFragment.this)
                .navigate(R.id.action_vergruposFragment_to_vergrupodetalleFragment, bundle);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.contextmenu, menu);
    }


    /**
     * Muestra el menu contextual de un grupo
     *
     * @param position La pisicion
     * @return true si se ha manejado
     */
    public boolean mostrarMenu(int position) {
        PopupMenu popupMenu = new PopupMenu(getContext(), binding.verGruposRecycleView.getChildAt(position).findViewById(R.id.nombre));
        // add the menu
        popupMenu.inflate(R.menu.contextmenu);
        // implement on menu item click Listener
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.itemEditar) {
                mostrarDialogoEdicion(gruposActuales.get(position));
            } else if (item.getItemId() == R.id.itemEliminar) {
                borrar(gruposActuales.get(position));
            }
            return true;
        });
        popupMenu.show();
        return true;
    }

    /**
     * Borrar un grupo
     * @param grupoEntity
     */
    private void borrar(GrupoEntity grupoEntity) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminación")
                .setMessage("¿Quieres borrar el grupo " + grupoEntity.getNombre() + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("SI", (dialog, which) -> {
                    viewModel.borrar(grupoEntity);
                })
                .setNegativeButton("NO", null)
                .show();
    }


    /**
     * Mostrar el dialogo de edicion de un grupo
     * @param grupo
     */
    private void mostrarDialogoEdicion(GrupoEntity grupo) {
        DialogoEditarGrupo d = new DialogoEditarGrupo(getContext(),grupo);
        d.mostrar(v -> {
            if (TextUtils.isEmpty(d.getNombre()))
                toast("El nombre no puede estar vacío");
            else {
                //guardar GRUPO
                viewModel.actualizar(grupo, d.getNombre(), d.getDescripcion());
                d.dismiss();
            }
        });
    }
}