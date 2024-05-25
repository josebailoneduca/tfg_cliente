package josebailon.ensayos.cliente.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.io.File;

import josebailon.ensayos.cliente.MainActivity;
import josebailon.ensayos.cliente.R;
import josebailon.ensayos.cliente.databinding.FragmentVerNotaBinding;
import josebailon.ensayos.cliente.model.database.entity.CancionEntity;
import josebailon.ensayos.cliente.model.database.relation.NotaAndAudio;
import josebailon.ensayos.cliente.model.grabacion.Reproductor;
import josebailon.ensayos.cliente.model.grabacion.ReproductorImpl;
import josebailon.ensayos.cliente.viewmodel.VerNotaViewModel;

public class VerNotaFragment extends Fragment {

    private FragmentVerNotaBinding binding;
    private VerNotaViewModel viewModel;
    private NotaAndAudio notaAndAudio;
    private CancionEntity cancion;
    private Reproductor reproductor;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);


        binding = FragmentVerNotaBinding.inflate(inflater, container, false);
        // Inicialización del ViewModel
        viewModel = new ViewModelProvider(this).get(VerNotaViewModel.class);

        //recoger id nota
        viewModel.setIdNota(getArguments().getString("idnota"));

        // Callback manejo boton atras
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                manejarBotonAtras();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        //escuchar mensajes
        viewModel.getMensaje().observe(getViewLifecycleOwner(), mensaje -> toast(mensaje.toString()));

        //escucha de descarga
        viewModel.getDescargando().observe(getViewLifecycleOwner(), descargando -> {
            binding.descargandoBar.setVisibility(descargando ? View.VISIBLE : View.INVISIBLE);
            boolean necesitaDescargar = notaAndAudio != null//hay nota/audio
                    && notaAndAudio.audio != null//tiene audio
                    && !viewModel.existeArchivo()//no existe el archivo
                    && !descargando;//no se esta descargando
            binding.btnDescargaAudio.setVisibility((necesitaDescargar) ? View.VISIBLE : View.INVISIBLE);
            if (!descargando)
                actualizarBotones();
        });

        //recoger nota de bd
        viewModel.getNotaAndAudio().observe(getViewLifecycleOwner(), datos -> {
            binding.inputNombre.setText(datos.nota.getNombre());
            binding.inputTexto.setText(datos.nota.getTexto());
            binding.fechaNota.setText(datos.nota.fechaFormateada());
            notaAndAudio = datos;
            if (datos.audio != null && !datos.audio.isBorrado())
                binding.fechaAudio.setText("Fecha del audio: " + datos.audio.fechaFormateada());
            //refrescar botones
            actualizarBotones();
        });

        viewModel.getCancion().observe(getViewLifecycleOwner(), datos -> {
            if (datos != null)
                cancion = datos;
        });

        return binding.getRoot();
    }

    private void actualizarBotones() {
        if (notaAndAudio == null)
            return;
        if (notaAndAudio.audio == null || notaAndAudio.audio.isBorrado()) {
            binding.btnEscuchaAudio.setVisibility(View.INVISIBLE);
            //con audio
        } else {
            binding.btnEscuchaAudio.setVisibility(viewModel.existeArchivo() ? View.VISIBLE : View.INVISIBLE);
            binding.btnDescargaAudio.setVisibility(!viewModel.existeArchivo() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ocultarMenuAcciones();

        //LISTENERS
        //reproducir audio
        binding.btnEscuchaAudio.setOnClickListener(v -> {
            escucharAudio();
        });
        //parar audio
        binding.btnStop.setOnClickListener(v -> {
            pararAudio();
        });
        //descarga audio
        binding.btnDescargaAudio.setOnClickListener(v -> viewModel.descargarAudio());

    }

    private void escucharAudio() {
        String ruta = viewModel.getRutaAudio();
        if (!viewModel.existeArchivo()) {
            toast("El archivo de sonido no se encuentra");
            return;
        }

        reproductor = new ReproductorImpl();
        reproductor.definirVistaParaMc(getContext(), binding.mediaControl, () -> {
            pararAudio();
        });
        try {
            reproductor.iniciar(new File(ruta));
            binding.btnEscuchaAudio.setVisibility(View.INVISIBLE);
            binding.btnStop.setVisibility(View.VISIBLE);
        } catch (Exception ex) {
            toast(ex.getMessage());
        }


    }

    private void pararAudio() {
        if (reproductor != null) {
            reproductor.parar();
            binding.btnEscuchaAudio.setVisibility(View.VISIBLE);
            binding.btnStop.setVisibility(View.INVISIBLE);
        }
    }

    private void manejarBotonAtras() {
        if (reproductor != null)
            reproductor.parar();
        NavHostFragment.findNavController(this).popBackStack();
    }


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

    private void ocultarMenuAcciones() {
        getActivity().addMenuProvider(new MenuProvider() {

            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_share, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String texto = (cancion!=null)?cancion.getNombre()+"\n":"";
                texto += notaAndAudio.nota.getNombre() + "\n" + notaAndAudio.nota.getTexto();
                sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
                if (notaAndAudio.audio != null) {
                    sendIntent.putExtra(Intent.EXTRA_STREAM, viewModel.getUriDeAudio(notaAndAudio.audio.getArchivo().toString()));
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                return true;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }


}